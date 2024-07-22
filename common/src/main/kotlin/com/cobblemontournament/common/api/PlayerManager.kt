package com.cobblemontournament.common.api

import com.cobblemontournament.common.CobblemonTournament
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

object PlayerManager {

    private val server: MinecraftServer? get() = CobblemonTournament.server

    fun getServerPlayer(playerID: UUID) = server?.playerList?.getPlayer(playerID)

    fun getServerPlayers(playerIDs: Set<UUID>): Set<ServerPlayer> {
        val players = mutableSetOf<ServerPlayer>()
        for (id in playerIDs) {
            val player = getServerPlayer(id)
                ?: continue
            players.add(player)
        }
        return players
    }

}
