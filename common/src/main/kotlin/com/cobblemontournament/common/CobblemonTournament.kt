package com.cobblemontournament.common

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.platform.events.PlatformEvents.SERVER_STARTING
import com.cobblemon.mod.common.platform.events.PlatformEvents.SERVER_STOPPED
import com.cobblemonrental.common.CobblemonRental
import com.cobblemontournament.common.api.MatchManager
import com.cobblemontournament.common.api.TournamentStoreManager
import dev.architectury.event.events.common.PlayerEvent
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.helpers.Util

object CobblemonTournament
{
    const val MOD_ID: String = "cobblemontournament"
    val LOGGER: Logger = LoggerFactory.getLogger("cobblemon-tournament")
    var implementation: CTModImplementation? = null
    /** Don't call prior to server initialization. Has server ref cached after server starts. */
    internal var server: MinecraftServer? = null

    @JvmStatic
    fun initialize(
        implementation: CTModImplementation,
    )
    {
        CobblemonTournament.implementation = implementation
        implementation.registerEvents()
        implementation.initializeConfig()
        implementation.registerCommands()
    }

    @JvmStatic
    fun registerEvents()
    {
        SERVER_STARTING.subscribe(Priority.HIGHEST) { event ->
            server = event.server
            CobblemonRental.instance.initialize(event.server)
            TournamentStoreManager.initialize(event.server)
        }

        SERVER_STOPPED.subscribe(Priority.HIGHEST) {
            Util.report("Saving Tournament factories...")
            TournamentStoreManager.unregisterAll()
            Util.report("Saving Tournament factories Complete.")
        }

        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL) {
            event -> MatchManager.handleBattleVictoryEvent( event)
        }

        PlayerEvent.PLAYER_QUIT.register( MatchManager::handlePlayerLogoutEvent)
    }

}