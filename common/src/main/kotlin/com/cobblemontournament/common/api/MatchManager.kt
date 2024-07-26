package com.cobblemontournament.common.api

import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.match.*
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.someguy.storage.util.PlayerID
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object MatchManager {

    private val watchedMatches = mutableSetOf<TournamentMatch>()

    fun handlePlayerLogoutEvent(player: ServerPlayer) {
        watchedMatches.removeIf { it.containsPlayer(playerID = player.uuid) }
    }

    fun displayAllPlayerMatches(player: ServerPlayer) {
        val tournaments = getTournamentsWithPlayer(playerID = player.uuid)
        if (tournaments.isEmpty()) {
            player.displayInChat(
                text = "${player.name.string} is not registered for any active tournaments.",
            )
            return
        }
        for (tournament in tournaments) {
            handleMatchChallengeRequest(tournament = tournament, challenger = player)
        }
    }

    private fun getTournamentsWithPlayer(playerID: PlayerID): Set<Tournament> {
        return TournamentStoreManager.getValuesFromInstances(
            storeClass = TournamentStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            predicate = { t -> t.containsPlayer(playerID = playerID) },
            handler = { t -> t },
        )
    }

    fun handleBattleVictoryEvent(event: BattleVictoryEvent) {
        if (event.wasWildCapture) {
            return
        }

        val victorID = event.winners.first().uuid
        val matches = getWatchedMatchesWithPlayer(uuid = victorID)
        if (matches.isEmpty()) {
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

        val player = CobblemonTournament.getServerPlayer(playerID = (event.winners.first().uuid))
        return when {
            validMatches.isNotEmpty() -> {
                validMatches.first().updateVictorID(newVictorID = victorID)
            }
            player == null -> {
                Util.report(("Failed to get victor ServerPlayer for current match."))
            }
            else -> {
                player.displayInChat(text = "Failed to get victor ServerPlayer for current match.")
            }
        }
    }

    private fun getWatchedMatchesWithPlayer(uuid: UUID?): List<TournamentMatch> {
        val matches = mutableListOf<TournamentMatch>()
        if (uuid != null) {
            for (match in (watchedMatches.takeWhile { it.containsPlayer(uuid) })) {
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
            storeClass = PlayerStore::class.java,
            storeID = tournament.uuid,
            instanceID = challenger.uuid,
        )

        if (player != null && player.currentMatchID == null) {
            return handleFinalizedPlayer(player = player, challenger = challenger)
        }

        // TODO handle NPCs
        val match = player?.currentMatchID?.let { matchID ->
            nullableMatch ?: TournamentStoreManager.getInstance(
                storeClass = MatchStore::class.java,
                storeID = tournament.uuid,
                instanceID = matchID,
            )
        }

        val playerMap = match?.playerEntrySet() ?: mapOf()

        val serverPlayers = CobblemonTournament.getServerPlayers(playerMap.keys.toSet())

        val challengerTeam = playerMap.firstNotNullOfOrNull { (playerID, team) ->
            if (playerID == challenger.uuid) team else null
        }

        val opponentID = playerMap.firstNotNullOfOrNull { (playerID, team) ->
            if (playerID != challenger.uuid && team != challengerTeam) {
                return@firstNotNullOfOrNull playerID
            } else {
                return@firstNotNullOfOrNull null
            }
        }

        val opponent = serverPlayers.firstOrNull { it.uuid == opponentID }

        val playerTwoName = when {
            opponent != null -> opponent.name.string
            opponentID != null -> opponentID.shortUUID()
            else -> "[opponent]"
        }

        val insert: MutableComponent? = when {
            player == null -> {
                getBracketedComponent(text = "Match was null", textColor = ChatUtil.YELLOW_FORMAT)
            }
            match == null -> {
                getBracketedComponent(text = "Match was null", textColor = ChatUtil.YELLOW_FORMAT)
            }
            serverPlayers.size < playerMap.size -> {
                getBracketedComponent(
                    text = "Participant offline",
                    textColor = ChatUtil.YELLOW_FORMAT,
                )
            }
            opponent != null -> {
                watchedMatches.add(match)
                handleChallengeInteractable(opponent = opponent, tournament = tournament)
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

    private fun handleFinalizedPlayer(player: TournamentPlayer, challenger: ServerPlayer) {
        val text = getComponent(text = "${player.name} ", color = ChatUtil.AQUA_FORMAT)
        text.appendWith(text = "finished in ")
        text.appendWithBracketed(
            text = player.finalPlacement.toString(),
            textColor = ChatUtil.GREEN_FORMAT,
            bold = true,
        )
        text.appendWith(text = " place!")
        challenger.displayClientMessage(text, false)
        return
    }

    fun displayMatchDetails(
        player: ServerPlayer,
        playerOneName: String,
        playerTwoName: String,
        tournamentName: String,
        match: TournamentMatch?,
        insert: Component? = null,
    ): Int {
        val title = getComponent(text = "\"")
        title.appendWith(text = tournamentName, color = ChatUtil.GREEN_FORMAT)
        title.appendWith(text = "\" ${match?.name} ")
        title.appendWith(text = playerOneName, color = ChatUtil.AQUA_FORMAT)
        title.appendWith(text = " vs ", color = ChatUtil.PURPLE_FORMAT)
        title.appendWith(text = playerTwoName, color = ChatUtil.AQUA_FORMAT)

        val status = getComponent(text = "  Match Status ", bold = true)
        val statusColor = match?.let {
            getStatusColor(it.getUpdatedMatchStatus())
        }?: ChatUtil.WHITE_FORMAT

        status.appendWithBracketed(
            text = "${match?.getUpdatedMatchStatus()}",
            textColor = statusColor,
            bold = true,
        )

        insert?.let { component ->
            status.appendWith(text = " ")
            status.append(component)
        }
        
        player.displayClientMessage(title, false)
        player.displayClientMessage(status, false)
        return Command.SINGLE_SUCCESS
    }

    private fun handleChallengeInteractable(
        opponent: ServerPlayer,
        tournament: Tournament,
    ): MutableComponent {
        return CommandUtil.createChallengeMatchInteractable(
            text = "Click to Challenge!",
            tournament = tournament,
            opponent = opponent,
            color = ChatUtil.GREEN_FORMAT,
            bracketed = true,
        )
    }

    private fun getStatusColor(matchStatus: MatchStatus): String {
        return when (matchStatus) {
            MatchStatus.ERROR -> ChatUtil.RED_FORMAT

            MatchStatus.UNKNOWN,
            MatchStatus.EMPTY,
            MatchStatus.PENDING,
            MatchStatus.NOT_READY -> ChatUtil.YELLOW_FORMAT

            MatchStatus.READY,
            MatchStatus.IN_PROGRESS,
            MatchStatus.COMPLETE,
            MatchStatus.FINALIZED -> ChatUtil.GREEN_FORMAT
        }
    }

}
