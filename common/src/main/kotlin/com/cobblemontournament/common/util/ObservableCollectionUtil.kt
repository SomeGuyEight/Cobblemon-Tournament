package com.cobblemontournament.common.util

import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import java.util.*

val roundMapHandler = { (_, r): Map.Entry<UUID, TournamentRound> -> setOf(r.getObservable()) }
val matchMapHandler = { (_, r): Map.Entry<UUID, TournamentMatch> -> setOf(r.getObservable()) }
val playerMapHandler = { (_, r): Map.Entry<UUID, TournamentPlayer> -> setOf(r.getObservable()) }
