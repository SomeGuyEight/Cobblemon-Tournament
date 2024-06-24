package com.cobblemontournament.fabric.config;

import com.cobblemontournament.common.CobblemonTournament;
import com.cobblemontournament.common.config.TournamentConfig;
import com.cobblemontournament.common.tournament.TournamentType;
import com.mojang.datafixers.util.Pair;
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat;

public class TournamentConfigFabric extends TournamentConfig
{
    public static SimpleConfig CONFIG;
    private static TournamentConfigProvider configs;
    
    public static void registerConfigs()
    {
        CobblemonTournament.LOGGER.info("Loading Tournament Configs");
        configs = new TournamentConfigProvider();
        createConfigs();
        CONFIG = SimpleConfig.of(CobblemonTournament.MOD_ID + "-config").provider(configs).request();
        assignConfigs();
    }
    
    private static void createConfigs()
    {
        configs.addKeyValuePair(new Pair<>("defaultTournamentType",TournamentType.SingleElimination.toString()));
        configs.addKeyValuePair(new Pair<>("defaultGroupSize",4));
        configs.addKeyValuePair(new Pair<>("defaultMaxParticipants",32));
        configs.addKeyValuePair(new Pair<>("defaultChallengeFormat",ChallengeFormat.STANDARD_6V6.toString()));
        configs.addKeyValuePair(new Pair<>("defaultMinLevel",50));
        configs.addKeyValuePair(new Pair<>("defaultMaxLevel",50));
        configs.addKeyValuePair(new Pair<>("defaultShowPreview",true));
    }
    
    private static void assignConfigs()
    {
        var type = CONFIG.getOrDefault("defaultTournamentType",TournamentType.SingleElimination.toString());
        DEFAULT_TOURNAMENT_TYPE = Enum.valueOf(TournamentType.class,type);
        DEFAULT_GROUP_SIZE = CONFIG.getOrDefault("defaultGroupSize",4);
        DEFAULT_MAX_PARTICIPANTS = CONFIG.getOrDefault("defaultMaxParticipants",32);
        var format = CONFIG.getOrDefault("defaultChallengeFormat",ChallengeFormat.STANDARD_6V6.toString());
        DEFAULT_CHALLENGE_FORMAT = Enum.valueOf(ChallengeFormat.class,format);
        DEFAULT_MIN_LEVEL = CONFIG.getOrDefault("defaultMinLevel",50);
        DEFAULT_MAX_LEVEL = CONFIG.getOrDefault("defaultMaxLevel",50);
        DEFAULT_SHOW_PREVIEW = CONFIG.getOrDefault("defaultShowPreview",true);
    }
    
}
