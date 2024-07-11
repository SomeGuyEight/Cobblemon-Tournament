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
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.util.ChatUtil
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object TournamentPropertiesHelper: PropertiesHelper <TournamentProperties>
{
    const val DEFAULT_TOURNAMENT_NAME = "Tournament"

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
            rounds              = TournamentUtil.shallowRoundsCopy( properties.rounds ),
            matches             = TournamentUtil.shallowMatchesCopy( properties.matches ),
            players             = TournamentUtil.shallowPlayersCopy( properties.players )
        )
    }

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
        mutable.rounds            = TournamentUtil.shallowRoundsCopy(  from.rounds )
        mutable.matches           = TournamentUtil.shallowMatchesCopy( from.matches )
        mutable.players           = TournamentUtil.shallowPlayersCopy( from.players )
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
        mutable.matches             = loadMatchData(  tournamentID, nbt.getCompound( DataKeys.MATCH_MAP ) )
        mutable.players             = loadPlayerData( tournamentID, nbt.getCompound( DataKeys.PLAYER_MAP ) )
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
        nbt.put( DataKeys.MATCH_MAP,  saveMatchData(  properties.matches, CompoundTag() ) )
        nbt.put( DataKeys.PLAYER_MAP, savePlayerData( properties.players, CompoundTag() ) )
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
            matches             = loadMatchData(  tournamentID, nbt.getCompound( DataKeys.MATCH_MAP ) ),
            players             = loadPlayerData( tournamentID, nbt.getCompound( DataKeys.PLAYER_MAP ) )
        )
    }

    private fun saveRoundData(
        rounds: Map<UUID,TournamentRound>,
        nbt: CompoundTag
    ): CompoundTag
    {
        var size = 0
        rounds.forEach {
            nbt.put(DataKeys.ROUND_DATA + size++, it.value.saveToNBT( CompoundTag() ))
        }
        nbt.putInt(StoreDataKeys.SIZE,size)
        return nbt
    }

    private fun loadRoundData(
        nbt: CompoundTag
    ): MutableMap<UUID, TournamentRound>
    {
        val map   = mutableMapOf<UUID,TournamentRound>()
        val size  = nbt.getInt(StoreDataKeys.SIZE)
        for (i in 0 until size) {
            val round = TournamentRound().loadFromNBT(nbt.getCompound(DataKeys.ROUND_DATA + i))
            map[round.uuid] = round
        }
        return map
    }

    private fun saveMatchData(
        matches: Map<UUID,TournamentMatch>,
        nbt: CompoundTag
    ): CompoundTag
    {
        var size = 0
        for ((_,match) in matches) {
            nbt.putUUID(DataKeys.ROUND_ID + size, match.roundID )
            nbt.putUUID(DataKeys.MATCH_ID + size++, match.uuid )
        }
        nbt.putInt(StoreDataKeys.SIZE,size)
        return nbt
    }

    private fun loadMatchData(
        tournamentID: UUID,
        nbt: CompoundTag
    ): MutableMap<UUID, TournamentMatch>
    {
        val map   = mutableMapOf<UUID,TournamentMatch>()
        val size  = nbt.getInt(StoreDataKeys.SIZE)
        for (i in 0 until size) {
            val roundID = nbt.getUUID(DataKeys.ROUND_ID + i) // TODO implement method in manager
            val matchID = nbt.getUUID(DataKeys.MATCH_ID + i)
            val match = TournamentStoreManager.getMatch( tournamentID, matchID )?: continue
            map[match.uuid] = match
        }
        return map
    }

    private fun savePlayerData(
        players: Map<UUID,TournamentPlayer>,
        nbt: CompoundTag
    ): CompoundTag
    {
        var size = 0
        players.forEach {
            nbt.putUUID(DataKeys.PLAYER_ID + size++, it.value.uuid )
        }
        nbt.putInt(StoreDataKeys.SIZE,size)
        return nbt
    }

    private fun loadPlayerData(
        tournamentID: UUID,
        nbt: CompoundTag
    ): MutableMap<UUID, TournamentPlayer>
    {
        val map   = mutableMapOf<UUID,TournamentPlayer>()
        val size  = nbt.getInt(StoreDataKeys.SIZE)
        for (i in 0 until size) {
            val playerID = nbt.getUUID(DataKeys.PLAYER_ID + i)
            val player = TournamentStoreManager.getPlayer( tournamentID, playerID )?: continue
            map[player.uuid] = player
        }
        return map
    }

    override fun logDebugHelper( properties: TournamentProperties)
    {
        Util.report("Tournament \"${properties.name}\" [${ChatUtil.shortUUID( properties.tournamentID )}]")
        Util.report("- ${properties.tournamentType} [${properties.challengeFormat}]")
        Util.report("- Max Participants: ${properties.maxParticipants}")
        Util.report("- Team Size (${properties.teamSize}) - Group Size (${properties.groupSize})")
        Util.report("- Level Range [Min: ${properties.minLevel}, Max: ${properties.maxLevel}]")
        Util.report("- Show Preview: ${properties.showPreview}")
        if (properties.rounds.isNotEmpty() || properties.matches.isNotEmpty()) {
            Util.report("- Rounds (${properties.rounds.size}) - Matches (${properties.matches.size})")
        }
        if (properties.players.isNotEmpty()) {
            Util.report("  Players ${properties.players.size}:")
            properties.players.forEach {
                Util.report("  - ${it.value} [${ChatUtil.shortUUID( it.key )}]")
            }
        }
    }

    override fun displayInChatHelper(
        properties: TournamentProperties,
        player: ServerPlayer )
    {
        val title = ChatUtil.formatText(
            text    = "Tournament ",
            color   = ChatUtil.green )
        title.append( ChatUtil.formatText( text = "\""))
        title.append( ChatUtil.formatText(
            text    = properties.name,
            color   = ChatUtil.green ) )
        title.append( ChatUtil.formatText( text = "\" "))
        title.append( ChatUtil.formatTextBracketed(
            text    = ChatUtil.shortUUID(properties.tournamentID),
            color   = ChatUtil.green ) )
        title.append( ChatUtil.formatText( text = " "))
        title.append( ChatUtil.formatTextBracketed(
            text    = properties.tournamentStatus.name,
            color   = ChatUtil.yellow ) )
        player.displayClientMessage( title, false )

        displaySlimInChatHelper( properties, player )

        if (properties.rounds.isNotEmpty() || properties.matches.isNotEmpty()) {
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
        if (properties.players.isNotEmpty()) {
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

    fun displaySlimInChatHelper(
        properties: TournamentProperties,
        player: ServerPlayer)
    {
        val component0 = ChatUtil.formatText( text = "  ")
        component0.append( ChatUtil.formatTextBracketed( text = "${properties.tournamentType}", color = ChatUtil.yellow ) )
        component0.append( ChatUtil.formatText( text = " " ) )
        component0.append( ChatUtil.formatTextBracketed( text = "${properties.challengeFormat}", color = ChatUtil.yellow ) )
        val component1 = ChatUtil.formatText( text = "  Max Participants ")
        component1.append( ChatUtil.formatTextBracketed( text = "${properties.maxParticipants}", color = ChatUtil.yellow ) )
        val component2 = ChatUtil.formatText( text = "  Team Size " )
        component2.append( ChatUtil.formatTextBracketed( text = "${properties.teamSize}", color = ChatUtil.yellow ) )
        component2.append( ChatUtil.formatText( text = " Group Size " ) )
        component2.append( ChatUtil.formatTextBracketed( text = "${properties.groupSize}", color = ChatUtil.yellow ) )
        val component3 = ChatUtil.formatText( text = "  Level Range: Min " )
        component3.append( ChatUtil.formatTextBracketed( text = "${properties.minLevel}", color = ChatUtil.yellow ) )
        component3.append( ChatUtil.formatText( text = " Max " ) )
        component3.append( ChatUtil.formatTextBracketed( text = "${properties.maxLevel}", color = ChatUtil.yellow ) )
        val component4 = ChatUtil.formatText( text = "  Show Preview ")
        component4.append( ChatUtil.formatTextBracketed( text = "${properties.showPreview}", color = ChatUtil.yellow ) )

        player.displayClientMessage( component0, false )
        player.displayClientMessage( component1, false )
        player.displayClientMessage( component2, false )
        player.displayClientMessage( component3, false )
        player.displayClientMessage( component4, false )
    }

    fun displayOverviewInChat(
        properties: TournamentProperties,
        player: ServerPlayer,
        fullOverview: Boolean = false )
    {
        displayInChatHelper( properties, player )
        val tournament = TournamentStoreManager.getTournament( properties.tournamentID )?: return
        val matchStore = TournamentStoreManager.getStore( MatchStore::class.java, properties.tournamentID )?: return // TODO log

        val matches = mutableListOf<TournamentMatch>()
        val addMatchPredicate: (TournamentMatch) -> Boolean =
            if (fullOverview) {
                { it.matchStatus == MatchStatus.READY }
            } else {
                { true }
            }
        run loop@ {
            for (match in matchStore.iterator() ) {
                if ( addMatchPredicate( match ) ) {
                    matches.add(match)
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
            bold    = true)
        var roundIndex = firstMatch.roundIndex
        for (match in matches) {
            if (roundIndex != match.roundIndex ){
                roundIndex = match.roundIndex
                ChatUtil.displayInPlayerChat(
                    player  = player,
                    text    = "Round $roundIndex [${ChatUtil.shortUUID( match.roundID )}]",
                    color   = ChatUtil.yellow,
                    bold    = true)
            }
            val matchPlayers = match.playerEntrySet()
            val entry = matchPlayers.firstNotNullOfOrNull { it }
            if (entry == null && !fullOverview) {
                continue
            }
            val playerTwoID = matchPlayers.firstNotNullOfOrNull {
                if (it.value != entry?.value) it.key else null
            }
            MatchManager.displayMatchDetails(
                player          = player,
                playerOneName   = properties.players[entry?.key]?.name ?: "[null]",
                playerTwoName   = properties.players[playerTwoID]?.name ?: "[null]",
                tournamentName  = tournament.name,
                match           = match)
        }
    }

}
