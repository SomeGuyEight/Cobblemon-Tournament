package com.cobblemontournament.common.tournament.properties

import com.cobblemontournament.common.api.MutableMatchMap
import com.cobblemontournament.common.api.MutablePlayerMap
import com.cobblemontournament.common.api.MutableRoundMap
import com.cobblemontournament.common.api.RoundEntry
import com.cobblemontournament.common.api.RoundMap
import com.cobblemontournament.common.api.match.MatchManager
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.storage.store.MatchStore
import com.cobblemontournament.common.api.storage.store.PlayerStore
import com.cobblemontournament.common.api.storage.store.TournamentStore
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.tournament.TournamentType
import com.sg8.collections.addIf
import com.sg8.collections.reactive.map.MutableObservableMap
import com.sg8.collections.reactive.map.loadMutableObservableMapOf
import com.sg8.collections.reactive.map.mutableObservableMapOf
import com.sg8.collections.reactive.map.saveToNbt
import com.sg8.properties.PropertiesHelper
import com.sg8.util.appendWith
import com.sg8.util.appendWithBracketed
import com.sg8.util.appendWithQuoted
import com.sg8.util.ComponentUtil
import com.sg8.util.displayInChat
import com.sg8.util.getConstantStrict
import com.sg8.util.short
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID


object TournamentPropertiesHelper: PropertiesHelper<TournamentProperties> {

    override fun saveToNbt(properties: TournamentProperties, nbt: CompoundTag): CompoundTag {
        nbt.putString(DataKeys.TOURNAMENT_NAME, properties.name)
        nbt.putUUID(DataKeys.TOURNAMENT_ID, properties.uuid)
        nbt.putString(DataKeys.TOURNAMENT_STATUS, properties.tournamentStatus.name)
        nbt.putString(DataKeys.TOURNAMENT_TYPE, properties.tournamentType.name)
        nbt.putString(DataKeys.CHALLENGE_FORMAT, properties.challengeFormat.name)
        nbt.putInt(DataKeys.MAX_PARTICIPANTS, properties.maxParticipants)
        nbt.putInt(DataKeys.TEAM_SIZE, properties.teamSize)
        nbt.putInt(DataKeys.GROUP_SIZE, properties.groupSize)
        nbt.putInt(DataKeys.MIN_LEVEL, properties.minLevel)
        nbt.putInt(DataKeys.MAX_LEVEL, properties.maxLevel)
        nbt.putBoolean(DataKeys.SHOW_PREVIEW, properties.showPreview )
        nbt.putRoundMap(properties.roundMap)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): TournamentProperties {
        val tournamentID = nbt.getUUID(DataKeys.TOURNAMENT_ID)
        return TournamentProperties(
            name = nbt.getString(DataKeys.TOURNAMENT_NAME),
            uuid = tournamentID,
            tournamentStatus = nbt.getConstantStrict<TournamentStatus>(DataKeys.TOURNAMENT_STATUS),
            tournamentType = nbt.getConstantStrict<TournamentType>(DataKeys.TOURNAMENT_TYPE),
            challengeFormat = nbt.getConstantStrict<ChallengeFormat>(DataKeys.CHALLENGE_FORMAT),
            teamSize = nbt.getInt(DataKeys.TEAM_SIZE),
            groupSize = nbt.getInt(DataKeys.GROUP_SIZE),
            maxParticipants = nbt.getInt(DataKeys.MAX_PARTICIPANTS),
            minLevel = nbt.getInt(DataKeys.MIN_LEVEL),
            maxLevel = nbt.getInt(DataKeys.MAX_LEVEL),
            showPreview = nbt.getBoolean(DataKeys.SHOW_PREVIEW),
            roundMap = nbt.getRoundMap(),
            matchMap = loadMatchMap(tournamentID = tournamentID),
            playerMap = loadPlayerMap(tournamentID = tournamentID),
        )
    }

