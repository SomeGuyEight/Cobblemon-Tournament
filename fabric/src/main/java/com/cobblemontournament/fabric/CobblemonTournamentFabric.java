package com.cobblemontournament.fabric;

import com.cobblemontournament.common.TournamentModImplementation;
import com.cobblemontournament.common.config.ConfigProvider;
import com.cobblemontournament.common.CobblemonTournament;
import com.cobblemontournament.common.commands.TournamentCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

public class CobblemonTournamentFabric implements ModInitializer, TournamentModImplementation
{
    @Override
    public void onInitialize() {
        CobblemonTournament.initialize (this);
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
