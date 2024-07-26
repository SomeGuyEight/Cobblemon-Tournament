package com.cobblemontournament.fabric

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.config.TournamentConfig
import com.someguy.mod.PlatformModImplementation
import net.fabricmc.api.ModInitializer

class CobblemonTournamentFabric :
    PlatformModImplementation(common = CobblemonTournament),
    ModInitializer {

    override fun onInitialize() = initialize()

    // no platform specific initialization needed at this time
    override fun initialize() = initializeCommon()

    override fun initializeConfig() = TournamentConfig.registerConfigs()

    // no platform specific events to register at this time
    override fun registerEvents() { }

}
