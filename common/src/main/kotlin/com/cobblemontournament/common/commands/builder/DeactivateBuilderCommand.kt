package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.storage.store.TournamentBuilderStore
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.util.getTournamentBuilderOrDisplayFail
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.*
import com.sg8.api.command.CommandContext
import com.sg8.api.command.node.ExecutionNode
import com.sg8.util.*
import net.minecraft.commands.*
import net.minecraft.server.level.ServerPlayer

/**
 * [TOURNAMENT]-[BUILDER]-([ACTIVE] or [HISTORY])-[BUILDER_NAME]-([DEACTIVATE] or [DELETE])
 */
object DeactivateBuilderCommand {

    val executionNode = ExecutionNode { deactivate(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val deleteStack = Commands
            .literal(DELETE)
            .executes(this.executionNode.handler)

        val deactivateOrDeleteStack = Commands
            .literal(DEACTIVATE)
            .executes(this.executionNode.handler)
            .then(deleteStack)

        dispatcher.register(ActiveBuilderNameNode.nest(deactivateOrDeleteStack))
        dispatcher.register(BuilderHistoryNameNode.nest(deleteStack))
    }

    private fun deactivate(ctx: CommandContext): Int {
        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(storeID = null)
            ?: return 0

        if (ctx.nodes.any { it.node.name == DELETE }) {
            return deleteBuilder(ctx.source.player, tournamentBuilder)
        }

        return transferBuilder(tournamentBuilder, ctx.source.player)
    }

    private fun deleteBuilder(player: ServerPlayer?, builder: TournamentBuilder): Int {
        val deleted = TournamentStoreManager
            .deleteInstance(
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
        val transferred = TournamentStoreManager
            .transferInstance(
                storeClass = TournamentBuilderStore::class.java,
                currentStoreID = TournamentStoreManager.ACTIVE_STORE_ID,
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
