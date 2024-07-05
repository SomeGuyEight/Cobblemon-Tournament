package com.cobblemontournament.common.tournament

import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import org.slf4j.helpers.Util

data class TournamentData(
    val tournament      : Tournament,
    val rounds          : Set<TournamentRound>,
    val matches         : Set<TournamentMatch>,
    val players         : Set<TournamentPlayer>)
{
    fun sendAllToManager(saveStore: Boolean = true)
    {
        sendTournamentToManager(saveStore)
        sendRoundsToManager(saveStore)
        sendMatchesToManager(saveStore)
        sendPlayersToManager(saveStore)
    }

    fun sendTournamentToManager(saveStore: Boolean = true) {
        TournamentManager.addTournament(tournament,saveStore)
    }
    fun sendRoundsToManager(saveStore: Boolean = true) {
        rounds.forEach { TournamentManager.addRound(it,saveStore) }
    }
    fun sendMatchesToManager(saveStore: Boolean = true) {
        matches.forEach { TournamentManager.addMatch(it,saveStore) }
    }
    fun sendPlayersToManager(saveStore: Boolean = true) {
        players.forEach { TournamentManager.addPlayer(it,saveStore) }
    }

    fun printTournamentDataDebug()
    {
        printTournamentDebug()
        printTournamentRoundDebug()
        printTournamentMatchDebug()
        printTournamentPlayerDebug()
    }

    fun printTournamentDebug() = tournament.getProperties().printProperties()

    fun printTournamentRoundDebug()
    {
        val sortedRounds = rounds.sortedBy { it -> it.roundIndex }
        sortedRounds.forEach { round ->
            Util.report("")
            round.getProperties().printProperties()
        }
    }

    fun printTournamentMatchDebug()
    {
        val sortedMatches = matches.sortedBy { it.tournamentMatchIndex }
        sortedMatches.forEach {
            Util.report("")
            it.printProperties()
        }
    }

    fun printTournamentPlayerDebug()
    {
        val sortedPlayers = players.sortedBy { it.seed }
        sortedPlayers.forEach {
            Util.report("")
            it.printProperties()
        }
    }
}
