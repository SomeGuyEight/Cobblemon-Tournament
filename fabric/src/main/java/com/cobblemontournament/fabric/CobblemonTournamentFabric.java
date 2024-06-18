package com.cobblemontournament.fabric;

import com.cobblemontournament.common.CobblemonTournament;
import net.fabricmc.api.ModInitializer;

@SuppressWarnings("unused")
public class CobblemonTournamentFabric implements ModInitializer
{
    @Override
    public void onInitialize() {
        CobblemonTournament.init();
    }
}