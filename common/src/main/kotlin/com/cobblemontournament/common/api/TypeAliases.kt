package com.cobblemontournament.common.api

import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.sg8.collections.reactive.map.MutableObservableMap
import com.sg8.collections.reactive.map.ObservableMap
import java.util.UUID
import kotlin.collections.Map.Entry


typealias RoundMap = ObservableMap<UUID, TournamentRound>
typealias MutableRoundMap = MutableObservableMap<UUID, TournamentRound>
typealias RoundEntry = Entry<UUID, TournamentRound>

typealias MatchMap = ObservableMap<UUID, TournamentMatch>
typealias MutableMatchMap = MutableObservableMap<UUID, TournamentMatch>

typealias PlayerMap = ObservableMap<UUID, TournamentPlayer>
typealias MutablePlayerMap = MutableObservableMap<UUID, TournamentPlayer>


typealias RoundSet = Set<TournamentRound>
typealias MatchSet = Set<TournamentMatch>
typealias PlayerSet = Set<TournamentPlayer>

