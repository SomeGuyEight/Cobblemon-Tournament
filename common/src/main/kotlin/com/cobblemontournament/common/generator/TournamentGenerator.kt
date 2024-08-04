package com.cobblemontournament.common.generator

import com.cobblemontournament.common.api.MatchSet
import com.cobblemontournament.common.api.PlayerSet
import com.cobblemontournament.common.api.RoundSet
import com.cobblemontournament.common.api.tournament.TournamentData
import com.cobblemontournament.common.generator.indexedseed.IndexedSeedGenerator
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.match.properties.MatchProperties
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.round.properties.RoundProperties
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.sg8.collections.SortType
import com.sg8.collections.reactive.map.MutableObservableMap
import com.sg8.util.ceilToPowerOfTwo
import java.util.UUID


typealias OrderedPlayers = List<PlayerProperties>


object TournamentGenerator {

    fun toTournament(name: String, builder: TournamentBuilder): TournamentData? {
        if (builder.getPlayersSize() < 2) {
            return null
        }

        val properties = builder.getTournamentProperties(name)

        return when (properties.tournamentType) {
            TournamentType.SINGLE_ELIMINATION -> handleSingleElimination(builder,properties)
            TournamentType.DOUBLE_ELIMINATION -> handleDoubleElimination(builder,properties)
            TournamentType.ROUND_ROBIN -> handleRoundRobin(builder,properties)
            TournamentType.VGC -> handleVGC(builder,properties)
        }
    }

