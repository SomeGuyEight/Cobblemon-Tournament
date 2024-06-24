package com.cobblemontournament.common.testing;

import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemontournament.common.api.tournament.TournamentBuilder;
import com.cobblemontournament.common.api.tournament.TournamentPropertiesBuilder;
import com.cobblemontournament.common.match.TournamentMatch;
import com.cobblemontournament.common.round.TournamentRound;
import com.cobblemontournament.common.api.storage.TournamentStore;
import com.cobblemontournament.common.tournament.TournamentType;
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat;
import org.slf4j.helpers.Util; // Util.report("");

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TournamentBuilderTest
{
    public static void buildTournamentDebug(int maxPlayers,boolean doPrint)
    {
        var propertiesBuilder = new TournamentPropertiesBuilder(
                TournamentType.SingleElimination,
                null,
                maxPlayers,
                ChallengeFormat.STANDARD_6V6,
                null,
                null,
                null
        );
        var tournamentBuilder = new TournamentBuilder(propertiesBuilder);

        for (int i = 0; i < maxPlayers; i++) {
            tournamentBuilder.addPlayer(UUID.randomUUID(),ActorType.PLAYER,i);
        }

        var tournament = tournamentBuilder.toTournament();

        if (doPrint && tournament != null){
            printTournamentDebug(tournament);
        }
    }

    public static void printTournamentDebug(TournamentStore tournament)
    {
        var props = tournament.getProperties();
        // print Properties
        Util.report("Tournament Debug - ID: " + tournament.getUuid());
        Util.report("   Properties:");
        Util.report("     - Tournament Type: " + props.getTournamentType());
        Util.report("     - Group Size: " + props.getGroupSize());
        Util.report("     - Max Players: " + props.getMaxPlayerCount());
        Util.report("     - Challenge Format: " + props.getChallengeFormat());
        Util.report("     - Min Level: " + props.getMinLevel());
        Util.report("     - Max Level: " + props.getMaxLevel());
        Util.report("     - Show Preview: " + props.getShowPreview());

        
        // TODO revamp after new store system us implemented
//        int roundCount = tournament.roundsSize();
//        int matchCount = 0;
//        for (int i = 0; i < roundCount; i++) {
//            matchCount += tournament.rounds.get(i).matchMap.size();
//        }
//
//        Util.report("   Details:");
//        Util.report("     - Rounds: " + roundCount);
//        Util.report("     - Matches: " + matchCount);
//
//        // print round details
//        for (int i = 0; i < roundCount;i++) {
//           printRoundDetails(tournament.rounds.get(i),true);
//        }
    }

    public static void printRoundDetails(TournamentRound round,boolean includeMatches)
    {
        Util.report("Round Details - ID:" + round.getUuid());
        Util.report("- Round Type: " + round.getRoundType());
        Util.report("- Round Index: " + round.getRoundIndex());
        Util.report("- Matches: " + round.getMatchMapSize());
        if (!includeMatches) {
            return;
        }
        
        /* TODO get matches to print
        for (TournamentMatch match: matches) {
            printMatchDetails(match);
        }
         */
    }
    public static void printMatchDetails(TournamentMatch match)
    {
        var players = match.playerEntrySet();
        var player1 = players.stream().filter(e -> e.getValue() == 1).findFirst();
        UUID player1ID = player1.map(Map.Entry::getKey).orElse(null);
        var player2 = players.stream().filter(e -> e.getValue() == 2).findFirst();
        UUID player2ID = player2.map(Map.Entry::getKey).orElse(null);
        Util.report("Match Details - ID:" + match.getUuid());
        Util.report("- Tournament Match Index: " + match.getTournamentMatchIndex());
        Util.report("- Round Match Index: " + match.getRoundMatchIndex());
        Util.report("- Player 1: " + player1ID);
        Util.report("- Player 2: " + player2ID);
        Util.report("- Victor: " + match.getVictorID());
        Util.report("- Status: " + match.getStatus());
    }
}
