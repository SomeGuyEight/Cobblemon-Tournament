package com.cobblemontournament.fabric;

import com.cobblemontournament.common.tournament.TournamentType;
import com.turtlehoarder.cobblemonchallenge.fabric.CobblemonChallengeFabric;
import com.cobblemontournament.common.CobblemonTournament;
import com.cobblemontournament.common.CommonInterface;
import com.cobblemontournament.common.testing.command.TestTournamentCommand;
import com.cobblemontournament.fabric.config.TournamentConfigFabric;
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CobblemonTournamentFabric extends CommonInterface implements ModInitializer
{
    //public static String MOD_ID = CobblemonTournament.MOD_ID;
    
    @Override
    public void onInitialize()
    {
        TournamentConfigFabric.registerConfigs();
        CobblemonTournament.initialize(this);
        
        // here while challenge is integrated in testing project
        // TODO: remove when able to use as dependency
        var instance = new CobblemonChallengeFabric();
        instance.onInitialize();
    }
    
    @Override
    public void initializeConfig()
    {
        config = new TournamentConfigFabric();
        config.initialize(this);
    }
    
    @Override
    public int getIntConfig(String configName)
    {
        return Integer.parseInt(TournamentConfigFabric.CONFIG.getOrDefault(configName,null));
    }
    
    @Override
    public boolean getBooleanConfig(String configName)
    {
        return Boolean.parseBoolean(TournamentConfigFabric.CONFIG.getOrDefault(configName,null));
    }
    
    @Override
    public TournamentType getTournamentTypeConfig(String configName)
    {
        return Enum.valueOf(
                TournamentType.class,
                TournamentConfigFabric.CONFIG.getOrDefault(configName,null)
        );
    }
    
    @Override
    public ChallengeFormat getChallengeFormatConfig(String configName)
    {
        return Enum.valueOf(
                ChallengeFormat.class,
                TournamentConfigFabric.CONFIG.getOrDefault(configName,null)
        );
    }

    @Override
    public void registerEvents()
    {
        CobblemonTournament.registerEvents();
    }
    
    @Override
    public void registerCommands()
    {
        //CommandRegistrationCallback.EVENT.register((commandDispatcher,commandBuildContext,commandSelection) ->
        //                                                   TournamentCommand.register(commandDispatcher)
        //);
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) ->
                                                           TestTournamentCommand.register(commandDispatcher)
        );
    }
}