    private fun handleSingleElimination(
        builder: TournamentBuilder,
        tournamentProperties: TournamentProperties
    ): TournamentData {
        val roundCount = getRoundCount(builder, tournamentProperties)
        val firstRoundMatchCount = 1 shl (roundCount - 1)

        val roundProperties = mutableSetOf<RoundProperties>()
        val matchProperties = mutableSetOf<MatchProperties>()
        val sortedPlayers = sortPlayers(
            seededPlayers = builder.getSeededPlayers(),
            unseededPlayers = builder.getUnseededPlayers(),
            tournamentProps = tournamentProperties,
        )

        handleFirstRound(
            rounds = roundProperties,
            matches = matchProperties,
            orderedPlayerProps = sortedPlayers,
            builder = builder,
            tournamentProps = tournamentProperties,
        )

        initializeRoundsAndMatches(
            firstRoundMatchCount = firstRoundMatchCount,
            roundCount = roundCount,
            rounds = roundProperties,
            matches = matchProperties,
            tournamentProperties = tournamentProperties,
        )

        setMatchConnections(matchProperties, roundProperties)

        handleFirstRoundByes(tournamentProperties, matchProperties, sortedPlayers)

        val rounds = getRounds(roundProperties, tournamentProperties)
        val matches = getMatches(matchProperties, tournamentProperties)
        val players = getPlayers(sortedPlayers, tournamentProperties)

        rounds.forEach { tournamentProperties.roundMap[it.uuid] = it }
        matches.forEach { tournamentProperties.matchMap[it.uuid] = it }
        players.forEach { tournamentProperties.playerMap[it.uuid] = it }

        val tournament = Tournament(tournamentProperties).initialize()

        return TournamentData(tournament, rounds, matches, players)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleDoubleElimination(
        builder: TournamentBuilder,
        properties: TournamentProperties,
    ): TournamentData {
        (TODO("Not implemented yet"))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleRoundRobin(
        builder: TournamentBuilder,
        properties: TournamentProperties
    ): TournamentData {
        (TODO("Not implemented yet"))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleVGC(
        builder: TournamentBuilder,
        properties: TournamentProperties
    ): TournamentData {
        (TODO("Not implemented yet"))
    }
    
    private fun getRoundCount(builder: TournamentBuilder, properties: TournamentProperties): Int {
        return when (properties.tournamentType) {
            TournamentType.SINGLE_ELIMINATION -> getRoundCountSingleElimination(builder)
            TournamentType.DOUBLE_ELIMINATION -> getRoundCountDoubleElimination(builder)
            TournamentType.ROUND_ROBIN -> getRoundCountRoundRobin(builder)
            TournamentType.VGC -> getRoundCountVGC(builder)
        }
    }

    private fun getRoundCountSingleElimination(builder: TournamentBuilder): Int {
        var bracketSlots = builder.getPlayersSize().ceilToPowerOfTwo()
        // -1 b/c first shift divides the total players in half
        // & gets the first round match count
        var rounds = -1
        while (bracketSlots > 0) {
            bracketSlots = bracketSlots shr 1
            rounds++
        }
        return rounds
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getRoundCountDoubleElimination(builder: TournamentBuilder): Int {
        // TODO: same as first round then -> - add lower bracket
        return 0
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getRoundCountRoundRobin(builder: TournamentBuilder): Int {
        // TODO
        return 0
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getRoundCountVGC(builder: TournamentBuilder): Int {
        // TODO
        return 0
    }


    private fun sortPlayers(
        seededPlayers: List<PlayerProperties>,
        unseededPlayers: List<PlayerProperties>,
        tournamentProps: TournamentProperties,
    ): OrderedPlayers {
        val orderedPlayers = ArrayList(seededPlayers.toList())
        orderedPlayers.sortWith(Comparator.comparing(PlayerProperties::seed))

        val shuffledPlayers = ArrayDeque(unseededPlayers.shuffled())
        orderedPlayers.addAll(shuffledPlayers)

        val sameSeedQueue = ArrayDeque<PlayerProperties>()
        val size = orderedPlayers.size

        for (i in 0 until size) {
            val nextPlayer = when {
                orderedPlayers.hasDuplicateSeed(i) -> {
                    var currentIndex = i
                    val sameSeedSet = mutableSetOf<PlayerProperties>()
                    while (orderedPlayers.hasDuplicateSeed(currentIndex)) {
                        sameSeedSet.add(orderedPlayers[++currentIndex])
                    }
                    sameSeedQueue.addAll(sameSeedSet.shuffled())
                    sameSeedQueue.removeFirst()
                }
                sameSeedQueue.isNotEmpty() -> sameSeedQueue.removeFirst()
                else -> orderedPlayers[i] ?: continue
            }

            orderedPlayers.removeAt(i)
            orderedPlayers.add(
                i, // index
                PlayerProperties(
                    name = nextPlayer.name,
                    actorType = nextPlayer.actorType,
                    uuid = nextPlayer.uuid,
                    tournamentID = tournamentProps.uuid,
                    seed = i + 1,
                    originalSeed = nextPlayer.seed,
                    pokemonTeamID = nextPlayer.pokemonTeamID,
                )
            )
        }

        return orderedPlayers
    }

    private fun List<PlayerProperties>.hasDuplicateSeed(index: Int): Boolean {
        val nextIndex = index + 1
        return nextIndex != this.size && this[index].seed == this[nextIndex].seed
    }

    private fun handleFirstRound(
        rounds: MutableSet<RoundProperties>,
        matches: MutableSet<MatchProperties>,
        orderedPlayerProps: OrderedPlayers,
        builder: TournamentBuilder,
        tournamentProps: TournamentProperties,
    ) {
        val firstRoundID = UUID.randomUUID()
        val firstRoundMatches = getFirstRoundMatches(
            roundID = firstRoundID,
            orderedPlayerProperties = orderedPlayerProps,
            builder = builder,
            tournamentProperties = tournamentProps,
        )
        val firstRound = RoundProperties(
            uuid = firstRoundID,
            tournamentID = tournamentProps.uuid,
            roundIndex = 0,
            roundType = RoundType.PRIMARY,
            indexedMatchMap = MutableObservableMap(getMatchMap(firstRoundMatches)),
        )
        rounds.add(firstRound)
        matches.addAll(firstRoundMatches)
    }


    private fun getMatchMap(matches: MutableSet<MatchProperties>): MutableMap<Int,UUID> {
        val matchMap = mutableMapOf<Int,UUID>()
        matches.forEach { matchMap[it.roundMatchIndex] = it.uuid }
        return matchMap
    }

    private fun getFirstRoundMatches(
        roundID: UUID,
        orderedPlayerProperties: OrderedPlayers,
        builder: TournamentBuilder,
        tournamentProperties: TournamentProperties,
    ): MutableSet<MatchProperties> {
        val indexedSeeds = IndexedSeedGenerator.getIndexedSeedArray(
            seedCount = builder.getPlayersSize(),
            sortType = SortType.INDEX_ASCENDING,
        )
        val size = indexedSeeds.size
        val matchCount = size shr 1 // div by 2
        val matches = mutableSetOf<MatchProperties>()
        var seedIndex = 0
        val getProperties = { seed: Int ->
            orderedPlayerProperties.firstOrNull { p -> p.seed == seed }
        }

        val seedEntries = indexedSeeds.deepCopy()
        for (i in 0 until matchCount) {
            val seed1 = seedEntries[seedIndex++].seed
            val seed2 = seedEntries[seedIndex++].seed
            val player1 = getProperties(seed1)
            val player2 = getProperties(seed2)
            val playerMap = mutableMapOf<UUID,Int>()
            val matchID: UUID = UUID.randomUUID()
            player1?.let {
                player1.currentMatchID = matchID
                playerMap[player1.uuid] = 1
            }
            player2?.let {
                player2.currentMatchID = matchID
                playerMap[player2.uuid] = 2
            }
            matches.add(
                MatchProperties(
                    uuid = matchID,
                    tournamentID = tournamentProperties.uuid,
                    roundID = roundID,
                    roundIndex = 0,
                    tournamentMatchIndex = i,
                    roundMatchIndex = i,
                    playerMap = MutableObservableMap(playerMap),
                )
            )
        }

        return matches
    }

    private fun initializeRoundsAndMatches (
        firstRoundMatchCount: Int,
        roundCount: Int,
        rounds: MutableSet<RoundProperties>,
        matches: MutableSet<MatchProperties>,
        tournamentProperties: TournamentProperties,
    ) {
        var matchesThisRound = firstRoundMatchCount
        var tournamentMatchIndex = firstRoundMatchCount // b/c first round is already done
        for (roundIndex in 1 until roundCount) {
            matchesThisRound = matchesThisRound shr 1 // shr 1 ≡ (x: Int) >> 1
            val roundID = UUID.randomUUID()
            val roundMatches = mutableSetOf<MatchProperties>()
            for (i in 0 until matchesThisRound) {
                roundMatches.add(
                    MatchProperties(
                        uuid = UUID.randomUUID(),
                        tournamentID = tournamentProperties.uuid,
                        roundID = roundID,
                        roundIndex = roundIndex,
                        tournamentMatchIndex = (tournamentMatchIndex++),
                        roundMatchIndex = i,
                    )
                )
            }

            val roundMatchMap = getMatchMap(roundMatches)
            val newRound = RoundProperties(
                uuid = roundID,
                tournamentID = tournamentProperties.uuid,
                roundIndex = roundIndex,
                roundType = RoundType.PRIMARY,
                indexedMatchMap = MutableObservableMap(roundMatchMap),
            )
            rounds.add(newRound)
            matches.addAll(roundMatches)
        }
    }

    private fun setMatchConnections(
        matchProperties: Set<MatchProperties>,
        roundProperties: Set<RoundProperties>,
    ) {
        for (match in matchProperties) {
            val currentRound = roundProperties.firstOrNull { round ->
                round.uuid == match.roundID
            } ?: continue

            val nextIndex = currentRound.roundIndex + 1
            val nextRound = roundProperties.firstOrNull { it.roundIndex == nextIndex }

            if (nextRound != null) {
                Tournament.victorNextMatchIndex(
                    roundMatchIndex = match.roundMatchIndex,
                    roundIndex = currentRound.roundIndex,
                    roundCount = roundProperties.size,
                )?.let { nextMatchIndex ->
                    match.matchConnections.victorNextMatch = nextRound.getMatchID(nextMatchIndex)
                }
            }

            val previousIndex = currentRound.roundIndex - 1
            val previousRound = roundProperties.firstOrNull { it.roundIndex == previousIndex }
            if (previousRound != null) {
                val (evenIndex, oddIndex) = Tournament.previousMatchIndices(
                    roundMatchIndex = match.roundMatchIndex,
                    roundIndex = currentRound.roundIndex,
                )

                val evenMatchID = previousRound.getMatchID(evenIndex)
                val oddMatchID = previousRound.getMatchID(oddIndex)
                match.matchConnections.addPreviousMatch(evenIndex, evenMatchID)
                match.matchConnections.addPreviousMatch(oddIndex, oddMatchID)
            }
        }
    }

    private fun handleFirstRoundByes(
        tournamentProperties: TournamentProperties,
        matchProperties: Set<MatchProperties>,
        playerProperties: OrderedPlayers,
    ) {
        for (match in matchProperties) {
            if (match.roundIndex != 0 || match.playerMap.isEmpty()) {
                continue
            }
            if (match.playerMap.size == tournamentProperties.teamSize) {
                val entries = match.playerMap.entries
                val teamIndex = entries.first().value
                if (entries.all { it.value == teamIndex }) {
                    // match is a bye -> set victorID & progress team
                    match.victorID = entries.first().key
                    for ((uuid, _) in entries) {
                        val player = playerProperties.firstOrNull { it.uuid == uuid } ?: continue
                        player.currentMatchID = match.matchConnections.victorNextMatch
                    }
                }
            }
        }
    }

    private fun getRounds(
        roundProperties: Set<RoundProperties>,
        tournamentProperties: TournamentProperties,
    ): RoundSet {
        val rounds = mutableSetOf<TournamentRound>()
        for (props in roundProperties) {
            if (props.tournamentID != tournamentProperties.uuid) {
                props.tournamentID = tournamentProperties.uuid
            }
            val round = TournamentRound(props).initialize()
            rounds.add(round)
        }
        return rounds
    }

    private fun getMatches(
        matchProperties: Set<MatchProperties>,
        tournamentProperties: TournamentProperties
    ): MatchSet {
        val matches = mutableSetOf<TournamentMatch>()
        for (props in matchProperties) {
            if (props.tournamentID != tournamentProperties.uuid) {
                props.tournamentID = tournamentProperties.uuid
            }
            val match = TournamentMatch(props).initialize()
            matches.add(match)
        }
        return matches
    }

    private fun getPlayers(
        playerProperties: OrderedPlayers,
        tournamentProperties: TournamentProperties,
    ): PlayerSet {
        val players = mutableSetOf<TournamentPlayer>()
        for (props in playerProperties) {
            if (props.tournamentID != tournamentProperties.uuid) {
                props.tournamentID = tournamentProperties.uuid
            }
            val player = TournamentPlayer(props).initialize()
            players.add(player)
        }
        return players
    }

}
