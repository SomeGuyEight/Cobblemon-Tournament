package com.cobblemontournament.common.tournament.properties

import com.cobblemontournament.common.api.MatchManager
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.util.*
import com.someguy.storage.PropertiesHelper
import com.someguy.storage.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object TournamentPropertiesHelper: PropertiesHelper<TournamentProperties> {

    override fun setFromNbtHelper(
        mutable: TournamentProperties,
        nbt: CompoundTag,
    ): TournamentProperties {
        val tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.name = nbt.getString(TOURNAMENT_NAME_KEY)
        mutable.tournamentID = tournamentID
        mutable.tournamentStatus =
            enumValueOf<TournamentStatus>(nbt.getString(TOURNAMENT_STATUS_KEY))
        mutable.tournamentType =
            enumValueOf<TournamentType>( nbt.getString(TOURNAMENT_TYPE_KEY))
        mutable.challengeFormat =
            enumValueOf<ChallengeFormat>(nbt.getString(CHALLENGE_FORMAT_KEY))
        mutable.teamSize = nbt.getInt(TEAM_SIZE_KEY)
        mutable.groupSize = nbt.getInt(GROUP_SIZE_KEY)
        mutable.maxParticipants = nbt.getInt(MAX_PARTICIPANTS_KEY)
        mutable.minLevel = nbt.getInt(MIN_LEVEL_KEY)
        mutable.maxLevel = nbt.getInt(MAX_LEVEL_KEY)
        mutable.showPreview = nbt.getBoolean(SHOW_PREVIEW_KEY)
        mutable.rounds = loadRoundData(nbt = nbt.getCompound(ROUND_MAP_KEY))
        mutable.matches = loadMatchData(tournamentID = tournamentID)
        mutable.players = loadPlayerData(tournamentID = tournamentID)
        return mutable
    }

    override fun deepCopyHelper(properties: TournamentProperties): TournamentProperties {
        return TournamentProperties(
            name = properties.name,
            tournamentID = properties.tournamentID,
            tournamentStatus = properties.tournamentStatus,
            tournamentType = properties.tournamentType,
            challengeFormat = properties.challengeFormat,
            maxParticipants = properties.maxParticipants,
            teamSize = properties.teamSize,
            groupSize = properties.groupSize,
            minLevel = properties.minLevel,
            maxLevel = properties.maxLevel,
            showPreview = properties.showPreview,
            rounds = properties.rounds.toMutableMap(),
            matches = properties.matches.toMutableMap(),
            players = properties.players.toMutableMap(),
        )
    }

    override fun saveToNbtHelper(
        properties: TournamentProperties,
        nbt: CompoundTag,
    ): CompoundTag {
        nbt.putString(TOURNAMENT_NAME_KEY, properties.name)
        nbt.putUUID(TOURNAMENT_ID_KEY, properties.tournamentID)
        nbt.putString(TOURNAMENT_STATUS_KEY, properties.tournamentStatus.name)
        nbt.putString(TOURNAMENT_TYPE_KEY, properties.tournamentType.name)
        nbt.putString(CHALLENGE_FORMAT_KEY, properties.challengeFormat.name)
        nbt.putInt(MAX_PARTICIPANTS_KEY, properties.maxParticipants)
        nbt.putInt(TEAM_SIZE_KEY, properties.teamSize)
        nbt.putInt(GROUP_SIZE_KEY, properties.groupSize)
        nbt.putInt(MIN_LEVEL_KEY, properties.minLevel)
        nbt.putInt(MAX_LEVEL_KEY, properties.maxLevel)
        nbt.putBoolean(SHOW_PREVIEW_KEY, properties.showPreview )
        nbt.put(ROUND_MAP_KEY, saveRoundData(properties.rounds, nbt = CompoundTag()))
        return nbt
    }

    private fun saveRoundData(rounds: RoundMap, nbt: CompoundTag): CompoundTag {
        var size = 0
        rounds.forEach { (_, round) ->
            nbt.put((ROUND_DATA_KEY + (size++)), round.saveToNbt(nbt = CompoundTag()))
        }
        nbt.putInt(SIZE_KEY, size)
        return nbt
    }

    override fun loadFromNbtHelper(nbt: CompoundTag): TournamentProperties {
        val tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        return TournamentProperties(
            name = nbt.getString(TOURNAMENT_NAME_KEY ),
            tournamentID = tournamentID,
            tournamentStatus = enumValueOf<TournamentStatus>(nbt.getString(TOURNAMENT_STATUS_KEY)),
            tournamentType = enumValueOf<TournamentType>(nbt.getString(TOURNAMENT_TYPE_KEY)),
            challengeFormat = enumValueOf<ChallengeFormat>(nbt.getString(CHALLENGE_FORMAT_KEY)),
            teamSize = nbt.getInt(TEAM_SIZE_KEY),
            groupSize = nbt.getInt(GROUP_SIZE_KEY),
            maxParticipants = nbt.getInt(MAX_PARTICIPANTS_KEY),
            minLevel = nbt.getInt(MIN_LEVEL_KEY),
            maxLevel = nbt.getInt(MAX_LEVEL_KEY),
            showPreview = nbt.getBoolean(SHOW_PREVIEW_KEY),
            rounds = loadRoundData(nbt = nbt.getCompound(ROUND_MAP_KEY)),
            matches = loadMatchData(tournamentID = tournamentID),
            players = loadPlayerData(tournamentID = tournamentID),
        )
    }

    private fun loadRoundData(nbt: CompoundTag): RoundMap {
        val map: RoundMap = mutableMapOf()
        val size = nbt.getInt(SIZE_KEY)
        for (i in 0 until size) {
            val round = TournamentRound.loadFromNbt(nbt = nbt.getCompound((ROUND_DATA_KEY + i)))
            map[round.uuid] = round
        }
        return map
    }

    private fun loadMatchData(tournamentID: UUID): MatchMap {
        val map: MatchMap = mutableMapOf()
        TournamentStoreManager.getStoreIterator(
            storeClass = MatchStore::class.java,
            storeID = tournamentID,
        ).forEach { match ->
            map[match.uuid] = match
        }
        return map
    }

    private fun loadPlayerData(tournamentID: UUID): PlayerMap {
        val map: PlayerMap = mutableMapOf()
        TournamentStoreManager.getStoreIterator(
            storeClass = PlayerStore::class.java,
            storeID = tournamentID,
        ).forEach { player ->
            map[player.uuid] = player
        }
        return map
    }

    override fun logDebugHelper(properties: TournamentProperties) {
        Util.report(("Tournament \"${properties.name}\" " +
                "[${properties.tournamentID.shortUUID()}]"))
        Util.report(("- ${properties.tournamentType} [${properties.challengeFormat}]"))
        Util.report(("- Max Participants: ${properties.maxParticipants}"))
        Util.report(("- Team Size (${properties.teamSize}) - " +
                "Group Size (${properties.groupSize})"))
        Util.report(("- Level Range [Min: ${properties.minLevel}, Max: ${properties.maxLevel}]"))
        Util.report(("- Show Preview: ${properties.showPreview}"))

        if (properties.rounds.isNotEmpty() || properties.matches.isNotEmpty()) {
            Util.report(("- Rounds (${properties.rounds.size}) - " +
                    "Matches (${properties.matches.size})"))
        }

        if (properties.players.isNotEmpty()) {
            Util.report(("  Players ${properties.players.size}:"))
            properties.players.forEach { (playerID, player) ->
                Util.report(("  - ${player.name} [${playerID.shortUUID()}]"))
            }
        }
    }

    override fun displayInChatHelper(properties: TournamentProperties, player: ServerPlayer) {
        displayTitleInChatHelper(properties, player)
        displaySlimInChatHelper(properties, player)

        if (properties.rounds.isNotEmpty() || properties.matches.isNotEmpty()) {
            val titleComponent = getComponent(
                text = "  Rounds ",
                color = ChatUtil.YELLOW_FORMAT,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.rounds.size}",
                textColor = ChatUtil.YELLOW_FORMAT,
            )
            titleComponent.appendWith(text = " - ")
            titleComponent.appendWith(
                text = "Matches ",
                color = ChatUtil.YELLOW_FORMAT,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.matches.size}",
                textColor = ChatUtil.YELLOW_FORMAT,
            )
            player.displayClientMessage(titleComponent, false)
        }
        if (properties.players.isNotEmpty()) {
            val titleComponent = getComponent(
                text = "  Players ",
                color = ChatUtil.AQUA_FORMAT,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.players.size}",
                textColor = ChatUtil.AQUA_FORMAT,
            )
            player.displayClientMessage(titleComponent, false)

            properties.players.values.forEach { tournamentPlayer ->
                tournamentPlayer.displayInChat(player = player, padStart = 4)
            }
        }
    }

    private fun displayTitleInChatHelper(properties: TournamentProperties, player: ServerPlayer) {
        val component = getComponent(text = "Tournament ", color = ChatUtil.GREEN_FORMAT)
        component.appendWith(text = "\"")
        component.appendWith(text = properties.name, color = ChatUtil.GREEN_FORMAT)
        component.appendWith(text = "\" ")
        component.appendWithBracketed(
            text = properties.tournamentID.shortUUID(),
            textColor = ChatUtil.GREEN_FORMAT,
        )
        component.appendWith(text = " ")
        component.appendWithBracketed(
            text = properties.tournamentStatus.name,
            textColor = ChatUtil.YELLOW_FORMAT,
        )
        player.displayClientMessage(component, false)
    }

    fun displaySlimInChatHelper(properties: TournamentProperties, player: ServerPlayer) {
        val typeAndFormat = getBracketedComponent(
            text = "${properties.tournamentType}",
            textColor = ChatUtil.YELLOW_FORMAT,
            padding = 2 to 0,
        )
        typeAndFormat.appendWithBracketed(
            text = "${properties.challengeFormat}",
            textColor = ChatUtil.YELLOW_FORMAT,
            padding = 1 to 0,
        )
        player.displayClientMessage(typeAndFormat, (false))

        val maxParticipants = getComponent(text = "  Max Participants ")
        maxParticipants.appendWithBracketed(
            text = "${properties.maxParticipants}",
            textColor = ChatUtil.YELLOW_FORMAT,
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
        level.appendWithBracketed(
            text = "${properties.maxLevel}",
            textColor = ChatUtil.YELLOW_FORMAT,
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

        val preview = getComponent(text = "  Show Preview ")
        preview.appendWithBracketed(
            text = "${properties.showPreview}",
            textColor = ChatUtil.YELLOW_FORMAT,
        )
        player.displayClientMessage(preview, false)
    }

    fun displayOverviewInChat(
        properties: TournamentProperties,
        player: ServerPlayer,
        fullOverview: Boolean = false
    ) {
        displayInChatHelper(properties, player)

        val tournament = TournamentStoreManager.getInstance(
            storeClass = TournamentStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            instanceID = properties.tournamentID,
        ) ?: return // TODO log?

        val matchStore = TournamentStoreManager.getStore(
            storeClass = MatchStore::class.java,
            uuid = properties.tournamentID,
        ) ?: return // TODO log?

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
            text = "Round ${firstMatch.roundID} [${firstMatch.roundID.shortUUID()}]",
            color = ChatUtil.YELLOW_FORMAT,
            bold = true,
        )
        var roundIndex = firstMatch.roundIndex
        for (match in matches) {
            if (roundIndex != match.roundIndex) {
                roundIndex = match.roundIndex
                player.displayInChat(
                    text = "Round $roundIndex [${match.roundID.shortUUID()}]",
                    color = ChatUtil.YELLOW_FORMAT,
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
                playerOneName = properties.players[entry?.key]?.name ?: "[null]",
                playerTwoName = properties.players[playerTwoID]?.name ?: "[null]",
                tournamentName = tournament.name,
                match = match,
            )
        }
    }

    fun displayResultsInChatHelper(properties: TournamentProperties, player: ServerPlayer) {
        val finalizedPlayers = mutableListOf<TournamentPlayer>()
        val competingPlayers = mutableListOf<TournamentPlayer>()

        val isFinalized: (TournamentPlayer) -> Boolean = { it.finalPlacement > 0 }
        for (tournamentPlayer in properties.players.values) {
            if (isFinalized(tournamentPlayer)) {
                finalizedPlayers.add(tournamentPlayer)
            } else {
                competingPlayers.add(tournamentPlayer)
            }
        }

        if (finalizedPlayers.isNotEmpty()) {
            displayTitleInChatHelper(properties, player)
            displaySlimInChatHelper(properties, player)

            player.displayInChat(text = "Final Placements:", color = ChatUtil.GREEN_FORMAT)
            for (tournamentPlayer in finalizedPlayers) {
                displayFinalPlacementInChat(player, tournamentPlayer)
            }
        }

        if (competingPlayers.isNotEmpty()) {
            player.displayInChat(text = "Players still competing:", color = ChatUtil.GREEN_FORMAT)
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
            textColor = ChatUtil.GREEN_FORMAT,
            padding = 2 to 0,
            bold = true,
        )
        component.appendWith(text = " Player ")
        component.appendWithQuoted(
            text = tournamentPlayer.name,
            textColor = ChatUtil.AQUA_FORMAT,
            padding = 0 to 1,
            bold = true,
        )
        component.appendWithBracketed(
            text = tournamentPlayer.uuid.shortUUID(),
            textColor = ChatUtil.AQUA_FORMAT,
        )
        component.appendWith(text = " Original Seed ")
        component.appendWithBracketed(text = tournamentPlayer.originalSeed.toString())
        player.displayClientMessage(component, false)
    }

}
