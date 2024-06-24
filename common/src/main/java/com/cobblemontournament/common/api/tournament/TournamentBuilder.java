package com.cobblemontournament.common.api.tournament;

import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemontournament.common.api.player.PlayerProperties;
import com.cobblemontournament.common.api.storage.TournamentStore;
import com.cobblemontournament.common.match.MatchStatus;
import com.cobblemontournament.common.match.TournamentMatch;
import com.cobblemontournament.common.player.TournamentPlayer;
import com.cobblemontournament.common.round.RoundType;
import com.cobblemontournament.common.round.TournamentRound;
import com.cobblemontournament.common.util.IndexedSeedArray;
import com.cobblemontournament.common.util.CollectionSortType;
import com.cobblemontournament.common.util.SeedUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public final class TournamentBuilder
{
    public TournamentBuilder(TournamentPropertiesBuilder builder)
    {
        propertiesBuilder = builder != null ? builder : new TournamentPropertiesBuilder();
    }

    @NotNull public final TournamentPropertiesBuilder propertiesBuilder;
    @NotNull private final ArrayList<PlayerProperties> playerData = new ArrayList<>();
    @NotNull private final ArrayList<PlayerProperties> unseededPlayerData = new ArrayList<>();

    // player registration & management
    public int getPlayerCount()
    {
        return playerData.size() + unseededPlayerData.size();
    }
    public boolean addPlayer(UUID playerID,ActorType actorType,Integer seed)
    {
        Predicate<? super PlayerProperties> predicate = sp -> sp.id() == playerID;
        if (containsPlayerWith(playerData,predicate) || containsPlayerWith(unseededPlayerData,predicate)) {
            return false;
        }
        return playerData.add(new PlayerProperties(playerID,actorType,seed));
    }
    public boolean updateSeededPlayer(UUID playerID,ActorType actorType,Integer seed)
    {
        int notNullSeed = (seed != null && seed > 0) ? seed : -1;
        Predicate<? super PlayerProperties> removePredicate = sp -> sp.id() == playerID && sp.seed() != notNullSeed;
        removePlayerIf(playerData,removePredicate);
        removePlayerIf(unseededPlayerData,removePredicate);
        return addPlayer(playerID,actorType,seed);
    }
    public boolean removePlayer(@NotNull UUID playerID)
    {
        boolean removed = removePlayerIf(playerData,sp -> sp.id() == playerID);
        return removePlayerIf(unseededPlayerData,sp -> sp.id() == playerID) || removed;
    }
    private boolean containsPlayerWith(ArrayList<PlayerProperties> collection,Predicate<? super PlayerProperties> predicate)
    {
        return collection.stream().anyMatch(predicate);
    }
    private boolean removePlayerIf(ArrayList<PlayerProperties> collection,Predicate<? super PlayerProperties> predicate)
    {
        return collection.removeIf(predicate);
    }

    public int getRoundCount()
    {
        return switch (propertiesBuilder.getTournamentType()) {
            case SingleElimination -> getRoundCountSingleElimination();
            case DoubleElimination -> getRoundCountDoubleElimination();
            case RoundRobin -> getRoundCountRoundRobin();
            case VGC -> getRoundCountVGC();
        };
    }
    private int getRoundCountSingleElimination()
    {
        int playerCount = getPlayerCount();
        int bracketSlots = SeedUtil.ceilToPowerOfTwo(playerCount);
        int rounds = 0;
        while (bracketSlots > 0) {
            bracketSlots = bracketSlots >> 1;
            rounds++;
        }
        return rounds;
    }
    private int getRoundCountDoubleElimination()
    {
        double playersRemaining = getPlayerCount();
        int rounds = 0;
        while (playersRemaining > 1) {
            playersRemaining = Math.ceil(playersRemaining * 0.5f);
        }

        // TODO lower bracket

        return rounds;
    }
    private int getRoundCountRoundRobin()
    {
        var playersRemaining = getPlayerCount();
        int rounds = 0;

        // TODO

        return rounds;
    }
    private int getRoundCountVGC()
    {
        double playersRemaining = getPlayerCount();
        int rounds = 0;

        // TODO

        return rounds;
    }

    /** Construct a finalized tournament */
    public TournamentStore toTournament()
    {
        var playerCount = getPlayerCount();
        if (playerCount < 2) {
            // TODO log not enough players for tournament
            return null;
        }

        return switch (propertiesBuilder.getTournamentType()){
            case SingleElimination -> handleSingleElimination(UUID.randomUUID(),playerCount);
            case DoubleElimination -> handleDoubleElimination(UUID.randomUUID(),playerCount);
            case RoundRobin -> handleRoundRobin(UUID.randomUUID(),playerCount);
            case VGC -> handleVGC(UUID.randomUUID(),playerCount);
        };
    }
    
    // TODO finish
    private TournamentStore handleSingleElimination(UUID tournamentID,int playerCount)
    {
        var properties = propertiesBuilder.toTournamentProperties();
        var roundCount = getRoundCount();

        // get total matches in first -> always power of 2 for single & double elimination brackets
        var matchCount = 1 << (roundCount - 1); // remove championship b/c... fml

        var rounds = new ArrayList<TournamentRound>(roundCount);
        var matches = new HashSet<TournamentMatch>(matchCount);
        var sortedPlayers = sortAndSyncSeededPlayers(playerData);
        
        var firstRoundID = UUID.randomUUID();
        var firstRoundMatches = getFirstRoundMatches(tournamentID,firstRoundID,sortedPlayers);
        var firstRoundMatchMap = getMatchMap(firstRoundMatches);
        var firstRound = new TournamentRound(
                tournamentID,
                firstRoundID,
                0,
                RoundType.Primary,
                firstRoundMatchMap
        );
        
        matches.addAll(firstRoundMatches);
        rounds.add(firstRound);
        
        /* TODO in future
              - extract code into methods
              - pass in each data type into the methods
              - send with action that will initialize a new instance & return an ID
         */
        
        int totalMatches = matchCount;
        for(int i = 1; i < roundCount; i++) {
            var newRoundID = UUID.randomUUID();
            // size of indexed seeds should always be power of 2 -> this cuts it in half 'safely'
            matchCount = matchCount >> 1;
            var lastMatchIndex = totalMatches - 1;
            var roundMatches = new ArrayList<TournamentMatch>(matchCount);
            for (var rmi = 0; rmi < matchCount; rmi++) { // rmi == roundMatchIndex
                roundMatches.add(new TournamentMatch(
                        UUID.randomUUID(),
                        tournamentID,
                        newRoundID,
                        lastMatchIndex++,
                        rmi,
                        MatchStatus.Empty,
                        null)
                );
            }
            var nextMatchMap = getMatchMap(roundMatches);
            var nextRound =new TournamentRound(
                    tournamentID,
                    newRoundID,
                    i,
                    RoundType.Primary,
                    nextMatchMap
            );
            rounds.add(nextRound);
            matches.addAll(roundMatches);
            totalMatches += matchCount;
        }
        
        
        var roundIDs = new HashSet<UUID>(roundCount);
        for (TournamentRound round : rounds) {
            roundIDs.add(round.getUuid());
            // TODO !! register rounds with factory & save !!
            //      - set up round store for this tournament
            
        }
        
        var matchIDs = new HashSet<UUID>(totalMatches);
        for (TournamentMatch match : matches) {
            matchIDs.add(match.getUuid());
            // TODO !! register matches with factory & save !!
            //     - set up match store for this tournament
            
        }
        
        var playerIDs = getUuids(tournamentID,sortedPlayers);
        
        return new TournamentStore(tournamentID,properties,roundIDs,matchIDs,playerIDs);
    }
    
    // TODO finish
    private static @NotNull HashSet<UUID> getUuids(UUID tournamentID,ArrayList<PlayerProperties> sortedPlayers)
    {
        var playerIDs = new HashSet<UUID>(sortedPlayers.size());
        for (PlayerProperties player : sortedPlayers){
            playerIDs.add(player.id());
            var finalizedPlayer = new TournamentPlayer(
                    player.id(),tournamentID,
                    player.actorType(),
                    player.seed(),
                    null,
                    null
            );
            
            // TODO !! register player with factory & save !!
            //     - set up player store for this tournament
            
        }
        return playerIDs;
    }
    
    private TournamentStore handleDoubleElimination(UUID tournamentID,int playerCount)
    {
        // TODO log || implement
        return null;
    }
    private TournamentStore handleRoundRobin(UUID tournamentID,int playerCount)
    {
        // TODO log || implement
        return null;
    }
    private TournamentStore handleVGC(UUID tournamentID,int playerCount)
    {
        // TODO log || implement
        return null;
    }

    private ArrayList<PlayerProperties> sortAndSyncSeededPlayers(ArrayList<PlayerProperties> playerData)
    {
        var orderedPlayers = new ArrayList<>(playerData.stream().toList());
        orderedPlayers.sort(Comparator.comparing(PlayerProperties::seed)); // ascending

        Random random = new Random();
        var sameSeededPlayers = new ArrayList<PlayerProperties>();
        int size = orderedPlayers.size();
        for (int i = 0; i < size; i++) {
            PlayerProperties nextPlayer;
            if (!sameSeededPlayers.isEmpty()) {
                var index = random.ints(0, sameSeededPlayers.size())
                        .findFirst()
                        .orElse(0);
                nextPlayer = sameSeededPlayers.remove(index);
            } else if (i + 1 != size && Objects.equals(orderedPlayers.get(i).seed(), orderedPlayers.get(i + 1).seed())) { // 'i + 1 != size' to catch out of bounds error on last iteration
                // multiple players with same seed -> create collection to pull players from at random
                var lastIndex = i;
                sameSeededPlayers.add(orderedPlayers.get(lastIndex));
                while (Objects.equals(orderedPlayers.get(lastIndex).seed(), orderedPlayers.get(lastIndex + 1).seed()))
                {
                    sameSeededPlayers.add(orderedPlayers.get(++lastIndex));
                }
                var index = random.ints(0, sameSeededPlayers.size())
                        .findFirst()
                        .orElse(0);
                nextPlayer = sameSeededPlayers.remove(index);
            } else {
                // just add the next player in order with new instance containing synced seed
                nextPlayer = orderedPlayers.get(i);
            }

            orderedPlayers.remove(i);
            orderedPlayers.add(i,new PlayerProperties(nextPlayer.id(),nextPlayer.actorType(),i + 1));
        }
        return orderedPlayers;
    }

    private ArrayList<TournamentMatch> getFirstRoundMatches(
            UUID tournamentID,
            UUID roundID,
            ArrayList<PlayerProperties> orderedPlayers)
    {
        var indexedSeeds = SeedUtil.getIndexedSeedArray(getPlayerCount(),CollectionSortType.INDEX_ASCENDING);
        
        if (indexedSeeds.sortStatus() != CollectionSortType.INDEX_ASCENDING) {
            indexedSeeds.sortBySeedAscending();
        }
        var size = indexedSeeds.size();
        // size of indexed seeds should always be power of 2 -> cuts in half 'safely'...
        int matchCount = size >> 1;
        var matches = new ArrayList<TournamentMatch>(matchCount);
        int seedIndex = 0;
        
        for (int i = 0; i < matchCount; i++) {
            var seed1 = indexedSeeds.collection.get(seedIndex++).seed();
            var seed2 = indexedSeeds.collection.get(seedIndex++).seed();
            var player1 = orderedPlayers.stream()
                    .filter(p -> p.seed().equals(seed1))
                    .findFirst()
                    .orElse(null);
            var player2 = orderedPlayers.stream()
                    .filter(p -> p.seed().equals(seed2))
                    .findFirst()
                    .orElse(null);
            UUID player1ID = player1 != null ? player1.id() : null;
            UUID player2ID = player2 != null ? player2.id() : null;
            var playerMap = new HashMap<UUID,Integer>() { {
                    put(player1ID,1);
                    put(player2ID,2);
            } };
            matches.add(new TournamentMatch(
                    UUID.randomUUID(),
                    tournamentID,
                    roundID,
                    i,
                    i,
                    MatchStatus.Empty,
                    playerMap)
            );
        }
        
        fillWithUnseededPlayers(matches,indexedSeeds);
        
        return matches;
    }

    private void fillWithUnseededPlayers(
            @NotNull ArrayList<TournamentMatch> matches,
            @NotNull IndexedSeedArray indexedSeeds
    ) {
        indexedSeeds.sortBySeedAscending();
        var unseededCount = unseededPlayerData.size();
        var unseededIndex = 0;
        for (int i = 0; i < indexedSeeds.size();i++) {
            var seedIndex = indexedSeeds.get(i).index();
            var matchIndex = seedIndex/2;
            // remainder used to place player correctly into team 1 or 2
            //  -> b/c remainder always 1 | 0
            var remainder = seedIndex%2;
            var match = matches.get(matchIndex);
            int team = remainder == 0 ? 1 : 2;
            if (match.playerEntrySet().stream().noneMatch(e -> e.getValue() == team)) {
                // has available slot
                var player = unseededPlayerData.get(unseededIndex);
                match.trySetPlayer(player.id(),team);
                unseededIndex++;
            }

            if (unseededIndex == unseededCount) {
                break;
            }
        }
    }
    
    private HashMap<Integer,UUID> getMatchMap(ArrayList<TournamentMatch> matches)
    {
        var matchMap = new HashMap<Integer,UUID>();
        matches.forEach( match -> matchMap.put(match.getRoundMatchIndex(),match.getUuid()));
        return matchMap;
    }

}
