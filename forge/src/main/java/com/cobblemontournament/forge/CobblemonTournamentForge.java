package com.cobblemontournament.forge;

import dev.architectury.platform.forge.EventBuses;
import com.cobblemontournament.common.CobblemonTournament;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("unused")
@Mod(CobblemonTournament.MOD_ID)
public class CobblemonTournamentForge
{
    public CobblemonTournamentForge() {
        //EventBuses.registerModEventBus(CobblemonTournament.MOD_ID,FMLJavaModLoadingContext.get().getModEventBus());
        CobblemonTournament.init();
    }
}