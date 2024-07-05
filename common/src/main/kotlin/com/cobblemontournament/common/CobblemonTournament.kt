package com.cobblemontournament.common

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.platform.events.PlatformEvents.SERVER_STARTING
import com.cobblemon.mod.common.platform.events.PlatformEvents.SERVER_STOPPED
import com.cobblemonrental.common.CobblemonRental
import com.cobblemontournament.common.TournamentManager.initialize
import com.cobblemontournament.common.TournamentManager.saveAllFactories
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.helpers.Util

object CobblemonTournament
{
    const val MOD_ID: String = "cobblemontournament"
    val LOGGER: Logger = LoggerFactory.getLogger("cobblemon-tournament")
    var implementation: CTModImplementation? = null

    @JvmStatic
    fun initialize(
        implementation: CTModImplementation
    )
    {
        CobblemonTournament.implementation = implementation
        implementation.initializeConfig()
        implementation.registerCommands()
        implementation.registerEvents()
    }

    @JvmStatic
    fun registerEvents()
    {
        SERVER_STARTING.subscribe(Priority.HIGHEST) { event ->
            val server: MinecraftServer = event.server
            CobblemonRental.instance.initialize(server)
            initialize(server)
        }

        SERVER_STOPPED.subscribe(Priority.HIGHEST) {
            Util.report("Saving Tournament factories...")
            saveAllFactories()
            Util.report("Saving Tournament factories Complete.")
        }
    }

}