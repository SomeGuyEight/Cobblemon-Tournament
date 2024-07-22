package com.cobblemontournament.common.generator

import com.cobblemontournament.common.generator.indexedseed.IndexedSeedGenerator
import com.cobblemontournament.common.match.properties.MatchProperties
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.round.properties.RoundProperties
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.api.tournament.TournamentData
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.collections.SortType
import java.util.UUID

@Suppress( names = ["UNUSED_PARAMETER"] )
object TournamentGenerator {

    fun toTournament(name: String, builder: TournamentBuilder): TournamentData? {
        if (builder.getPlayersSize() < 2) {
            // TODO log not enough players for tournament
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
            tournamentProperties = tournamentProperties,
        )

        handleFirstRound(
            rounds = roundProperties,
            matches = matchProperties,
            sortedPlayers = sortedPlayers,
            builder = builder,
            tournamentProperties = tournamentProperties,
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

    private fun handleDoubleElimination(
        builder: TournamentBuilder,
        properties: TournamentProperties,
    ): TournamentData {
        (TODO("Not implemented yet"))
    }

    private fun handleRoundRobin(
        builder: TournamentBuilder,
        properties: TournamentProperties
    ): TournamentData {
        (TODO("Not implemented yet"))
    }
    
    private fun handleVGC(
        builder: TournamentBuilder,
        properties: TournamentProperties
    ): TournamentData {
        (TODO("Not implemented yet"))
    }
    
    private fun getRoundCount(
        builder: TournamentBuilder,
        properties: TournamentProperties
    ): Int {
        return when (properties.tournamentType) {
            TournamentType.SINGLE_ELIMINATION -> getRoundCountSingleElimination(builder)
            TournamentType.DOUBLE_ELIMINATION -> getRoundCountDoubleElimination(builder)
            TournamentType.ROUND_ROBIN -> getRoundCountRoundRobin(builder)
            TournamentType.VGC -> getRoundCountVGC(builder)
        }
    }

    private fun getRoundCountSingleElimination(builder: TournamentBuilder): Int {
        val playerCount = builder.getPlayersSize()
        var bracketSlots = IndexedSeedGenerator.ceilToPowerOfTwo(playerCount)
        // -1 b/c first shift divides the total players in half & gets the actual first round match count
        //  -> this is to give the actual quantity of matches in the first round (round index 0)
        var rounds = -1
        while (bracketSlots > 0) {
            bracketSlots = bracketSlots shr 1
            rounds++
        }
        return rounds
    }

    private fun getRoundCountDoubleElimination(builder: TournamentBuilder): Int {
        // TODO: same as first round then -> - add lower bracket
        return 0
    }

    private fun getRoundCountRoundRobin(builder: TournamentBuilder): Int {
        // TODO
        return 0
    }

    private fun getRoundCountVGC(builder: TournamentBuilder): Int {
        // TODO
        return 0
    }


    private fun sortPlayers(
        seededPlayers: List<PlayerProperties>,
        unseededPlayers: List<PlayerProperties>,
        tournamentProperties: TournamentProperties
    ): List<PlayerProperties> {
        val orderedPlayers = ArrayList(seededPlayers.toList())
        orderedPlayers.sortWith(Comparator.comparing(PlayerProperties::seed))

        val shuffledPlayers = ArrayDeque(unseededPlayers.shuffled())
        orderedPlayers.addAll(shuffledPlayers)

        val sameSeedQueue = ArrayDeque<PlayerProperties>()
        val size = orderedPlayers.size

        for (i in 0 until size) {
            var nextPlayer: PlayerProperties?
            if (sameSeedQueue.isNotEmpty()) {
                nextPlayer = sameSeedQueue.removeFirst()
                // 'i + 1 != size' to catch out of bounds error on last iteration
            } else if ((i + 1) != size && (orderedPlayers[i]!!.seed == orderedPlayers[(i + 1)]!!.seed)) {
                // multiple players with same seed -> create collection to pull players from at random
                var lastIndex = i
                val tempSameSeeds = mutableSetOf<PlayerProperties>()
                tempSameSeeds.add(orderedPlayers[lastIndex])

                // TODO clean up predicate? make more readable... WIP
                val predicate: (Int, List<PlayerProperties>) -> Boolean = { index, list ->
                    ((index + 1) != list.size) && (list[index].seed == list[(index + 1)].seed)
                }
                while (predicate(lastIndex, orderedPlayers)) {
                    tempSameSeeds.add(orderedPlayers[++lastIndex])
                }
                sameSeedQueue.addAll(tempSameSeeds.shuffled())
                nextPlayer = sameSeedQueue.removeFirst()
            } else {
                // just add the next player in order with new instance containing synced seed
                nextPlayer = orderedPlayers[i]
                if (nextPlayer == null) {
                    // TODO: LOG if nextPlayer was null
                    // should not happen... but just in case continue past
                    continue
                }
            }

            orderedPlayers.removeAt(i)
            orderedPlayers.add(
                i, // index in list
                PlayerProperties(
                    name            = nextPlayer.name,
                    actorType       = nextPlayer.actorType,
                    playerID        = nextPlayer.playerID,
                    tournamentID    = tournamentProperties.tournamentID,
                    seed            = i + 1,
                    originalSeed    = nextPlayer.seed,
                    pokemonTeamID   = nextPlayer.pokemonTeamID,
                )
            )
        }

        return orderedPlayers
    }

    private fun handleFirstRound(
        rounds: MutableSet<RoundProperties>,
        matches: MutableSet<MatchProperties>,
        sortedPlayers: List<PlayerProperties>,
        builder: TournamentBuilder,
        tournamentProperties: TournamentProperties,
    ) {
        val firstRoundID = UUID.randomUUID()
        val firstRoundMatches = getFirstRoundMatches(firstRoundID, sortedPlayers, builder, tournamentProperties)
        val firstRound = RoundProperties(
            roundID = firstRoundID,
            tournamentID = tournamentProperties.tournamentID,
            roundIndex = 0,
            roundType = RoundType.PRIMARY,
            indexedMatchMap = getMatchMap(firstRoundMatches),
        )
        rounds.add(firstRound)
        matches.addAll(firstRoundMatches)
    }


    private fun getMatchMap(matches: Set<MatchProperties>): MutableMap<Int,UUID> {
        val matchMap = mutableMapOf<Int,UUID>()
        matches.forEach { matchMap[it.roundMatchIndex] = it.matchID }
        return matchMap
    }

    private fun getFirstRoundMatches(
        roundID: UUID,
        orderedSeededPlayers: List<PlayerProperties>,
        builder: TournamentBuilder,
        tournamentProperties: TournamentProperties,
    ): Set<MatchProperties> {
        val indexedSeeds = IndexedSeedGenerator.getIndexedSeedArray(
            seedCount = builder.getPlayersSize(),
            currentSortType = SortType.INDEX_ASCENDING,
        )
        if (indexedSeeds.sortType != SortType.INDEX_ASCENDING) {
            indexedSeeds.sortBySeedAscending()
        }
        val size = indexedSeeds.size()
        val matchCount = size shr 1 // divide by 2
        val matches = mutableSetOf<MatchProperties>()
        var seedIndex = 0
        val getProperties: (Int) -> PlayerProperties? = { seed ->
            orderedSeededPlayers.firstOrNull { p -> p.seed == seed }
        }

        val seedEntries = indexedSeeds.deepCopy()
        for (i in 0 until matchCount) {
            val seed1 = seedEntries[seedIndex++].seed
            val seed2 = seedEntries[seedIndex++].seed
            val player1 = getProperties(seed1)
            val player2 = getProperties(seed2)
            val playerMap = mutableMapOf<UUID,Int>()
            val matchID = UUID.randomUUID()
            if (player1 != null) {
                player1.currentMatchID = matchID
                playerMap[player1.playerID] = 1
            }
            if (player2 != null) {
                player2.currentMatchID = matchID
                playerMap[player2.playerID] = 2
            }
            matches.add(MatchProperties(
                matchID = matchID,
                tournamentID = tournamentProperties.tournamentID,
                roundID = roundID,
                roundIndex = 0,
                tournamentMatchIndex = i,
                roundMatchIndex = i,
                playerMap = playerMap,
            ))
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
        var tournamentMatchIndex = firstRoundMatchCount // b/c first round is already taken care of
        for (roundIndex in 1 until roundCount) {
            matchesThisRound = matchesThisRound shr 1 // shr 1 ≡ (x: Int) >> 1
            val roundID = UUID.randomUUID()
            val roundMatches = mutableSetOf<MatchProperties>()
            for (i in 0 until matchesThisRound) {
                roundMatches.add(MatchProperties(
                    matchID = UUID.randomUUID(),
                    tournamentID = tournamentProperties.tournamentID,
                    roundID = roundID,
                    roundIndex = roundIndex,
                    tournamentMatchIndex = (tournamentMatchIndex++),
                    roundMatchIndex = i,
                ))
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
        //tournamentType: TournamentType, // TODO when other tournament types implemented
    ) {
        for (match in matchProperties) {
            val currentRound = roundProperties.firstOrNull { round ->
                round.roundID == match.roundID
            } ?: continue // TODO Log?

            val nextRound = roundProperties.firstOrNull { round ->
                round.roundIndex == (currentRound.roundIndex + 1)
            }
            val victorIndex = if (nextRound != null) {
                TournamentUtil.victorNextMatchIndex(
                    roundMatchIndex = match.roundMatchIndex,
                    roundIndex = currentRound.roundIndex,
                    roundCount = roundProperties.size,
                )
            } else {
                null
            }

            if (nextRound != null) {
                match.connections.victorNextMatch = nextRound.indexedMatchMap[victorIndex]
            }

            // TODO when other tournament types implemented
            //val defeatedIndex = TournamentRound.defeatedNextMatchIndex( match.roundMatchIndex, tournamentType)

            val previousRound = roundProperties.firstOrNull { round ->
                round.roundIndex == (currentRound.roundIndex - 1)
            }
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
        playerProperties: List<PlayerProperties>,
    ) {
        for (match in matchProperties) {
            if ((match.roundIndex != 0) || match.playerMap.isEmpty()) {
                continue
            }
            if (match.playerMap.size == tournamentProperties.teamSize) {
                val entries = match.playerMap.entries
                val teamIndex = entries.first().value
                if (entries.all { it.value == teamIndex }) {
                    // match is a bye -> set victorID & progress team
                    match.victorID = entries.first().key
                    for ((uuid, _) in entries) {
                        val player = playerProperties.firstOrNull { it.playerID == uuid }
                            ?: continue
                        player.currentMatchID = match.connections.victorNextMatch
                    }
                }
            }
        }
    }

    private fun getRounds(
        roundProperties: Set<RoundProperties>,
        tournamentProperties: TournamentProperties,
    ): Set<TournamentRound> {
        val rounds = HashSet<TournamentRound>(roundProperties.size)
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
    ): Set<TournamentMatch> {
        val matches = HashSet<TournamentMatch>(matchProperties.size)
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
        playerProperties: List<PlayerProperties>,
        tournamentProperties: TournamentProperties,
    ): Set<TournamentPlayer> {
        val players = HashSet<TournamentPlayer>(playerProperties.size)
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
