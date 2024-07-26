package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer

/**
 * [TOURNAMENT]-[BUILDER]-([ACTIVE] or [HISTORY])-[BUILDER_NAME]-([DEACTIVATE] or [DELETE])
 */
object DeactivateBuilderCommand {

    val executionNode by lazy { ExecutionNode { deactivate(ctx = it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val deleteStack = Commands
            .literal(DELETE)
            .executes(this.executionNode.action)

        val deactivateOrDeleteStack = Commands
            .literal(DEACTIVATE)
            .executes(this.executionNode.action)
            .then(deleteStack)

        dispatcher.register(ActiveBuilderNameNode.nest(deactivateOrDeleteStack))
        dispatcher.register(BuilderHistoryNameNode.nest(deleteStack))
    }

    private fun deactivate(ctx: CommandContext): Int {
        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(storeID = null) ?: return 0

        if (ctx.nodes.any { it.node.name == DELETE }) {
            return deleteBuilder(ctx.source.player, tournamentBuilder)
        }

        return transferBuilder(tournamentBuilder, ctx.source.player)
    }

    private fun deleteBuilder(player: ServerPlayer?, builder: TournamentBuilder): Int {
        val deleted = TournamentStoreManager.deleteInstance(
            storeClass = TournamentBuilderStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            instance = builder,
        )

        if (!deleted) {
            player.displayCommandFail(reason = "Failed to DELETE in store \"${builder.name}\"")
            return 0
        }

        player.displayCommandSuccess(text = "DELETED Tournament Builder \"${builder.name}\"")

        return Command.SINGLE_SUCCESS
    }

    private fun transferBuilder(builder: TournamentBuilder, player: ServerPlayer?): Int {
        val transferred = TournamentStoreManager.transferInstance(
            storeClass = TournamentBuilderStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            newStoreID = TournamentStoreManager.INACTIVE_STORE_ID,
            instance = builder,
        )

        if (transferred) {
            player.displayCommandFail(reason = "Failed to DEACTIVATE in store \"${builder.name}\"")
            return 0
        }

        player.displayCommandSuccess(text = "DEACTIVATED Tournament Builder \"${builder.name}\"")

        return Command.SINGLE_SUCCESS
    }

}
