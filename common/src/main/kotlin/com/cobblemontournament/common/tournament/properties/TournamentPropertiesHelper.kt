package com.cobblemontournament.common.tournament.properties

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.api.WatchedMatches
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.api.storage.MatchStore
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.api.storage.PlayerStore
import com.cobblemontournament.common.api.storage.TournamentDataKeys.CHALLENGE_FORMAT_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.GROUP_SIZE_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MAX_LEVEL_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MAX_PARTICIPANTS_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MIN_LEVEL_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_DATA_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_MAP_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.SHOW_PREVIEW_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TEAM_SIZE_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_NAME_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_STATUS_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_TYPE_KEY
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

object TournamentPropertiesHelper: PropertiesHelper <TournamentProperties> {

    const val DEFAULT_TOURNAMENT_NAME = "Default-Tournament-Name"
    val DEFAULT_TOURNAMENT_STATUS = TournamentStatus.UNKNOWN

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
            rounds = properties.rounds.shallowCopy(),
            matches = properties.matches.shallowCopy(),
            players = properties.players.shallowCopy(),
        )
    }

    @Suppress("DuplicatedCode")
    override fun setFromPropertiesHelper(
        mutable: TournamentProperties,
        from: TournamentProperties,
    ): TournamentProperties {
        mutable.name = from.name
        mutable.tournamentID = from.tournamentID
        mutable.tournamentStatus = from.tournamentStatus
        mutable.tournamentType = from.tournamentType
        mutable.challengeFormat = from.challengeFormat
        mutable.teamSize = from.teamSize
        mutable.groupSize = from.groupSize
        mutable.maxParticipants = from.maxParticipants
        mutable.minLevel = from.minLevel
        mutable.maxLevel = from.maxLevel
        mutable.showPreview = from.showPreview
        mutable.rounds = from.rounds.shallowCopy()
        mutable.matches = from.matches.shallowCopy()
        mutable.players = from.players.shallowCopy()
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: TournamentProperties,
        nbt: CompoundTag,
    ): TournamentProperties {
        val tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.name = nbt.getString(TOURNAMENT_NAME_KEY)
        mutable.tournamentID = tournamentID
        mutable.tournamentStatus = enumValueOf<TournamentStatus>(nbt.getString(TOURNAMENT_STATUS_KEY))
        mutable.tournamentType = enumValueOf<TournamentType>( nbt.getString(TOURNAMENT_TYPE_KEY))
        mutable.challengeFormat = enumValueOf<ChallengeFormat>(nbt.getString(CHALLENGE_FORMAT_KEY))
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

    override fun saveToNBTHelper(
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

    override fun loadFromNBTHelper(nbt: CompoundTag): TournamentProperties {
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

    private fun saveRoundData(
        rounds: Map<UUID, TournamentRound>,
        nbt: CompoundTag,
    ): CompoundTag {
        var size = 0
        rounds.forEach { (_, round) ->
            nbt.put((ROUND_DATA_KEY + (size++)), round.saveToNBT(nbt = CompoundTag()))
        }
        nbt.putInt(StoreDataKeys.SIZE, size)
        return nbt
    }

    private fun loadRoundData(nbt: CompoundTag): MutableMap<UUID, TournamentRound> {
        val map = mutableMapOf<UUID, TournamentRound>()
        val size = nbt.getInt(StoreDataKeys.SIZE)
        for (i in 0 until size) {
            val round = TournamentRound.loadFromNBT(nbt = nbt.getCompound((ROUND_DATA_KEY + i)))
            map[round.uuid] = round
        }
        return map
    }

    private fun loadMatchData(tournamentID: UUID): MutableMap<UUID, TournamentMatch> {
        val map = mutableMapOf<UUID, TournamentMatch>()
        TournamentStoreManager.getStoreIterator(
            storeClass = MatchStore::class.java,
            storeID = tournamentID,
        ).forEach { match ->
            map[match.uuid] = match
        }
        return map
    }

    private fun loadPlayerData(tournamentID: UUID): MutableMap<UUID, TournamentPlayer> {
        val map = mutableMapOf<UUID, TournamentPlayer>()
        TournamentStoreManager.getStoreIterator(
            storeClass = PlayerStore::class.java,
            storeID = tournamentID,
        ).forEach { player ->
            map[player.uuid] = player
        }
        return map
    }

    override fun logDebugHelper(properties: TournamentProperties) {
        Util.report(("Tournament \"${properties.name}\" [${ChatUtil.shortUUID( properties.tournamentID )}]"))
        Util.report(("- ${properties.tournamentType} [${properties.challengeFormat}]"))
        Util.report(("- Max Participants: ${properties.maxParticipants}"))
        Util.report(("- Team Size (${properties.teamSize}) - Group Size (${properties.groupSize})"))
        Util.report(("- Level Range [Min: ${properties.minLevel}, Max: ${properties.maxLevel}]"))
        Util.report(("- Show Preview: ${properties.showPreview}"))
        if (properties.rounds.isNotEmpty() || properties.matches.isNotEmpty()) {
            Util.report(("- Rounds (${properties.rounds.size}) - Matches (${properties.matches.size})"))
        }
        if (properties.players.isNotEmpty()) {
            Util.report(("  Players ${properties.players.size}:"))
            properties.players.forEach { (playerID, player) ->
                Util.report(("  - ${player.name} [${ChatUtil.shortUUID(uuid = playerID)}]"))
            }
        }
    }

    override fun displayInChatHelper(
        properties: TournamentProperties,
        player: ServerPlayer
    ) {
        displayTitleInChatHelper(properties, player)
        displaySlimInChatHelper(properties, player)

        if (properties.rounds.isNotEmpty() || properties.matches.isNotEmpty()) {
            val titleText = ChatUtil.formatText(
                text = "  Rounds ",
                color = ChatUtil.yellow,
                bold = true,
            )
            titleText.append(ChatUtil.formatTextBracketed(
                text = "${properties.rounds.size}",
                color = ChatUtil.yellow,
            ))
            titleText.append(ChatUtil.formatText(text = " - "))
            titleText.append(ChatUtil.formatText(
                text = "Matches ",
                color = ChatUtil.yellow,
                bold = true,
            ))
            titleText.append(ChatUtil.formatTextBracketed(
                text = "${properties.matches.size}",
                color = ChatUtil.yellow,
            ))
            player.displayClientMessage(titleText, (false))
        }
        if (properties.players.isNotEmpty()) {
            val titleText = ChatUtil.formatText(
                text = "  Players ",
                color = ChatUtil.aqua,
                bold = true,
            )
            titleText.append(ChatUtil.formatTextBracketed(
                text = "${properties.players.size}",
                color = ChatUtil.aqua,
            ))
            player.displayClientMessage(titleText, (false))

            properties.players.forEach { (playerID, tournamentPlayer) ->
                val playerText = ChatUtil.formatText(text = "    \"")
                playerText.append(ChatUtil.formatText(
                    text = tournamentPlayer.name,
                    color = ChatUtil.aqua,
                ))
                playerText.append(ChatUtil.formatText(text = "\" "))
                playerText.append(ChatUtil.formatTextBracketed(
                    text = ChatUtil.shortUUID(uuid = playerID),
                    color = ChatUtil.aqua,
                ))
                player.displayClientMessage(playerText, (false))
            }
        }
    }

    private fun displayTitleInChatHelper(
        properties: TournamentProperties,
        player: ServerPlayer,
    ) {
        val title = ChatUtil.formatText(
            text = "Tournament ",
            color = ChatUtil.green,
        )
        title.append(ChatUtil.formatText(text = "\""))
        title.append(ChatUtil.formatText(
            text = properties.name,
            color = ChatUtil.green,
        ))
        title.append(ChatUtil.formatText(text = "\" "))
        title.append(ChatUtil.formatTextBracketed(
            text = ChatUtil.shortUUID(uuid = properties.tournamentID),
            color = ChatUtil.green,
        ))
        title.append(ChatUtil.formatText(text = " "))
        title.append(ChatUtil.formatTextBracketed(
            text = properties.tournamentStatus.name,
            color = ChatUtil.yellow,
        ))
        player.displayClientMessage(title, (false))
    }

    fun displaySlimInChatHelper(
        properties: TournamentProperties,
        player: ServerPlayer,
    ) {
        val typeAndFormat = ChatUtil.formatTextBracketed(
            text = "${properties.tournamentType}",
            color = ChatUtil.yellow,
            spacingBefore = "  ",
        )
        typeAndFormat.append( ChatUtil.formatTextBracketed(
            text = "${properties.challengeFormat}",
            color = ChatUtil.yellow,
            spacingBefore = " ",
        ))
        player.displayClientMessage(typeAndFormat, (false))

        val maxParticipants = ChatUtil.formatText(text = "  Max Participants ")
        maxParticipants.append(ChatUtil.formatTextBracketed(
            text = "${properties.maxParticipants}",
            color = ChatUtil.yellow,
        ))
        player.displayClientMessage(maxParticipants, (false))

        /*
        val teamSize = ChatUtil.formatText(text = "  Team Size ")
        teamSize.append(ChatUtil.formatTextBracketed(
            text = "${properties.teamSize}",
            color = ChatUtil.yellow,
        ))
        player.displayClientMessage(teamSize, (false))

        val groupSize = ChatUtil.formatText(text = "  Group Size ")
        groupSize.append(ChatUtil.formatTextBracketed(
            text = "${properties.groupSize}",
            color = ChatUtil.yellow,
        ))
        player.displayClientMessage(groupSize, (false))
         */

        // TODO temp until level range is released for CobblemonChallenge
        val level = ChatUtil.formatText(text = "  Level ")
        level.append(ChatUtil.formatTextBracketed(
            text = "${properties.maxLevel}",
            color = ChatUtil.yellow,
        ))
        player.displayClientMessage(level, (false))

       /*
        val levelRange = ChatUtil.formatText(text = "  Level Range: Min ")
        levelRange.append(ChatUtil.formatTextBracketed(
            text = "${properties.minLevel}",
            color = ChatUtil.yellow,
        ))
        levelRange.append(ChatUtil.formatText(text = " Max "))
        levelRange.append(ChatUtil.formatTextBracketed(
            text = "${properties.maxLevel}",
            color = ChatUtil.yellow,
        ))
        player.displayClientMessage(levelRange, (false))
        */

        val preview = ChatUtil.formatText(text = "  Show Preview ")
        preview.append( ChatUtil.formatTextBracketed(
            text = "${properties.showPreview}",
            color = ChatUtil.yellow,
        ))
        player.displayClientMessage(preview, (false))
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
        val addMatchPredicate: (TournamentMatch) -> Boolean = if (fullOverview) {
            { it.matchStatus == MatchStatus.READY }
        } else {
            { true }
        }
        for (match in matchStore.iterator()) {
            if ( addMatchPredicate(match)) {
                matches.add(match)
            }
        }

        if (matches.isEmpty()) {
            return
        }

        matches.sortBy { it.tournamentMatchIndex }
        val firstMatch = matches[0]
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "Round ${firstMatch.roundID} [${ChatUtil.shortUUID( firstMatch.roundID )}]",
            color = ChatUtil.yellow,
            bold = true,
        )
        var roundIndex = firstMatch.roundIndex
        for (match in matches) {
            if (roundIndex != match.roundIndex) {
                roundIndex = match.roundIndex
                ChatUtil.displayInPlayerChat(
                    player = player,
                    text = "Round $roundIndex [${ChatUtil.shortUUID( match.roundID )}]",
                    color = ChatUtil.yellow,
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

            WatchedMatches.displayMatchDetails(
                player = player,
                playerOneName = properties.players[entry?.key]?.name ?: "[null]",
                playerTwoName = properties.players[playerTwoID]?.name ?: "[null]",
                tournamentName = tournament.name,
                match = match,
            )
        }
    }

    fun displayResultsInChatHelper(
        properties: TournamentProperties,
        player: ServerPlayer,
    ) {
        val predicate: (TournamentPlayer) -> Boolean = { it.finalPlacement > 0 }
        val finalizedPlayers = mutableListOf<TournamentPlayer>()
        val competingPlayers = mutableListOf<TournamentPlayer>()
        for (tournamentPlayer in properties.players.values) {
            if (predicate(tournamentPlayer)) {
                finalizedPlayers.add(tournamentPlayer)
            } else {
                competingPlayers.add(tournamentPlayer)
            }
        }
        if (finalizedPlayers.isNotEmpty()) {
            displayTitleInChatHelper(properties, player)
            displaySlimInChatHelper(properties, player)

            ChatUtil.displayInPlayerChat(
                player = player,
                text = "Final Placements:",
                color = ChatUtil.green,
            )
            for (tournamentPlayer in finalizedPlayers) {
                displayFinalPlacementInChat(player, tournamentPlayer)
            }
        }
        if (competingPlayers.isNotEmpty()) {
            ChatUtil.displayInPlayerChat(
                player = player,
                text = "Players still competing:",
                color = ChatUtil.green )
            for (tournamentPlayer in competingPlayers) {
                displayFinalPlacementInChat(player, tournamentPlayer)
            }
        }
    }

    private fun displayFinalPlacementInChat(
        player: ServerPlayer,
        tournamentPlayer: TournamentPlayer,
    ) {
        val text = ChatUtil.formatTextBracketed(
            text = tournamentPlayer.finalPlacement.toString(),
            color = ChatUtil.green,
            spacingBefore = "  ",
            bold = true,
        )
        text.append(ChatUtil.formatText(text = " Player "))
        text.append(ChatUtil.formatTextQuoted(
            text = tournamentPlayer.name,
            color = ChatUtil.aqua,
            spacingAfter = " ",
            bold = true,
            ))
        text.append(ChatUtil.formatTextBracketed(
            text    = ChatUtil.shortUUID(uuid = tournamentPlayer.uuid),
            color   = ChatUtil.aqua,
        ))
        text.append(ChatUtil.formatText(text = " Original Seed "))
        text.append(ChatUtil.formatTextBracketed(text = tournamentPlayer.originalSeed.toString()))
        player.displayClientMessage(text, (false))
    }

}
