package com.cobblemontournament.common.util

import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import java.util.UUID


val ROUND_MAP_HANDLER = { _: UUID, r: TournamentRound -> setOf(r.getObservable()) }
val MATCH_MAP_HANDLER = { _: UUID, r: TournamentMatch -> setOf(r.getObservable()) }
val PLAYER_MAP_HANDLER = { _: UUID, r: TournamentPlayer -> setOf(r.getObservable()) }
