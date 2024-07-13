package com.cobblemontournament.common.api.tournament

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.api.storage.MatchStore
import com.cobblemontournament.common.api.storage.PlayerStore
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.Tournament
import org.slf4j.helpers.Util

data class TournamentData(
    val tournament      : Tournament,
    val rounds          : Set <TournamentRound>,
    val matches         : Set <TournamentMatch>,
    val players         : Set <TournamentPlayer> )
{
    fun sendAllToManager()
    {
        sendTournamentToManager()
        sendMatchesToManager()
        sendPlayersToManager()
    }

    private fun sendTournamentToManager() {
        TournamentStoreManager.addInstance(
            storeClass  = TournamentStore::class.java,
            storeID     = TournamentStoreManager.activeStoreKey,
            instance    = tournament )
    }
    private fun sendMatchesToManager() {
        matches.forEach {
            TournamentStoreManager.addInstance(
                storeClass  = MatchStore::class.java,
                storeID     = tournament.tournamentID,
                instance    = it )
        }
    }
    private fun sendPlayersToManager() {
        players.forEach {
            TournamentStoreManager.addInstance(
                storeClass  = PlayerStore::class.java,
                storeID     = tournament.tournamentID,
                instance    = it )
        }
    }

    fun printTournamentDataDebug()
    {
        printTournamentDebug()
        printTournamentRoundDebug()
        printTournamentMatchDebug()
        printTournamentPlayerDebug()
    }

    fun printTournamentDebug() = tournament.printProperties()

    fun printTournamentRoundDebug()
    {
        val sortedRounds = rounds.sortedBy { it.roundIndex }
        sortedRounds.forEach {
            Util.report("")
            it.printProperties()
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
