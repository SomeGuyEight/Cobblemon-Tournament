package com.cobblemontournament.forge

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.forge.config.TournamentConfigForge
import com.someguy.api.PlatformModImplementation
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig

@Suppress("unused")
@Mod(CobblemonTournament.COMPANION.MOD_ID)
object CobblemonTournamentForge : PlatformModImplementation( common = CobblemonTournament )
{
    private val commonSpec: ForgeConfigSpec

    init
    {
        val ( _ , commonSpec ) = ForgeConfigSpec.Builder().configure {
            builder -> TournamentConfigForge.initialize( builder )
        }
        this.commonSpec = commonSpec
        initialize()
    }

    // no platform specific initialization needed at this time
    override fun initialize() = initializeCommon()

    override fun initializeConfig() {
        ModLoadingContext.get().registerConfig( ModConfig.Type.COMMON, commonSpec )
    }

    // no platform specific events to register at this time
    override fun registerEvents() { }
}
