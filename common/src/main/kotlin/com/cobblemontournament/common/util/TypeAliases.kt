package com.cobblemontournament.common.util

import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.round.TournamentRound
import java.util.UUID

typealias RoundMap = MutableMap<UUID, TournamentRound>
typealias MatchMap = MutableMap<UUID, TournamentMatch>
typealias PlayerMap = MutableMap<UUID, TournamentPlayer>

typealias RoundSet = Set<TournamentRound>
typealias MatchSet = Set<TournamentMatch>
typealias PlayerSet = Set<TournamentPlayer>

typealias OrderedPlayerProperties = List<PlayerProperties>

typealias IndexedMatchMap = MutableMap<Int, UUID>
typealias PlayerToTeamMap = MutableMap<UUID, Int>

typealias TournamentBuilderID = UUID
typealias TournamentID = UUID
typealias RoundID = UUID
typealias MatchID = UUID
typealias VictorID = UUID
