package com.cobblemontournament.common.api

import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemontournament.common.api.storage.MatchStore
import com.cobblemontournament.common.api.storage.PlayerStore
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.util.ChatUtil
import com.mojang.brigadier.Command
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object WatchedMatches {

    private val watchedMatches = mutableSetOf<TournamentMatch>()

    fun handlePlayerLogoutEvent(player: ServerPlayer) {
        watchedMatches.removeIf { it.containsPlayer( player.uuid ) }
    }

    fun displayAllPlayerMatches(player: ServerPlayer) {
        val tournaments = getTournamentsWithPlayer(player.uuid)
        if (tournaments.isEmpty()) {
            ChatUtil.displayInPlayerChat(
                player = player,
                text = "${player.name.string} is not registered for any active tournaments.",
                )
            return
        }
        for (tournament in tournaments) {
            handleMatchChallengeRequest(tournament, player)
        }
    }

    private fun getTournamentsWithPlayer(playerID: UUID): Set<Tournament> {
        return TournamentStoreManager.getValuesFromStore(
            storeClass = TournamentStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            predicate = { t -> t.containsPlayerID( playerID ) },
            action = { t -> t },
            )
    }

    private fun getMatchesWithPlayer(playerID: UUID): Set<TournamentMatch> {
        return TournamentStoreManager.getValuesFromStore(
            storeClass = TournamentStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            predicate = { t -> t.getCurrentMatch( playerID ) != null },
            action = { t -> t.getCurrentMatch( playerID )!! }, // '!!' safe b/c null check in method
            )
    }

    fun handleBattleVictoryEvent(event: BattleVictoryEvent) {
        if (event.wasWildCapture) {
            return
        }

        val victorID = event.winners.first().uuid
        val matches = getWatchedMatchesWithPlayer(victorID)
        if ( matches.isEmpty() ) {
            return
        }

        // TODO handle doubles & other match types when implemented
        //  - should just be one for now
        val validMatches = mutableSetOf<TournamentMatch>()
        for (match in matches) {
            if (event.losers.any { match.containsPlayer(it.uuid) }) {
                validMatches.add(match)
            }
        }
        val player = PlayerManager.getServerPlayer(event.winners.first().uuid)
        return when {
            validMatches.isNotEmpty() -> validMatches.first().updateVictorID(victorID)
            player == null -> Util.report("Failed to get victor ServerPlayer for current match.")
            else -> {
                ChatUtil.displayInPlayerChat(
                    player = player,
                    text = "Failed to get victor ServerPlayer for current match.",
                )
            }
        }
    }

    private fun getWatchedMatchesWithPlayer(uuid: UUID?): List<TournamentMatch> {
        val matches = mutableListOf<TournamentMatch>()
        if (uuid != null) {
            for (match in watchedMatches.takeWhile { it.containsPlayer(uuid) }) {
                matches.add(match)
            }
        }
        return matches
    }

    fun handleMatchChallengeRequest(
        tournament: Tournament,
        challenger: ServerPlayer,
        nullableMatch: TournamentMatch? = null,
    ) {
        val player = TournamentStoreManager.getInstance(
            storeClass  = PlayerStore::class.java,
            storeID     = tournament.uuid,
            instanceID  = challenger.uuid,
            )

        if (player != null && player.currentMatchID == null) {
            val text = ChatUtil.formatText(
                text = "${player.name} ",
                color = ChatUtil.aqua,
                )
            text.append(ChatUtil.formatText(text = "finished in "))
            text.append(ChatUtil.formatTextBracketed(
                text    = player.finalPlacement.toString(),
                color   = ChatUtil.green,
                bold    = true,
                )
            )
            text.append(ChatUtil.formatText(text = " place!"))
            challenger.displayClientMessage(text, false )
            return
        }

        // TODO handle NPCs
        val match: TournamentMatch? = if (player != null) {
            nullableMatch ?: TournamentStoreManager.getInstance(
                storeClass = MatchStore::class.java,
                storeID = tournament.uuid,
                instanceID = player.currentMatchID!!,
            )
        } else {
            null
        }

        val playerMap = match?.playerEntrySet() ?: mapOf()

        val serverPlayers = PlayerManager.getServerPlayers(playerMap.keys.toSet())

        val challengerTeam = playerMap.firstNotNullOfOrNull {
            if (it.key == challenger.uuid) {
                it.value
            } else {
                null
            }
        }

        val opponentID = playerMap.firstNotNullOfOrNull {
            if (it.key != challenger.uuid && it.value != challengerTeam) {
                it.key
            } else {
                null
            }
        }

        val opponent = if (opponentID != null) {
            serverPlayers.firstOrNull { it.uuid == opponentID }
        } else {
            null
        }

        val playerTwoName = when {
            opponent != null -> opponent.name.string
            opponentID != null -> ChatUtil.shortUUID(opponentID)
            else -> "[opponent]"
        }

        val insert: MutableComponent? = when{
            player == null -> {
                ChatUtil.formatTextBracketed(
                    text = "Match was null",
                    color = ChatUtil.yellow,
                    )
            }
            match == null -> {
                ChatUtil.formatTextBracketed(
                    text = "Match was null",
                    color = ChatUtil.yellow,
                    )
            }
            serverPlayers.size < playerMap.size -> {
                ChatUtil.formatTextBracketed(
                    text = "Participant offline",
                    color = ChatUtil.yellow,
                    )
            }
            opponent != null -> {
                watchedMatches.add(match)
                handleChallengeInteractable(
                    opponent = opponent,
                    tournament = tournament,
                    )
            }
            else -> null
        }

        displayMatchDetails(
            player = challenger,
            playerOneName = challenger.name.string,
            playerTwoName = playerTwoName,
            tournamentName = tournament.name,
            match = match,
            insert = insert,
            )
    }

    fun displayMatchDetails(
        player: ServerPlayer,
        playerOneName: String,
        playerTwoName: String,
        tournamentName: String,
        match: TournamentMatch?,
        insert: Component? = null,
    ): Int {
        val title = ChatUtil.formatText(text = "\"")
        title.append(ChatUtil.formatText(text = tournamentName, color = ChatUtil.green))
        title.append(ChatUtil.formatText(text = "\" ${match?.name} "))
        title.append(ChatUtil.formatText(text = playerOneName, color = ChatUtil.aqua))
        title.append(ChatUtil.formatText(text = " vs ", color = ChatUtil.purple))
        title.append(ChatUtil.formatText(text = playerTwoName, color = ChatUtil.aqua))
        val status = ChatUtil.formatText(text = "  Match Status ", bold = true)
        val statusColor = if (match != null) {
            getStatusColor(match.matchStatus)
        } else ChatUtil.white
        status.append( ChatUtil.formatTextBracketed(
            text = "${match?.matchStatus}",
            color = statusColor,
            bold = true,
            )
        )
        if (insert != null) {
            status.append(ChatUtil.formatText(text = " "))
            status.append(insert)
        }
        player.displayClientMessage(title, false)
        player.displayClientMessage(status, false)
        return Command.SINGLE_SUCCESS
    }

    fun handleChallengeInteractable(
        opponent: ServerPlayer,
        tournament: Tournament,
    ): MutableComponent {
        return CommandUtil.createChallengeMatchInteractable(
            text = "Click to Challenge!",
            tournament = tournament,
            opponent = opponent,
            color = ChatUtil.green,
            bracketed = true,
            )
    }

    private fun getStatusColor(matchStatus: MatchStatus): String {
        return when (matchStatus) {
            MatchStatus.ERROR
            ->
                ChatUtil.red
            MatchStatus.UNKNOWN,
            MatchStatus.EMPTY,
            MatchStatus.PENDING,
            MatchStatus.NOT_READY
            ->
                ChatUtil.yellow
            MatchStatus.READY,
            MatchStatus.IN_PROGRESS,
            MatchStatus.COMPLETE,
            MatchStatus.FINALIZED
            ->
                ChatUtil.green
        }
    }

}
