package com.cobblemontournament.fabric;

import com.cobblemontournament.common.CTModImplementation;
import com.cobblemontournament.common.config.ConfigProvider;
import com.turtlehoarder.cobblemonchallenge.fabric.CobblemonChallengeFabric;
import com.cobblemontournament.common.CobblemonTournament;
import com.cobblemontournament.common.TournamentCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

public class CobblemonTournamentFabric implements ModInitializer, CTModImplementation
{
    @Override
    public void onInitialize()
    {
        CobblemonTournament.initialize (this);
        
        // here while challenge is integrated in testing project
        // TODO: remove when able to use as dependency
        new CobblemonChallengeFabric().onInitialize();
    }

    @Override
    public void initializeConfig()
    {
        ConfigProvider.registerConfigs(FabricLoader.getInstance().getConfigDir());
    }

    @Override
    public void registerEvents()
    {
        CobblemonTournament.registerEvents();
    }

    @Override
    public void registerCommands()
    {
        CommandRegistrationCallback.EVENT.register(TournamentCommands::register);
    }
    
}
