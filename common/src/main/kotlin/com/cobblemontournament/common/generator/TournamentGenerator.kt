package com.cobblemontournament.common.generator

import com.cobblemontournament.common.match.properties.MutableMatchProperties
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.properties.MutablePlayerProperties
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.properties.MutableRoundProperties
import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournament.TournamentData
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.MutableTournamentProperties
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.someguy.collections.SortType
import java.util.UUID

object TournamentGenerator
{
    fun toTournament(
        builder: TournamentBuilder
    ): TournamentData?
    {
        if (builder.totalPlayersSize() < 2) {
            // TODO log not enough players for tournament
            return null
        }

        val properties = builder.tournamentProperties.deepMutableCopy()
        properties.tournamentID = UUID.randomUUID()

        val tournamentData: TournamentData = when (builder.tournamentProperties.tournamentType) {
            TournamentType.SINGLE_ELIMINATION -> handleSingleElimination(builder,properties)
            TournamentType.DOUBLE_ELIMINATION -> handleDoubleElimination(builder,properties)
            TournamentType.ROUND_ROBIN -> handleRoundRobin(builder,properties)
            TournamentType.VGC -> handleVGC(builder,properties)
        }

        return tournamentData
    }

    private fun handleSingleElimination(
        builder: TournamentBuilder,
        properties: MutableTournamentProperties
    ): TournamentData
    {
        val roundCount = getRoundCount( builder, properties)
        val firstRoundMatchCount = 1 shl (roundCount - 1)

        val roundProperties = mutableSetOf<MutableRoundProperties>()
        val matchProperties = mutableSetOf<MutableMatchProperties>()
        val sortedPlayers = sortPlayers( builder.getSeededPlayers(), builder.getUnseededPlayers(), properties)

        handleFirstRound( roundProperties, matchProperties, sortedPlayers, builder, properties)
        initializeRoundsAndMatchesSE( firstRoundMatchCount, roundCount, roundProperties, matchProperties, properties)

        val rounds  = getRounds( roundProperties, properties)
        val matches = getMatches( matchProperties, properties)
        val players = getPlayers( sortedPlayers, properties)

        players.forEach { properties.players[it.uuid] = it.name }

        return TournamentData( Tournament(properties), rounds, matches, players)
    }

    private fun handleDoubleElimination(
        builder: TournamentBuilder,
        properties: MutableTournamentProperties
    ): TournamentData {
        (TODO("Not implemented yet"))
    }
    
    private fun handleRoundRobin(
        builder: TournamentBuilder,
        properties: MutableTournamentProperties
    ): TournamentData {
        (TODO("Not implemented yet"))
    }
    
    private fun handleVGC(
        builder: TournamentBuilder,
        properties: MutableTournamentProperties
    ): TournamentData {
        (TODO("Not implemented yet"))
    }
    
    private fun getRoundCount(
        builder: TournamentBuilder,
        properties: MutableTournamentProperties
    ): Int
    {
        return when (properties.tournamentType) {
            TournamentType.SINGLE_ELIMINATION -> getRoundCountSingleElimination(builder)
            TournamentType.DOUBLE_ELIMINATION -> getRoundCountDoubleElimination(builder)
            TournamentType.ROUND_ROBIN -> getRoundCountRoundRobin(builder)
            TournamentType.VGC -> getRoundCountVGC(builder)
        }
    }

