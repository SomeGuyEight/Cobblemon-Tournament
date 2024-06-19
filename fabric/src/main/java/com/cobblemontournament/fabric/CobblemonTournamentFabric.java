package com.cobblemontournament.fabric;

import com.cobblemontournament.common.CobblemonTournament;
import net.fabricmc.api.ModInitializer;

public class CobblemonTournamentFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobblemonTournament.init();
    }
}