package com.cobblemontournament.common

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemontournament.common.api.WatchedMatches
import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.commands.TournamentCommands
import com.someguy.api.ModImplementation
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.PlayerEvent
import org.slf4j.helpers.Util

object CobblemonTournament : ModImplementation(
    modID           = COMPANION.MOD_ID,
    commandManager  = TournamentCommands )
{
    object COMPANION {
        const val MOD_ID = "cobblemontournament"
    }

    override fun registerEvents()
    {
        super.registerEvents()

        LifecycleEvent.SERVER_STARTING.register {
            TournamentStoreManager.initialize( it )
        }

        LifecycleEvent.SERVER_STOPPED.register {
            Util.report("Saving Tournament Factories...")
            TournamentStoreManager.unregisterAll()
            Util.report("Saving Factories Complete.")
        }

        PlayerEvent.PLAYER_QUIT.register( WatchedMatches::handlePlayerLogoutEvent )

        CobblemonEvents.BATTLE_VICTORY.subscribe( Priority.NORMAL ) {
                WatchedMatches.handleBattleVictoryEvent( it )
        }
    }
}
