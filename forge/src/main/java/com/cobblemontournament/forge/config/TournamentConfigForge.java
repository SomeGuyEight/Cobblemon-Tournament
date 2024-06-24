package com.cobblemontournament.forge.config;

import com.cobblemontournament.common.CobblemonTournament;
import com.cobblemontournament.common.config.TournamentConfig;
import com.cobblemontournament.common.tournament.TournamentType;
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;

@SuppressWarnings("unused")
public final class TournamentConfigForge extends TournamentConfig
{
    
    public HashMap<String, ForgeConfigSpec.ConfigValue<?>> configMap = new HashMap<>();
    
    public TournamentConfigForge(ForgeConfigSpec.Builder builder)
    {
        CobblemonTournament.LOGGER.info("Loading Tournament Configs");
        builder.push("cobblemontournament");
        
        var DEFAULT_TOURNAMENT_TYPE     = builder.comment("(TODO)")
                                                 .define("defaultTournamentType",TournamentType.SingleElimination);
        var DEFAULT_GROUP_SIZE          = builder.comment("(TODO)")
                                                 .define("defaultGroupSize",4);
        var DEFAULT_MAX_PLAYER_COUNT    = builder.comment("(TODO)")
                                                 .define("defaultMaxParticipants",32);
        var DEFAULT_CHALLENGE_FORMAT    = builder.comment("(TODO)")
                                                 .define("defaultChallengeFormat",ChallengeFormat.STANDARD_6V6);
        var DEFAULT_CHALLENGE_MIN_LEVEL = builder.comment("(TODO)")
                                                 .define("defaultMinLevel",50);
        var DEFAULT_CHALLENGE_MAX_LEVEL = builder.comment("(TODO)")
                                                 .define("defaultMaxLevel",50);
        var DEFAULT_SHOW_PREVIEW        = builder.comment("(TODO)")
                                                 .define("defaultShowPreview",true);
        
        configMap.put(TournamentConfig.DEFAULT_TOURNAMENT_TYPE_CONFIG_NAME  ,DEFAULT_TOURNAMENT_TYPE    );
        configMap.put(TournamentConfig.DEFAULT_GROUP_SIZE_CONFIG_NAME       ,DEFAULT_GROUP_SIZE         );
        configMap.put(TournamentConfig.DEFAULT_MAX_PARTICIPANTS_CONFIG_NAME ,DEFAULT_MAX_PLAYER_COUNT   );
        configMap.put(TournamentConfig.DEFAULT_CHALLENGE_FORMAT_CONFIG_NAME ,DEFAULT_CHALLENGE_FORMAT   );
        configMap.put(TournamentConfig.DEFAULT_MIN_LEVEL_CONFIG_NAME        ,DEFAULT_CHALLENGE_MIN_LEVEL);
        configMap.put(TournamentConfig.DEFAULT_MAX_LEVEL_CONFIG_NAME        ,DEFAULT_CHALLENGE_MAX_LEVEL);
        configMap.put(TournamentConfig.DEFAULT_SHOW_PREVIEW_CONFIG_NAME     ,DEFAULT_SHOW_PREVIEW       );
        
    }
    
    public int getIntConfig(String name)
    {
        return Integer.parseInt(configMap.get(name).get().toString());
    }
    
    public boolean getBooleanConfig(String name)
    {
        return Boolean.parseBoolean(configMap.get(name).get().toString());
    }
    
    public TournamentType getTournamentTypeConfig(String name)
    {
        return (TournamentType)configMap.get(name).get();
    }
    
    public ChallengeFormat getChallengeFormatConfig(String name)
    {
        return (ChallengeFormat)configMap.get(name).get();
    }
    
}