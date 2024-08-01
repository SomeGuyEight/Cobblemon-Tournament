package com.cobblemontournament.common.tournament.properties

import com.cobblemontournament.common.api.*
import com.cobblemontournament.common.api.match.MatchManager
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.api.storage.store.*
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.tournament.TournamentType
import com.sg8.collections.addIf
import com.sg8.collections.reactive.map.MutableObservableMap
import com.sg8.collections.reactive.map.loadObservableMapOf
import com.sg8.collections.reactive.map.observableMapOf
import com.sg8.collections.reactive.map.saveToNbt
import com.sg8.properties.PropertiesHelper
import com.sg8.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID


object TournamentPropertiesHelper: PropertiesHelper<TournamentProperties> {

    override fun saveToNbt(properties: TournamentProperties, nbt: CompoundTag): CompoundTag {
        nbt.putString(TOURNAMENT_NAME_KEY, properties.name)
        nbt.putUUID(TOURNAMENT_ID_KEY, properties.uuid)
        nbt.putString(TOURNAMENT_STATUS_KEY, properties.tournamentStatus.name)
        nbt.putString(TOURNAMENT_TYPE_KEY, properties.tournamentType.name)
        nbt.putString(CHALLENGE_FORMAT_KEY, properties.challengeFormat.name)
        nbt.putInt(MAX_PARTICIPANTS_KEY, properties.maxParticipants)
        nbt.putInt(TEAM_SIZE_KEY, properties.teamSize)
        nbt.putInt(GROUP_SIZE_KEY, properties.groupSize)
        nbt.putInt(MIN_LEVEL_KEY, properties.minLevel)
        nbt.putInt(MAX_LEVEL_KEY, properties.maxLevel)
        nbt.putBoolean(SHOW_PREVIEW_KEY, properties.showPreview )
        nbt.putRoundMap(properties.roundMap)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): TournamentProperties {
        val tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        return TournamentProperties(
            name = nbt.getString(TOURNAMENT_NAME_KEY),
            uuid = tournamentID,
            tournamentStatus = nbt.getConstantStrict<TournamentStatus>(TOURNAMENT_STATUS_KEY),
            tournamentType = nbt.getConstantStrict<TournamentType>(TOURNAMENT_TYPE_KEY),
            challengeFormat = nbt.getConstantStrict<ChallengeFormat>(CHALLENGE_FORMAT_KEY),
            teamSize = nbt.getInt(TEAM_SIZE_KEY),
            groupSize = nbt.getInt(GROUP_SIZE_KEY),
            maxParticipants = nbt.getInt(MAX_PARTICIPANTS_KEY),
            minLevel = nbt.getInt(MIN_LEVEL_KEY),
            maxLevel = nbt.getInt(MAX_LEVEL_KEY),
            showPreview = nbt.getBoolean(SHOW_PREVIEW_KEY),
            roundMap = nbt.getRoundMap(),
            matchMap = loadMatchMap(tournamentID = tournamentID),
            playerMap = loadPlayerMap(tournamentID = tournamentID),
        )
    }

    override fun setFromNbt(
        mutable: TournamentProperties,
        nbt: CompoundTag,
    ): TournamentProperties {
        val tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.name = nbt.getString(TOURNAMENT_NAME_KEY)
        mutable.uuid = tournamentID
        mutable.tournamentStatus = nbt.getConstantStrict<TournamentStatus>(TOURNAMENT_STATUS_KEY)
        mutable.tournamentType = nbt.getConstantStrict<TournamentType>(TOURNAMENT_TYPE_KEY)
        mutable.challengeFormat = nbt.getConstantStrict<ChallengeFormat>(CHALLENGE_FORMAT_KEY)
        mutable.teamSize = nbt.getInt(TEAM_SIZE_KEY)
        mutable.groupSize = nbt.getInt(GROUP_SIZE_KEY)
        mutable.maxParticipants = nbt.getInt(MAX_PARTICIPANTS_KEY)
        mutable.minLevel = nbt.getInt(MIN_LEVEL_KEY)
        mutable.maxLevel = nbt.getInt(MAX_LEVEL_KEY)
        mutable.showPreview = nbt.getBoolean(SHOW_PREVIEW_KEY)
        mutable.roundMap = nbt.getRoundMap()
        mutable.matchMap = loadMatchMap(tournamentID = tournamentID)
        mutable.playerMap = loadPlayerMap(tournamentID = tournamentID)
        return mutable
    }

    private fun CompoundTag.putRoundMap(roundMap: RoundMap) {
        val entryHandler = { entry: RoundEntry -> entry.value.saveToNbt(CompoundTag()) }
        val roundMapNbt = roundMap.saveToNbt(entryHandler)
        this.put(ROUND_MAP_KEY, roundMapNbt)
    }

    private fun CompoundTag.getRoundMap(): MutableObservableMap<UUID, TournamentRound> {
        val entryHandler = { roundNbt: CompoundTag,->
            TournamentRound.loadFromNbt(roundNbt).let { round -> round.uuid to round }
        }
        val roundMapNbt = this.getCompound(ROUND_DATA_KEY)
        return roundMapNbt.loadObservableMapOf(entryHandler)
    }

