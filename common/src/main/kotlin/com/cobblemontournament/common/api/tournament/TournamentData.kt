package com.cobblemontournament.common.api.tournament

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.api.storage.MatchStore
import com.cobblemontournament.common.api.storage.PlayerStore
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.Tournament

data class TournamentData(
    val tournament: Tournament,
    val rounds: Set <TournamentRound>,
    val matches: Set <TournamentMatch>,
    val players: Set <TournamentPlayer>,
) {

    fun sendAllToManager() {
        sendTournamentToManager()
        sendMatchesToManager()
        sendPlayersToManager()
    }

    private fun sendTournamentToManager() {
        TournamentStoreManager.addInstance(
            storeClass  = TournamentStore::class.java,
            storeID     = TournamentStoreManager.ACTIVE_STORE_ID,
            instance    = tournament
        )
    }

    private fun sendMatchesToManager() {
        for (match in matches) {
            TournamentStoreManager.addInstance(
                storeClass  = MatchStore::class.java,
                storeID     = tournament.uuid,
                instance    = match
            )
        }
    }

    private fun sendPlayersToManager() {
        for (player in players) {
            TournamentStoreManager.addInstance(
                storeClass  = PlayerStore::class.java,
                storeID     = tournament.uuid,
                instance    = player
            )
        }
    }

}
