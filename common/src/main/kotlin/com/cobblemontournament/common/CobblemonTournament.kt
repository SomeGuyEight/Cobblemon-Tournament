package com.cobblemontournament.common

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemontournament.common.api.match.MatchManager
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.commands.TournamentCommands
import com.sg8.api.modimplementation.ModImplementation
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.PlayerEvent
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

const val MOD_ID = "cobblemontournament"

object CobblemonTournament : ModImplementation(TournamentCommands) {

    override fun registerEvents() {
        super.registerEvents()

        LifecycleEvent.SERVER_STARTED.register { _ ->
            TournamentStoreManager.initialize()
        }

        LifecycleEvent.SERVER_STOPPED.register { _ ->
            Util.report(("Saving Tournament Factories..."))
            TournamentStoreManager.unregisterAll()
            Util.report(("Saving Factories Complete."))
        }

        PlayerEvent.PLAYER_QUIT.register(MatchManager::handlePlayerLogoutEvent)

        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL) { event ->
            MatchManager.handleBattleVictoryEvent(event = event)
        }
    }

    fun getServerPlayer(playerUuid: UUID) = server?.playerList?.getPlayer(playerUuid)

    fun getServerPlayers(playerIDs: Set<UUID>): Set<ServerPlayer> {
        val players = mutableSetOf<ServerPlayer>()
        for (playerID in playerIDs) {
            val player = getServerPlayer(playerID) ?: continue
            players.add(player)
        }
        return players
    }

}