    override fun setFromNbt(
        mutable: TournamentProperties,
        nbt: CompoundTag,
    ): TournamentProperties {
        val tournamentID = nbt.getUUID(DataKeys.TOURNAMENT_ID)
        mutable.name = nbt.getString(DataKeys.TOURNAMENT_NAME)
        mutable.uuid = tournamentID
        mutable.tournamentStatus = nbt.getConstantStrict<TournamentStatus>(DataKeys.TOURNAMENT_STATUS)
        mutable.tournamentType = nbt.getConstantStrict<TournamentType>(DataKeys.TOURNAMENT_TYPE)
        mutable.challengeFormat = nbt.getConstantStrict<ChallengeFormat>(DataKeys.CHALLENGE_FORMAT)
        mutable.teamSize = nbt.getInt(DataKeys.TEAM_SIZE)
        mutable.groupSize = nbt.getInt(DataKeys.GROUP_SIZE)
        mutable.maxParticipants = nbt.getInt(DataKeys.MAX_PARTICIPANTS)
        mutable.minLevel = nbt.getInt(DataKeys.MIN_LEVEL)
        mutable.maxLevel = nbt.getInt(DataKeys.MAX_LEVEL)
        mutable.showPreview = nbt.getBoolean(DataKeys.SHOW_PREVIEW)
        mutable.roundMap = nbt.getRoundMap()
        mutable.matchMap = loadMatchMap(tournamentID = tournamentID)
        mutable.playerMap = loadPlayerMap(tournamentID = tournamentID)
        return mutable
    }

    private fun CompoundTag.putRoundMap(roundMap: RoundMap) {
        val entryHandler = { entry: RoundEntry -> entry.value.saveToNbt(CompoundTag()) }
        val roundMapNbt = roundMap.saveToNbt(entryHandler)
        this.put(DataKeys.ROUND_MAP, roundMapNbt)
    }

    private fun CompoundTag.getRoundMap(): MutableObservableMap<UUID, TournamentRound> {
        val entryHandler = { roundNbt: CompoundTag,->
            TournamentRound.loadFromNbt(roundNbt).let { round -> round.uuid to round }
        }
        val roundMapNbt = this.getCompound(DataKeys.ROUND_DATA)
        return roundMapNbt.loadMutableObservableMapOf(entryHandler)
    }

    private fun loadMatchMap(tournamentID: UUID): MutableMatchMap {
        val map: MutableMatchMap = mutableObservableMapOf()
        TournamentStoreManager.getStoreIterator(
            storeClass = MatchStore::class.java,
            storeID = tournamentID
        ).forEach { match ->
            map[match.uuid] = match
        }
        return map
    }

    private fun loadPlayerMap(tournamentID: UUID): MutablePlayerMap {
        val map: MutablePlayerMap = mutableObservableMapOf()
        TournamentStoreManager.getStoreIterator(
            storeClass = PlayerStore::class.java,
            storeID = tournamentID,
        ).forEach { player ->
            map[player.uuid] = player
        }
        return map
    }

    override fun deepCopy(properties: TournamentProperties): TournamentProperties {
        val roundMap: MutableRoundMap = mutableObservableMapOf()
        properties.roundMap.forEach { roundMap[it.key] = it.value.deepCopy() }
        properties.roundMap = roundMap

        val matchMap: MutableMatchMap = mutableObservableMapOf()
        properties.matchMap.forEach { matchMap[it.key] = it.value.deepCopy() }
        properties.matchMap = matchMap

        val playerMap: MutablePlayerMap = mutableObservableMapOf()
        properties.playerMap.forEach { playerMap[it.key] = it.value.deepCopy() }
        properties.playerMap = playerMap

        return copy(properties)
    }

    override fun copy(properties: TournamentProperties): TournamentProperties {
        return TournamentProperties(
            name = properties.name,
            uuid = properties.uuid,
            tournamentStatus = properties.tournamentStatus,
            tournamentType = properties.tournamentType,
            challengeFormat = properties.challengeFormat,
            maxParticipants = properties.maxParticipants,
            teamSize = properties.teamSize,
            groupSize = properties.groupSize,
            minLevel = properties.minLevel,
            maxLevel = properties.maxLevel,
            showPreview = properties.showPreview,
            roundMap = properties.roundMap,
            matchMap = properties.matchMap,
            playerMap = properties.playerMap,
        )
    }

    override fun printDebug(properties: TournamentProperties) {
        Util.report(("Tournament \"${properties.name}\" [${properties.uuid.short()}]"))
        Util.report(("- ${properties.tournamentType} [${properties.challengeFormat}]"))
        Util.report(("- Max Participants: ${properties.maxParticipants}"))
        Util.report(("- Team Size (${properties.teamSize}) - " +
                "Group Size (${properties.groupSize})"))
        Util.report(("- Level Range [Min: ${properties.minLevel}, Max: ${properties.maxLevel}]"))
        Util.report(("- Show Preview: ${properties.showPreview}"))

        if (properties.roundMap.isNotEmpty() || properties.matchMap.isNotEmpty()) {
            Util.report(("- Rounds (${properties.roundMap.size}) - " +
                    "Matches (${properties.matchMap.size})"))
        }

        if (properties.playerMap.isNotEmpty()) {
            Util.report(("  Players ${properties.playerMap.size}:"))
            properties.playerMap.forEach { (playerID, player) ->
                Util.report(("  - ${player.name} [${playerID.short()}]"))
            }
        }
    }