    private fun loadMatchMap(tournamentID: UUID): MutableMatchMap {
        val map: MutableMatchMap = observableMapOf()
        TournamentStoreManager.getStoreIterator(
            storeClass = MatchStore::class.java,
            storeID = tournamentID
        ).forEach { match ->
            map[match.uuid] = match
        }
        return map
    }

    private fun loadPlayerMap(tournamentID: UUID): MutablePlayerMap {
        val map: MutablePlayerMap = observableMapOf()
        TournamentStoreManager.getStoreIterator(
            storeClass = PlayerStore::class.java,
            storeID = tournamentID,
        ).forEach { player ->
            map[player.uuid] = player
        }
        return map
    }

    override fun deepCopy(properties: TournamentProperties): TournamentProperties {
        val roundMap: MutableRoundMap = observableMapOf()
        properties.roundMap.forEach { roundMap[it.key] = it.value.deepCopy() }
        properties.roundMap = roundMap

        val matchMap: MutableMatchMap = observableMapOf()
        properties.matchMap.forEach { matchMap[it.key] = it.value.deepCopy() }
        properties.matchMap = matchMap

        val playerMap: MutablePlayerMap = observableMapOf()
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
            val titleComponent = getComponent(
                text = "  Rounds ",
                color = YELLOW_FORMAT,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.roundMap.size}",
                textColor = YELLOW_FORMAT,
            )
            titleComponent.appendWith(text = " - ")
            titleComponent.appendWith(
                text = "Matches ",
                color = YELLOW_FORMAT,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.matchMap.size}",
                textColor = YELLOW_FORMAT,
            )
            player.displayClientMessage(titleComponent, false)
        }
        if (properties.playerMap.isNotEmpty()) {
            val titleComponent = getComponent(
                text = "  Players ",
                color = AQUA_FORMAT,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.playerMap.size}",
                textColor = AQUA_FORMAT,
            )
            player.displayClientMessage(titleComponent, false)

            properties.playerMap.values.forEach { tournamentPlayer ->
                tournamentPlayer.displayInChat(player = player, padStart = 4)
            }
        }
    }

    private fun displayTitleInChatHelper(properties: TournamentProperties, player: ServerPlayer) {
        val component = getComponent(text = "Tournament ", color = GREEN_FORMAT)
        component.appendWith(text = "\"")
        component.appendWith(text = properties.name, color = GREEN_FORMAT)
        component.appendWith(text = "\" ")
        component.appendWithBracketed(text = properties.uuid.short(), textColor = GREEN_FORMAT)
        component.appendWith(text = " ")
        component.appendWithBracketed(
            text = properties.tournamentStatus.name,
            textColor = YELLOW_FORMAT,
        )
        player.displayClientMessage(component, false)
    }

    fun displaySlimInChatHelper(properties: TournamentProperties, player: ServerPlayer) {
        val typeAndFormat = getBracketedComponent(
            text = "${properties.tournamentType}",
            textColor = YELLOW_FORMAT,
            padding = 2 to 0,
        )
        typeAndFormat.appendWithBracketed(
            text = "${properties.challengeFormat}",
            textColor = YELLOW_FORMAT,
            padding = 1 to 0,
        )
        player.displayClientMessage(typeAndFormat, (false))

        val maxParticipants = getComponent(text = "  Max Participants ")
        maxParticipants.appendWithBracketed(
            text = "${properties.maxParticipants}",
            textColor = YELLOW_FORMAT,
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
        val level = getComponent(text = "  Level ")
        level.appendWithBracketed(text = "${properties.maxLevel}", textColor = YELLOW_FORMAT)
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

        val preview = getComponent(text = "  Show Preview ")
        preview.appendWithBracketed(text = "${properties.showPreview}", textColor = YELLOW_FORMAT)
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
            color = YELLOW_FORMAT,
            bold = true,
        )
        var roundIndex = firstMatch.roundIndex
        for (match in matches) {
            if (roundIndex != match.roundIndex) {
                roundIndex = match.roundIndex
                player.displayInChat(
                    text = "Round $roundIndex [${match.roundID.short()}]",
                    color = YELLOW_FORMAT,
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

            player.displayInChat(text = "Final Placements:", color = GREEN_FORMAT)
            for (tournamentPlayer in finalizedPlayers) {
                displayFinalPlacementInChat(player, tournamentPlayer)
            }
        }

        if (competingPlayers.isNotEmpty()) {
            player.displayInChat(text = "Players still competing:", color = GREEN_FORMAT)
            for (tournamentPlayer in competingPlayers) {
                displayFinalPlacementInChat(player, tournamentPlayer)
            }
        }
    }

    private fun displayFinalPlacementInChat(
        player: ServerPlayer,
        tournamentPlayer: TournamentPlayer,
    ) {
        val component = getBracketedComponent(
            text = tournamentPlayer.finalPlacement.toString(),
            textColor = GREEN_FORMAT,
            padding = 2 to 0,
            bold = true,
        )
        component.appendWith(text = " Player ")
        component.appendWithQuoted(
            text = tournamentPlayer.name,
            textColor = AQUA_FORMAT,
            padding = 0 to 1,
            bold = true,
        )
        component.appendWithBracketed(
            text = tournamentPlayer.uuid.short(),
            textColor = AQUA_FORMAT,
        )
        component.appendWith(text = " Original Seed ")
        component.appendWithBracketed(text = tournamentPlayer.originalSeed.toString())
        player.displayClientMessage(component, false)
    }

}
