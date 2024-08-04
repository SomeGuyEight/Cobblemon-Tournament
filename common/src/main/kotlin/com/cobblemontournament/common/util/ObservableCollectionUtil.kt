package com.cobblemontournament.common.util

import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import java.util.UUID


val ROUND_MAP_HANDLER = { (_, r): Map.Entry<UUID, TournamentRound> -> setOf(r.getObservable()) }
val MATCH_MAP_HANDLER = { (_, r): Map.Entry<UUID, TournamentMatch> -> setOf(r.getObservable()) }
val PLAYER_MAP_HANDLER = { (_, r): Map.Entry<UUID, TournamentPlayer> -> setOf(r.getObservable()) }
