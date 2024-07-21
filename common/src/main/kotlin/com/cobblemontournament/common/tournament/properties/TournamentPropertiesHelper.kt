package com.cobblemontournament.common.tournament.properties

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.api.MatchManager
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.api.storage.MatchStore
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.api.storage.PlayerStore
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.util.ChatUtil
import com.someguy.storage.classstored.ClassStoredUtil.shallowCopy
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object TournamentPropertiesHelper: PropertiesHelper <TournamentProperties>
{
    const val DEFAULT_TOURNAMENT_NAME = "Default-Tournament-Name"
    val DEFAULT_TOURNAMENT_STATUS = TournamentStatus.UNKNOWN

    override fun deepCopyHelper(
        properties: TournamentProperties
    ): TournamentProperties
    {
        return TournamentProperties(
            name                = properties.name,
            tournamentID        = properties.tournamentID,
            tournamentStatus    = properties.tournamentStatus,
            tournamentType      = properties.tournamentType,
            challengeFormat     = properties.challengeFormat,
            maxParticipants     = properties.maxParticipants,
            teamSize            = properties.teamSize,
            groupSize           = properties.groupSize,
            minLevel            = properties.minLevel,
            maxLevel            = properties.maxLevel,
            showPreview         = properties.showPreview,
            rounds              = properties.rounds.shallowCopy(),
            matches             = properties.matches.shallowCopy(),
            players             = properties.players.shallowCopy())
    }

    @Suppress("DuplicatedCode")
    override fun setFromPropertiesHelper(
        mutable: TournamentProperties,
        from: TournamentProperties
    ): TournamentProperties
    {
        mutable.name              = from.name
        mutable.tournamentID      = from.tournamentID
        mutable.tournamentStatus  = from.tournamentStatus
        mutable.tournamentType    = from.tournamentType
        mutable.challengeFormat   = from.challengeFormat
        mutable.teamSize          = from.teamSize
        mutable.groupSize         = from.groupSize
        mutable.maxParticipants   = from.maxParticipants
        mutable.minLevel          = from.minLevel
        mutable.maxLevel          = from.maxLevel
        mutable.showPreview       = from.showPreview
        mutable.rounds            = from.rounds.shallowCopy()
        mutable.matches           = from.matches.shallowCopy()
        mutable.players           = from.players.shallowCopy()
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: TournamentProperties,
        nbt: CompoundTag,
    ): TournamentProperties
    {
        val tournamentID            = nbt.getUUID(      DataKeys.TOURNAMENT_ID)
        mutable.name                = nbt.getString(    DataKeys.TOURNAMENT_NAME)
        mutable.tournamentID        = tournamentID
        mutable.tournamentStatus    = enumValueOf<TournamentStatus>( nbt.getString( DataKeys.TOURNAMENT_STATUS ) )
        mutable.tournamentType      = enumValueOf<TournamentType>( nbt.getString( DataKeys.TOURNAMENT_TYPE ) )
        mutable.challengeFormat     = enumValueOf<ChallengeFormat>( nbt.getString( DataKeys.CHALLENGE_FORMAT ) )
        mutable.teamSize            = nbt.getInt(       DataKeys.TEAM_SIZE )
        mutable.groupSize           = nbt.getInt(       DataKeys.GROUP_SIZE )
        mutable.maxParticipants     = nbt.getInt(       DataKeys.MAX_PARTICIPANTS )
        mutable.minLevel            = nbt.getInt(       DataKeys.MIN_LEVEL )
        mutable.maxLevel            = nbt.getInt(       DataKeys.MAX_LEVEL )
        mutable.showPreview         = nbt.getBoolean(   DataKeys.SHOW_PREVIEW )
        mutable.rounds              = loadRoundData(  nbt.getCompound( DataKeys.ROUND_MAP ) )
        mutable.matches             = loadMatchData(  tournamentID )
        mutable.players             = loadPlayerData( tournamentID )
        return mutable
    }

    override fun saveToNBTHelper(
        properties: TournamentProperties,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putString(  DataKeys.TOURNAMENT_NAME    , properties.name )
        nbt.putUUID(    DataKeys.TOURNAMENT_ID      , properties.tournamentID )
        nbt.putString(  DataKeys.TOURNAMENT_STATUS  , properties.tournamentStatus.name )
        nbt.putString(  DataKeys.TOURNAMENT_TYPE    , properties.tournamentType.name )
        nbt.putString(  DataKeys.CHALLENGE_FORMAT   , properties.challengeFormat.name )
        nbt.putInt(     DataKeys.MAX_PARTICIPANTS   , properties.maxParticipants )
        nbt.putInt(     DataKeys.TEAM_SIZE          , properties.teamSize )
        nbt.putInt(     DataKeys.GROUP_SIZE         , properties.groupSize )
        nbt.putInt(     DataKeys.MIN_LEVEL          , properties.minLevel )
        nbt.putInt(     DataKeys.MAX_LEVEL          , properties.maxLevel )
        nbt.putBoolean( DataKeys.SHOW_PREVIEW       , properties.showPreview )
        nbt.put( DataKeys.ROUND_MAP,  saveRoundData(  properties.rounds, CompoundTag() ) )
        return nbt
    }

    override fun loadFromNBTHelper(
        nbt: CompoundTag
    ): TournamentProperties
    {
        val tournamentID = nbt.getUUID( DataKeys.TOURNAMENT_ID)
        return TournamentProperties(
            name                = nbt.getString(    DataKeys.TOURNAMENT_NAME ),
            tournamentID        = tournamentID,
            tournamentStatus    = enumValueOf<TournamentStatus>( nbt.getString( DataKeys.TOURNAMENT_STATUS ) ),
            tournamentType      = enumValueOf<TournamentType>( nbt.getString( DataKeys.TOURNAMENT_TYPE ) ),
            challengeFormat     = enumValueOf<ChallengeFormat>( nbt.getString( DataKeys.CHALLENGE_FORMAT ) ),
            teamSize            = nbt.getInt(       DataKeys.TEAM_SIZE ),
            groupSize           = nbt.getInt(       DataKeys.GROUP_SIZE ),
            maxParticipants     = nbt.getInt(       DataKeys.MAX_PARTICIPANTS ),
            minLevel            = nbt.getInt(       DataKeys.MIN_LEVEL ),
            maxLevel            = nbt.getInt(       DataKeys.MAX_LEVEL ),
            showPreview         = nbt.getBoolean(   DataKeys.SHOW_PREVIEW ),
            rounds              = loadRoundData(  nbt.getCompound( DataKeys.ROUND_MAP ) ),
            matches             = loadMatchData(  tournamentID ),
            players             = loadPlayerData( tournamentID ) )
    }

    private fun saveRoundData(
        rounds: Map <UUID,TournamentRound>,
        nbt: CompoundTag
    ): CompoundTag
    {
        var size = 0
        rounds.forEach {
            nbt.put( DataKeys.ROUND_DATA + size++, it.value.saveToNBT( CompoundTag() ) )
        }
        nbt.putInt( StoreDataKeys.SIZE, size )
        return nbt
    }

    private fun loadRoundData(
        nbt: CompoundTag
    ): MutableMap <UUID,TournamentRound>
    {
        val map   = mutableMapOf <UUID,TournamentRound>()
        val size  = nbt.getInt( StoreDataKeys.SIZE )
        for (i in 0 until size) {
            val round = TournamentRound.loadFromNBT( nbt.getCompound( DataKeys.ROUND_DATA + i ) )
            map[round.uuid] = round
        }
        return map
    }

    private fun loadMatchData(
        tournamentID: UUID
    ): MutableMap <UUID,TournamentMatch>
    {
        val map = mutableMapOf <UUID,TournamentMatch>()
        for ( match in TournamentStoreManager.getStoreIterator( MatchStore::class.java, tournamentID ) ) {
            map[match.uuid] = match
        }
        return map
    }

    private fun loadPlayerData(
        tournamentID: UUID
    ): MutableMap <UUID,TournamentPlayer>
    {
        val map = mutableMapOf <UUID,TournamentPlayer>()
        for ( player in TournamentStoreManager.getStoreIterator( PlayerStore::class.java, tournamentID ) ) {
            map[player.uuid] = player
        }
        return map
    }

    override fun logDebugHelper( properties: TournamentProperties )
    {
        Util.report( "Tournament \"${properties.name}\" [${ChatUtil.shortUUID( properties.tournamentID )}]" )
        Util.report( "- ${properties.tournamentType} [${properties.challengeFormat}]" )
        Util.report( "- Max Participants: ${properties.maxParticipants}" )
        Util.report( "- Team Size (${properties.teamSize}) - Group Size (${properties.groupSize})" )
        Util.report( "- Level Range [Min: ${properties.minLevel}, Max: ${properties.maxLevel}]" )
        Util.report( "- Show Preview: ${properties.showPreview}")
        if ( properties.rounds.isNotEmpty() || properties.matches.isNotEmpty() ) {
            Util.report( "- Rounds (${properties.rounds.size}) - Matches (${properties.matches.size})" )
        }
        if ( properties.players.isNotEmpty() ) {
            Util.report( "  Players ${properties.players.size}:" )
            properties.players.forEach {
                Util.report("  - ${it.value} [${ChatUtil.shortUUID( it.key )}]" )
            }
        }
    }

    override fun displayInChatHelper(
        properties: TournamentProperties,
        player: ServerPlayer )
    {
        displayTitleInChatHelper( properties, player )
        displaySlimInChatHelper( properties, player )

        if ( properties.rounds.isNotEmpty() || properties.matches.isNotEmpty() ) {
            val text = ChatUtil.formatText(
                text    = "  Rounds ",
                color   = ChatUtil.yellow,
                bold    = true )
            text.append( ChatUtil.formatTextBracketed(
                text = "${properties.rounds.size}",
                color = ChatUtil.yellow ) )
            text.append( ChatUtil.formatText( text = " - " ) )
            text.append( ChatUtil.formatText(
                text    = "Matches ",
                color   = ChatUtil.yellow,
                bold    = true ) )
            text.append( ChatUtil.formatTextBracketed(
                text = "${properties.matches.size}",
                color = ChatUtil.yellow ) )
            player.displayClientMessage( text, false )
        }
        if ( properties.players.isNotEmpty() ) {
            val text0 = ChatUtil.formatText(
                text    = "  Players ",
                color   = ChatUtil.aqua,
                bold    = true )
            text0.append( ChatUtil.formatTextBracketed(
                text = "${properties.players.size}",
                color = ChatUtil.aqua ) )
            player.displayClientMessage( text0, false )

            properties.players.forEach {
                val text1 = ChatUtil.formatText( text = "    \"" )
                text1.append( ChatUtil.formatText(
                    text    = it.value.name,
                    color   = ChatUtil.aqua ) )
                text1.append( ChatUtil.formatText( text = "\" " ) )
                text1.append( ChatUtil.formatTextBracketed(
                    text    = ChatUtil.shortUUID( it.key ),
                    color   = ChatUtil.aqua ) )
                player.displayClientMessage( text1, false )
            }
        }
    }

    private fun displayTitleInChatHelper(
        properties  : TournamentProperties,
        player      : ServerPlayer )
    {
        val title = ChatUtil.formatText(
            text    = "Tournament ",
            color   = ChatUtil.green )
        title.append( ChatUtil.formatText( text = "\"" ) )
        title.append( ChatUtil.formatText(
            text    = properties.name,
            color   = ChatUtil.green ) )
        title.append( ChatUtil.formatText( text = "\" " ) )
        title.append( ChatUtil.formatTextBracketed(
            text    = ChatUtil.shortUUID(properties.tournamentID ),
            color   = ChatUtil.green ) )
        title.append( ChatUtil.formatText( text = " " ) )
        title.append( ChatUtil.formatTextBracketed(
            text    = properties.tournamentStatus.name,
            color   = ChatUtil.yellow ) )
        player.displayClientMessage( title, false )
    }

    fun displaySlimInChatHelper(
        properties: TournamentProperties,
        player: ServerPlayer )
    {
        val typeAndFormat = ChatUtil.formatTextBracketed(
            text = "${properties.tournamentType}",
            color = ChatUtil.yellow,
            spacingBefore = "  " )
        typeAndFormat.append( ChatUtil.formatTextBracketed(
            text = "${properties.challengeFormat}",
            color = ChatUtil.yellow,
            spacingBefore = " " ) )
        player.displayClientMessage( typeAndFormat, false )

        val maxParticipants = ChatUtil.formatText(
            text = "  Max Participants " )
        maxParticipants.append( ChatUtil.formatTextBracketed(
            text = "${properties.maxParticipants}",
            color = ChatUtil.yellow ) )
        player.displayClientMessage( maxParticipants, false )

        /*
        val teamSize = ChatUtil.formatText(
            text = "  Team Size " )
        teamSize.append( ChatUtil.formatTextBracketed(
            text = "${properties.teamSize}",
            color = ChatUtil.yellow ) )
        player.displayClientMessage( teamSize, false )

        val groupSize = ChatUtil.formatText(
            text = "  Group Size " )
        groupSize.append( ChatUtil.formatTextBracketed(
            text = "${properties.groupSize}",
            color = ChatUtil.yellow ) )
        player.displayClientMessage( groupSize, false )
         */

        // TODO temp until level range is released for CobblemonChallenge
        val level = ChatUtil.formatText(
            text = "  Level " )
        level.append( ChatUtil.formatTextBracketed(
            text = "${properties.maxLevel}",
            color = ChatUtil.yellow ) )
        player.displayClientMessage( level, false )

        /*
        val levelRange = ChatUtil.formatText(
            text = "  Level Range: Min " )
        levelRange.append( ChatUtil.formatTextBracketed(
            text = "${properties.minLevel}",
            color = ChatUtil.yellow ) )
        levelRange.append( ChatUtil.formatText(
            text = " Max " ) )
        levelRange.append( ChatUtil.formatTextBracketed(
            text = "${properties.maxLevel}",
            color = ChatUtil.yellow ) )
        player.displayClientMessage( levelRange, false )
        */

        val preview = ChatUtil.formatText(
            text = "  Show Preview " )
        preview.append( ChatUtil.formatTextBracketed(
            text = "${properties.showPreview}",
            color = ChatUtil.yellow ) )
        player.displayClientMessage( preview, false )
    }

    fun displayOverviewInChat(
        properties: TournamentProperties,
        player: ServerPlayer,
        fullOverview: Boolean = false )
    {
        displayInChatHelper( properties, player )
        val tournament = TournamentStoreManager.getInstance(
            storeClass  = TournamentStore::class.java,
            storeID     = TournamentStoreManager.activeStoreKey,
            instanceID  = properties.tournamentID
        ) ?: return // TODO log?
        val matchStore = TournamentStoreManager.getStore(
            storeClass  = MatchStore::class.java,
            uuid        = properties.tournamentID
        ) ?: return // TODO log?

        val matches = mutableListOf<TournamentMatch>()
        val addMatchPredicate: (TournamentMatch) -> Boolean =
            if (fullOverview) {
                { it.matchStatus == MatchStatus.READY }
            } else {
                { true }
            }
        run loop@ {
            for ( match in matchStore.iterator() ) {
                if ( addMatchPredicate( match ) ) {
                    matches.add( match )
                }
            }
        }

        if ( matches.isEmpty() ) return
        matches.sortBy { it.tournamentMatchIndex }
        val firstMatch = matches[0]
        ChatUtil.displayInPlayerChat(
            player  = player,
            text    = "Round ${firstMatch.roundID} [${ChatUtil.shortUUID( firstMatch.roundID )}]",
            color   = ChatUtil.yellow,
            bold    = true )
        var roundIndex = firstMatch.roundIndex
        for ( match in matches ) {
            if ( roundIndex != match.roundIndex ){
                roundIndex = match.roundIndex
                ChatUtil.displayInPlayerChat(
                    player  = player,
                    text    = "Round $roundIndex [${ChatUtil.shortUUID( match.roundID )}]",
                    color   = ChatUtil.yellow,
                    bold    = true )
            }
            val matchPlayers = match.playerEntrySet()
            val entry = matchPlayers.firstNotNullOfOrNull { it }
            if ( entry == null && !fullOverview ) {
                continue
            }
            val playerTwoID = matchPlayers.firstNotNullOfOrNull {
                if ( it.value != entry?.value ) it.key else null
            }
            MatchManager.displayMatchDetails(
                player          = player,
                playerOneName   = properties.players[entry?.key]?.name ?: "[null]",
                playerTwoName   = properties.players[playerTwoID]?.name ?: "[null]",
                tournamentName  = tournament.name,
                match           = match )
        }
    }

    fun displayResultsInChatHelper(
        properties  : TournamentProperties,
        player      : ServerPlayer )
    {
        val predicate: (TournamentPlayer) -> Boolean = { p -> p.finalPlacement > 0 }
        val finalizedPlayers = mutableListOf<TournamentPlayer>()
        val competingPlayers = mutableListOf<TournamentPlayer>()
        for (p in properties.players.values) {
            if (predicate(p)) {
                finalizedPlayers.add(p)
            } else {
                competingPlayers.add(p)
            }
        }
        if (finalizedPlayers.isNotEmpty()) {
            displayTitleInChatHelper( properties, player )
            displaySlimInChatHelper( properties, player )
            ChatUtil.displayInPlayerChat(
                player = player,
                text = "Final Placements:",
                color = ChatUtil.green )
            for (p in finalizedPlayers) {
                displayFinalPlacementInChat( player, p )
            }
        }
        if (competingPlayers.isNotEmpty()) {
            ChatUtil.displayInPlayerChat(
                player = player,
                text = "Players still competing:",
                color = ChatUtil.green )
            for (p in competingPlayers) {
                displayFinalPlacementInChat( player, p )
            }
        }
    }

    private fun displayFinalPlacementInChat(
        player: ServerPlayer,
        tournamentPlayer: TournamentPlayer )
    {
        val text = ChatUtil.formatTextBracketed(
            text            = tournamentPlayer.finalPlacement.toString(),
            color           = ChatUtil.green,
            spacingBefore   = "  ",
            bold    = true )
        text.append( ChatUtil.formatText(
            text            = " Player " ) )
        text.append( ChatUtil.formatTextQuoted(
            text            = tournamentPlayer.name,
            color           = ChatUtil.aqua,
            spacingAfter    = " ",
            bold    = true ) )
        text.append( ChatUtil.formatTextBracketed(
            text    = ChatUtil.shortUUID( tournamentPlayer.uuid ),
            color   = ChatUtil.aqua ) )
        text.append( ChatUtil.formatText(
            text    = " Original Seed " ) )
        text.append( ChatUtil.formatTextBracketed(
            text    = tournamentPlayer.originalSeed.toString() ) )
        player.displayClientMessage( text ,false )
    }

}
