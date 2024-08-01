package com.cobblemontournament.common.api.tournament

import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.storage.store.MatchStore
import com.cobblemontournament.common.api.storage.store.PlayerStore
import com.cobblemontournament.common.api.storage.store.TournamentStore
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.Tournament

data class TournamentData(
    val tournament: Tournament,
    val rounds: Set<TournamentRound>,
    val matches: Set<TournamentMatch>,
    val players: Set<TournamentPlayer>,
) {

    fun saveAll() {
        saveTournament()
        saveMatches()
        savePlayers()
    }

    private fun saveTournament() {
        TournamentStoreManager.addInstance(
            storeClass = TournamentStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            instance = tournament,
        )
    }

    private fun saveMatches() {
        for (match in matches) {
            TournamentStoreManager.addInstance(
                storeClass = MatchStore::class.java,
                storeID = tournament.uuid,
                instance = match,
            )
        }
    }

    private fun savePlayers() {
        for (player in players) {
            TournamentStoreManager.addInstance(
                storeClass = PlayerStore::class.java,
                storeID = tournament.uuid,
                instance = player,
            )
        }
    }

}
