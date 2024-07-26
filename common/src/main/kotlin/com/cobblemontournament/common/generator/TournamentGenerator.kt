package com.cobblemontournament.common.generator

import com.cobblemontournament.common.api.TournamentData
import com.cobblemontournament.common.generator.indexedseed.IndexedSeedGenerator
import com.cobblemontournament.common.generator.indexedseed.SortType
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
import com.cobblemontournament.common.util.*
import java.util.UUID

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

        rounds.forEach { tournamentProperties.rounds[it.uuid] = it }
        matches.forEach { tournamentProperties.matches[it.uuid] = it }
        players.forEach { tournamentProperties.players[it.uuid] = it }

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
        val playerCount = builder.getPlayersSize()
        var bracketSlots = ceilToPowerOfTwo(playerCount)
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
    ): OrderedPlayerProperties {
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
                i, // index in list
                PlayerProperties(
                    name            = nextPlayer.name,
                    actorType       = nextPlayer.actorType,
                    playerID        = nextPlayer.playerID,
                    tournamentID    = tournamentProps.tournamentID,
                    seed            = i + 1,
                    originalSeed    = nextPlayer.seed,
                    pokemonTeamID   = nextPlayer.pokemonTeamID,
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
        orderedPlayerProps: OrderedPlayerProperties,
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
            roundID = firstRoundID,
            tournamentID = tournamentProps.tournamentID,
            roundIndex = 0,
            roundType = RoundType.PRIMARY,
            indexedMatchMap = getMatchMap(firstRoundMatches),
        )
        rounds.add(firstRound)
        matches.addAll(firstRoundMatches)
    }


    private fun getMatchMap(matches: MutableSet<MatchProperties>): MutableMap<Int,UUID> {
        val matchMap = mutableMapOf<Int,UUID>()
        matches.forEach { matchMap[it.roundMatchIndex] = it.matchID }
        return matchMap
    }

    private fun getFirstRoundMatches(
        roundID: RoundID,
        orderedPlayerProperties: OrderedPlayerProperties,
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
            val matchID: MatchID = UUID.randomUUID()
            player1?.let {
                player1.currentMatchID = matchID
                playerMap[player1.playerID] = 1
            }
            player2?.let {
                player2.currentMatchID = matchID
                playerMap[player2.playerID] = 2
            }
            matches.add(
                MatchProperties(
                    matchID = matchID,
                    tournamentID = tournamentProperties.tournamentID,
                    roundID = roundID,
                    roundIndex = 0,
                    tournamentMatchIndex = i,
                    roundMatchIndex = i,
                    playerMap = playerMap,
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
                        matchID = UUID.randomUUID(),
                        tournamentID = tournamentProperties.tournamentID,
                        roundID = roundID,
                        roundIndex = roundIndex,
                        tournamentMatchIndex = (tournamentMatchIndex++),
                        roundMatchIndex = i,
                    )
                )
            }

            val roundMatchMap = getMatchMap(roundMatches)
            val newRound = RoundProperties(
                roundID = roundID,
                tournamentID = tournamentProperties.tournamentID,
                roundIndex = roundIndex,
                roundType = RoundType.PRIMARY,
                indexedMatchMap = roundMatchMap,
            )
            rounds.add(newRound)
            matches.addAll(roundMatches)
        }
    }

    private fun setMatchConnections(
        matchProperties: Set<MatchProperties>,
        roundProperties: Set<RoundProperties>,
        // TODO when other tournament types implemented
        //tournamentType: TournamentType,
    ) {
        for (match in matchProperties) {
            val currentRound = roundProperties.firstOrNull { round ->
                round.roundID == match.roundID
            } ?: continue

            val nextIndex = currentRound.roundIndex + 1
            val nextRound = roundProperties.firstOrNull { it.roundIndex == nextIndex }

            if (nextRound != null) {
                TournamentUtil.victorNextMatchIndex(
                    roundMatchIndex = match.roundMatchIndex,
                    roundIndex = currentRound.roundIndex,
                    roundCount = roundProperties.size,
                )?.let { nextMatchIndex ->
                    match.connections.victorNextMatch = nextRound.indexedMatchMap[nextMatchIndex]
                }
            }

            // TODO when other tournament types implemented
            //val defeatedIndex = TournamentRound.defeatedNextMatchIndex(
            //    match.roundMatchIndex,
            //    tournamentType
            //)

            val previousIndex = currentRound.roundIndex - 1
            val previousRound = roundProperties.firstOrNull { it.roundIndex == previousIndex }
            if (previousRound != null) {
                val (evenIndex, oddIndex) = TournamentUtil.previousMatchIndices(
                    roundMatchIndex = match.roundMatchIndex,
                    roundIndex = currentRound.roundIndex,
                )
                val evenMatchID = previousRound.indexedMatchMap[evenIndex]
                val oddMatchID = previousRound.indexedMatchMap[oddIndex]
                if (evenMatchID != null && evenIndex != null) {
                    match.connections.addPrevious(evenIndex, evenMatchID)
                }
                if (oddMatchID != null && oddIndex != null) {
                    match.connections.addPrevious(oddIndex, oddMatchID)
                }
            }
        }
    }

    private fun handleFirstRoundByes(
        tournamentProperties: TournamentProperties,
        matchProperties: Set<MatchProperties>,
        playerProperties: OrderedPlayerProperties,
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
                        val player = playerProperties.firstOrNull { playerProps ->
                            playerProps.playerID == uuid
                        } ?: continue
                        player.currentMatchID = match.connections.victorNextMatch
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
        for (finalProperties in roundProperties) {
            if (finalProperties.tournamentID != tournamentProperties.tournamentID) {
                finalProperties.tournamentID = tournamentProperties.tournamentID
            }
            val round = TournamentRound(finalProperties).initialize()
            rounds.add(round)
        }
        return rounds
    }

    private fun getMatches(
        matchProperties: Set<MatchProperties>,
        tournamentProperties: TournamentProperties
    ): MatchSet {
        val matches = mutableSetOf<TournamentMatch>()
        for (finalProperties in matchProperties) {
            if (finalProperties.tournamentID != tournamentProperties.tournamentID) {
                finalProperties.tournamentID = tournamentProperties.tournamentID
            }
            val match = TournamentMatch(finalProperties).initialize()
            matches.add(match)
        }
        return matches
    }

    private fun getPlayers(
        playerProperties: OrderedPlayerProperties,
        tournamentProperties: TournamentProperties,
    ): PlayerSet {
        val players = mutableSetOf<TournamentPlayer>()
        for (finalProperties in playerProperties) {
            if (finalProperties.tournamentID != tournamentProperties.tournamentID) {
                finalProperties.tournamentID = tournamentProperties.tournamentID
            }
            val player = TournamentPlayer(finalProperties).initialize()
            players.add(player)
        }
        return players
    }

}