    private fun getRoundCountSingleElimination(
        builder: TournamentBuilder
    ): Int
    {
        val playerCount = builder.totalPlayersSize()
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

    private fun getRoundCountDoubleElimination(
        builder: TournamentBuilder
    ): Int {
        // TODO: same as first round then -> - add lower bracket
        return 0
    }

    private fun getRoundCountRoundRobin(
        builder: TournamentBuilder
    ): Int {
        // TODO
        return 0
    }

    private fun getRoundCountVGC(
        builder: TournamentBuilder
    ): Int {
        // TODO
        return 0
    }

    private fun handleFirstRound(
        rounds: MutableSet<MutableRoundProperties>,
        matches: MutableSet<MutableMatchProperties>,
        sortedPlayers: List<MutablePlayerProperties>,
        builder: TournamentBuilder,
        tournamentProperties: MutableTournamentProperties
    )
    {
        val firstRoundID = UUID.randomUUID()
        val firstRoundMatches = getFirstRoundMatches(firstRoundID, sortedPlayers, builder, tournamentProperties)
        val firstRound = MutableRoundProperties(
            roundID             = firstRoundID,
            tournamentID        = tournamentProperties.tournamentID,
            roundIndex          = 0,
            roundType           = RoundType.PRIMARY,
            indexedMatchMap     = getMatchMap(firstRoundMatches)
        )
        rounds.add(firstRound)
        matches.addAll(firstRoundMatches)
    }


    private fun getMatchMap(
        matches: Set<MutableMatchProperties>
    ): MutableMap<Int, UUID>
    {
        val matchMap = mutableMapOf<Int,UUID>()
        matches.forEach { match: MutableMatchProperties ->
            matchMap[match.roundMatchIndex] = match.matchID
        }
        return matchMap
    }

    private fun sortPlayers(
        seededPlayers: List<MutablePlayerProperties>,
        unseededPlayers: List<MutablePlayerProperties>,
        tournamentProperties: MutableTournamentProperties
    ): List<MutablePlayerProperties>
    {
        val orderedPlayers = ArrayList(seededPlayers.stream().toList())
        orderedPlayers.sortWith(Comparator.comparing(MutablePlayerProperties::seed)) // ascending

        val shuffledPlayers = ArrayDeque(unseededPlayers.shuffled())
        orderedPlayers.addAll(shuffledPlayers)

        val sameSeedQueue = ArrayDeque<MutablePlayerProperties>()
        val size = orderedPlayers.size

        for (i in 0 until size) {
            var nextPlayer: MutablePlayerProperties?
            if (sameSeedQueue.isNotEmpty()) {
                nextPlayer = sameSeedQueue.removeFirst()
            // 'i + 1 != size' to catch out of bounds error on last iteration
            } else if (i + 1 != size && orderedPlayers[i]!!.seed == orderedPlayers[i + 1]!!.seed) { 
                // multiple players with same seed -> create collection to pull players from at random
                var lastIndex = i
                val tempSameSeeds = mutableSetOf<MutablePlayerProperties>()
                tempSameSeeds.add( orderedPlayers[lastIndex])
                while ((lastIndex + 1) != orderedPlayers.size && orderedPlayers[lastIndex].seed == orderedPlayers[lastIndex + 1].seed) {
                    tempSameSeeds.add( orderedPlayers[++lastIndex])
                }
                sameSeedQueue.addAll( tempSameSeeds.shuffled())
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
                MutablePlayerProperties(
                    name            = nextPlayer.name,
                    actorType       = nextPlayer.actorType,
                    playerID        = nextPlayer.playerID,
                    tournamentID    = tournamentProperties.tournamentID,
                    seed            = i + 1,
                    originalSeed    = nextPlayer.seed,
                    pokemonTeamID   = nextPlayer.pokemonTeamID)
            )
        }

        return orderedPlayers
    }

    private fun getFirstRoundMatches(
        roundID: UUID,
        orderedSeededPlayers: List<MutablePlayerProperties>,
        builder: TournamentBuilder,
        tournamentProperties: MutableTournamentProperties
    ): Set<MutableMatchProperties>
    {
        val indexedSeeds = IndexedSeedGenerator.getIndexedSeedArray(builder.totalPlayersSize(), SortType.INDEX_ASCENDING)
        if (indexedSeeds.sortType != SortType.INDEX_ASCENDING) {
            indexedSeeds.sortBySeedAscending()
        }
        val size = indexedSeeds.size()
        val matchCount = size shr 1 // divide by 2
        val matches = mutableSetOf<MutableMatchProperties>()
        var seedIndex = 0
        val getProperties: ((Int) -> MutablePlayerProperties?) = {
            orderedSeededPlayers.firstOrNull { p -> p.seed == it }
        }

        val seedEntries = indexedSeeds.deepCopy()
        for (i in 0 until matchCount) {
            val seed1 = seedEntries[seedIndex++].seed
            val seed2 = seedEntries[seedIndex++].seed
            val player1 = getProperties(seed1)
            val player2 = getProperties(seed2)
            val playerMap = mutableMapOf<UUID, Int>()
            val matchID = UUID.randomUUID()
            if (player1 != null) {
                player1.currentMatchID = matchID
                playerMap[player1.playerID] = 1
            }
            if (player2 != null) {
                player2.currentMatchID = matchID
                playerMap[player2.playerID] = 2
            }
            matches.add( MutableMatchProperties(
                matchID                 = matchID,
                tournamentID            = tournamentProperties.tournamentID,
                roundID                 = roundID,
                tournamentMatchIndex    = i,
                roundMatchIndex         = i,
                playerMap               = playerMap
            ))
        }

        return matches
    }

    private fun initializeRoundsAndMatchesSE (
        firstRoundMatchCount: Int,
        roundCount: Int,
        rounds: MutableSet<MutableRoundProperties>,
        matches: MutableSet<MutableMatchProperties>,
        tournamentProperties: MutableTournamentProperties
    )
    {
        var matchesThisRound = firstRoundMatchCount
        var tournamentMatchIndex = firstRoundMatchCount // b/c first round is already taken care of
        for (roundIndex in 1 until roundCount) {
            matchesThisRound = matchesThisRound shr 1 // shr 1 ≡ (x: Int) >> 1

            val roundID = UUID.randomUUID()
            val roundMatches = mutableSetOf<MutableMatchProperties>()
            for (i in 0 until matchesThisRound) {
                roundMatches.add( MutableMatchProperties(
                    matchID                 = UUID.randomUUID(),
                    tournamentID            = tournamentProperties.tournamentID,
                    roundID                 = roundID,
                    tournamentMatchIndex    = tournamentMatchIndex++,
                    roundMatchIndex         = i
                ))
            }

            val roundMatchMap = getMatchMap(roundMatches)
            val newRound = MutableRoundProperties(
                roundID         = roundID,
                tournamentID    = tournamentProperties.tournamentID,
                roundIndex      = roundIndex,
                roundType       = RoundType.PRIMARY,
                indexedMatchMap = roundMatchMap
            )
            rounds.add(newRound)
            matches.addAll(roundMatches)
        }
    }

    private fun getRounds(
        roundProperties: Set<MutableRoundProperties>,
        tournamentProperties: MutableTournamentProperties
    ): Set<TournamentRound>
    {
        val rounds = HashSet<TournamentRound>(roundProperties.size)
        for (finalProperties in roundProperties) {
            if (finalProperties.tournamentID != tournamentProperties.tournamentID) {
                finalProperties.tournamentID = tournamentProperties.tournamentID
            }
            val round = TournamentRound(finalProperties)
            rounds.add(round)
        }
        tournamentProperties.totalRounds = rounds.size
        return rounds
    }

    private fun getMatches(
        matchProperties: Set<MutableMatchProperties>,
        tournamentProperties: MutableTournamentProperties
    ): Set<TournamentMatch>
    {
        val matches = HashSet<TournamentMatch>(matchProperties.size)
        for (finalProperties in matchProperties) {
            if (finalProperties.tournamentID != tournamentProperties.tournamentID) {
                finalProperties.tournamentID = tournamentProperties.tournamentID
            }
            val match = TournamentMatch(finalProperties)
            matches.add(match)
        }
        tournamentProperties.totalMatches = matches.size
        return matches
    }

    private fun getPlayers(
        playerProperties: List<MutablePlayerProperties>,
        tournamentProperties: MutableTournamentProperties
    ): Set<TournamentPlayer>
    {
        val players = HashSet<TournamentPlayer>(playerProperties.size)
        for (finalProperties in playerProperties) {
            if (finalProperties.tournamentID != tournamentProperties.tournamentID) {
                finalProperties.tournamentID = tournamentProperties.tournamentID
            }
            val player = TournamentPlayer(finalProperties)
            players.add(player)
        }
        tournamentProperties.totalPlayers = playerProperties.size
        return players
    }
    
}
