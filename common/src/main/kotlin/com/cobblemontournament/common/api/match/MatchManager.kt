package com.cobblemontournament.common.api.match

import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.storage.store.MatchStore
import com.cobblemontournament.common.api.storage.store.PlayerStore
import com.cobblemontournament.common.api.storage.store.TournamentStore
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.tournament.Tournament
import com.mojang.brigadier.Command
import com.sg8.collections.reactive.map.observableMapOf
import com.sg8.util.appendWith
import com.sg8.util.appendWithBracketed
import com.sg8.util.ComponentUtil
import com.sg8.util.displayInChat
import com.sg8.util.short
import net.minecraft.ChatFormatting
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
        val tournaments = getTournamentsWithPlayer(playerUuid = player.uuid)
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

    private fun getTournamentsWithPlayer(playerUuid: UUID): Set<Tournament> {
        return TournamentStoreManager.getInstances(
            storeClass = TournamentStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            predicate = { t -> t.containsPlayer(playerID = playerUuid) },
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

        val player = CobblemonTournament.getServerPlayer(playerUuid = (event.winners.first().uuid))
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

        val playerMap = match?.playerMap ?: observableMapOf()

        val serverPlayers = CobblemonTournament.getServerPlayers(playerMap.keys.toSet())

        val challengerTeam = playerMap.firstValueOrNull { it.key == challenger.uuid }

        val opponentID = run loop@{
            playerMap.forEach { (playerID, team) ->
                if (playerID != challenger.uuid && team != challengerTeam) {
                    return@loop playerID
                }
            }
            null
        }

        val opponent = serverPlayers.firstOrNull { it.uuid == opponentID }

        val playerTwoName = when {
            opponent != null -> opponent.name.string
            opponentID != null -> opponentID.short()
            else -> "[opponent]"
        }

        val insert: MutableComponent? = when {
            player == null -> {
                ComponentUtil.getBracketedComponent(
                    text = "Match was null",
                    textColor = ChatFormatting.YELLOW,
                )
            }
            match == null -> {
                ComponentUtil.getBracketedComponent(
                    text = "Match was null",
                    textColor = ChatFormatting.YELLOW,
                )
            }
            serverPlayers.size < playerMap.size -> {
                ComponentUtil.getBracketedComponent(
                    text = "Participant offline",
                    textColor = ChatFormatting.YELLOW,
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
        val text = ComponentUtil.getComponent(text = "${player.name} ", color = ChatFormatting.AQUA)
        text.appendWith(text = "finished in ")
        text.appendWithBracketed(
            text = player.finalPlacement.toString(),
            textColor = ChatFormatting.GREEN,
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
        val title = ComponentUtil.getComponent(text = "\"")
        title.appendWith(text = tournamentName, color = ChatFormatting.GREEN)
        title.appendWith(text = "\" ${match?.name} ")
        title.appendWith(text = playerOneName, color = ChatFormatting.AQUA)
        title.appendWith(text = " vs ", color = ChatFormatting.LIGHT_PURPLE)
        title.appendWith(text = playerTwoName, color = ChatFormatting.AQUA)

        val status = ComponentUtil.getComponent(text = "  Match Status ", bold = true)
        val statusColor = match?.let {
            getStatusColor(it.getUpdatedMatchStatus())
        } ?: ChatFormatting.WHITE

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
            color = ChatFormatting.GREEN,
            bracketed = true,
        )
    }

    private fun getStatusColor(matchStatus: MatchStatus): ChatFormatting {
        return when (matchStatus) {
            MatchStatus.ERROR -> ChatFormatting.RED

            MatchStatus.UNKNOWN,
            MatchStatus.EMPTY,
            MatchStatus.PENDING,
            MatchStatus.NOT_READY -> ChatFormatting.YELLOW

            MatchStatus.READY,
            MatchStatus.IN_PROGRESS,
            MatchStatus.COMPLETE,
            MatchStatus.FINALIZED -> ChatFormatting.GREEN
        }
    }

}