    override fun displayInChat(properties: TournamentProperties, player: ServerPlayer) {
        displayTitleInChatHelper(properties, player)
        displaySlimInChatHelper(properties, player)

        if (properties.roundMap.isNotEmpty() || properties.matchMap.isNotEmpty()) {
            val titleComponent = ComponentUtil.getComponent(
                text = "  Rounds ",
                color = ChatFormatting.YELLOW,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.roundMap.size}",
                textColor = ChatFormatting.YELLOW,
            )
            titleComponent.appendWith(text = " - ")
            titleComponent.appendWith(
                text = "Matches ",
                color = ChatFormatting.YELLOW,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.matchMap.size}",
                textColor = ChatFormatting.YELLOW,
            )
            player.displayClientMessage(titleComponent, false)
        }
        if (properties.playerMap.isNotEmpty()) {
            val titleComponent = ComponentUtil.getComponent(
                text = "  Players ",
                color = ChatFormatting.AQUA,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.playerMap.size}",
                textColor = ChatFormatting.AQUA,
            )
            player.displayClientMessage(titleComponent, false)

            properties.playerMap.values.forEach { tournamentPlayer ->
                tournamentPlayer.displayInChat(player = player, padStart = 4)
            }
        }
    }

    private fun displayTitleInChatHelper(properties: TournamentProperties, player: ServerPlayer) {
        val component = ComponentUtil.getComponent(
            text = "Tournament ",
            color = ChatFormatting.GREEN,
        )
        component.appendWith(text = "\"")
        component.appendWith(text = properties.name, color = ChatFormatting.GREEN)
        component.appendWith(text = "\" ")
        component.appendWithBracketed(
            text = properties.uuid.short(),
            textColor = ChatFormatting.GREEN,
        )
        component.appendWith(text = " ")
        component.appendWithBracketed(
            text = properties.tournamentStatus.name,
            textColor = ChatFormatting.YELLOW,
        )
        player.displayClientMessage(component, false)
    }

    fun displaySlimInChatHelper(properties: TournamentProperties, player: ServerPlayer) {
        val typeAndFormat = ComponentUtil.getBracketedComponent(
            text = "${properties.tournamentType}",
            textColor = ChatFormatting.YELLOW,
            padding = 2 to 0,
        )
        typeAndFormat.appendWithBracketed(
            text = "${properties.challengeFormat}",
            textColor = ChatFormatting.YELLOW,
            padding = 1 to 0,
        )
        player.displayClientMessage(typeAndFormat, (false))

        val maxParticipants = ComponentUtil.getComponent(text = "  Max Participants ")
        maxParticipants.appendWithBracketed(
            text = "${properties.maxParticipants}",
            textColor = ChatFormatting.YELLOW,
        )
        player.displayClientMessage(maxParticipants, false)

        /*
        val teamSize = ChatUtil.formatText(text = "  Team Size ")
        teamSize.appendWithBracketed(
            text = "${properties.teamSize}",
            textColor = ChatUtil.yellow,
        )
        player.displayClientMessage(teamSize, false)

        val groupSize = ChatUtil.formatText(text = "  Group Size ")
        groupSize.appendWithBracketed(
            text = "${properties.groupSize}",
            textColor = ChatUtil.yellow,
        )
        player.displayClientMessage(groupSize, false)
         */

        // TODO temp until level range is released for CobblemonChallenge
        val level = ComponentUtil.getComponent(text = "  Level ")
        level.appendWithBracketed(
            text = "${properties.maxLevel}",
            textColor = ChatFormatting.YELLOW,
        )
        player.displayClientMessage(level, false)

       /*
        val levelRange = ChatUtil.formatText(text = "  Level Range: Min ")
        levelRange.appendWithBracketed(
            text = "${properties.minLevel}",
            textColor = ChatUtil.yellow,
        )
        levelRange.append(ChatUtil.formatText(text = " Max "))
        levelRange.appendWithBracketed(
            text = "${properties.maxLevel}",
            textColor = ChatUtil.yellow,
        )
        player.displayClientMessage(levelRange, false)
        */

        val preview = ComponentUtil.getComponent(text = "  Show Preview ")
        preview.appendWithBracketed(
            text = "${properties.showPreview}",
            textColor = ChatFormatting.YELLOW,
        )
        player.displayClientMessage(preview, false)
    }

    fun displayOverviewInChat(
        properties: TournamentProperties,
        player: ServerPlayer,
        fullOverview: Boolean = false
    ) {
        displayInChat(properties, player)

        val tournament = TournamentStoreManager
            .getInstance(
                storeClass = TournamentStore::class.java,
                storeID = TournamentStoreManager.ACTIVE_STORE_ID,
                instanceID = properties.uuid,
            ) ?: return

        val matchStore = TournamentStoreManager
            .getStore(storeClass = MatchStore::class.java, uuid = properties.uuid)
            ?: return

        val matches = mutableListOf<TournamentMatch>()
        val matchPredicate: (TournamentMatch) -> Boolean = if (fullOverview) {
            { it.getUpdatedMatchStatus() == MatchStatus.READY }
        } else {
            { true }
        }

        matchStore.iterator().forEach { matches.addIf(it, matchPredicate) }

        if (matches.isEmpty()) {
            return
        }

        matches.sortBy { it.tournamentMatchIndex }
        val firstMatch = matches[0]
        player.displayInChat(
            text = "Round ${firstMatch.roundID} [${firstMatch.roundID.short()}]",
            color = ChatFormatting.YELLOW,
            bold = true,
        )
        var roundIndex = firstMatch.roundIndex
        for (match in matches) {
            if (roundIndex != match.roundIndex) {
                roundIndex = match.roundIndex
                player.displayInChat(
                    text = "Round $roundIndex [${match.roundID.short()}]",
                    color = ChatFormatting.YELLOW,
                    bold = true,
                )
            }
            val matchPlayers = match.playerEntrySet()
            val entry = matchPlayers.firstNotNullOfOrNull { it }
            if (entry == null && !fullOverview) {
                continue
            }
            val playerTwoID = matchPlayers.firstNotNullOfOrNull { (playerID, team) ->
                if ( team != entry?.value ) {
                    return@firstNotNullOfOrNull playerID
                } else {
                    return@firstNotNullOfOrNull null
                }
            }

            MatchManager.displayMatchDetails(
                player = player,
                playerOneName = properties.playerMap[entry?.key]?.name ?: "[null]",
                playerTwoName = properties.playerMap[playerTwoID]?.name ?: "[null]",
                tournamentName = tournament.name,
                match = match,
            )
        }
    }

    fun displayResultsInChatHelper(properties: TournamentProperties, player: ServerPlayer) {
        val finalizedPlayers = mutableListOf<TournamentPlayer>()
        val competingPlayers = mutableListOf<TournamentPlayer>()

        val isFinalized: (TournamentPlayer) -> Boolean = { it.finalPlacement > 0 }
        for (tournamentPlayer in properties.playerMap.values) {
            if (isFinalized(tournamentPlayer)) {
                finalizedPlayers.add(tournamentPlayer)
            } else {
                competingPlayers.add(tournamentPlayer)
            }
        }

        if (finalizedPlayers.isNotEmpty()) {
            displayTitleInChatHelper(properties, player)
            displaySlimInChatHelper(properties, player)

            player.displayInChat(text = "Final Placements:", color = ChatFormatting.YELLOW)
            for (tournamentPlayer in finalizedPlayers) {
                displayFinalPlacementInChat(player, tournamentPlayer)
            }
        }

        if (competingPlayers.isNotEmpty()) {
            player.displayInChat(text = "Players still competing:", color = ChatFormatting.YELLOW)
            for (tournamentPlayer in competingPlayers) {
                displayFinalPlacementInChat(player, tournamentPlayer)
            }
        }
    }

    private fun displayFinalPlacementInChat(
        player: ServerPlayer,
        tournamentPlayer: TournamentPlayer,
    ) {
        val component = ComponentUtil.getBracketedComponent(
            text = tournamentPlayer.finalPlacement.toString(),
            textColor = ChatFormatting.GREEN,
            padding = 2 to 0,
            bold = true,
        )
        component.appendWith(text = " Player ")
        component.appendWithQuoted(
            text = tournamentPlayer.name,
            textColor = ChatFormatting.AQUA,
            padding = 0 to 1,
            bold = true,
        )
        component.appendWithBracketed(
            text = tournamentPlayer.uuid.short(),
            textColor = ChatFormatting.AQUA,
        )
        component.appendWith(text = " Original Seed ")
        component.appendWithBracketed(text = tournamentPlayer.originalSeed.toString())
        player.displayClientMessage(component, false)
    }

}
