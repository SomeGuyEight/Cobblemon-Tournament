package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.storage.store.TournamentBuilderStore
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.util.getTournamentBuilderOrDisplayFail
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.sg8.api.command.node.ExecutionNode
import com.sg8.util.displayCommandFail
import com.sg8.util.displayCommandSuccess
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[BUILDER]-[HISTORY]-[BUILDER_NAME]-[ACTIVATE]
 */
object ActivateBuilderCommand {

    val executionNode = ExecutionNode { activate(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(BuilderHistoryNameNode
            .nest(Commands
                .literal(ACTIVATE)
                .executes(this.executionNode.handler)
            )
        )
    }

    private fun activate(ctx: CommandContext<CommandSourceStack>): Int {
        val builder = ctx.getTournamentBuilderOrDisplayFail(
            storeID = TournamentStoreManager.INACTIVE_STORE_ID
        ) ?: return 0

        val transferred = TournamentStoreManager.transferInstance(
            storeClass = TournamentBuilderStore::class.java,
            currentStoreID = TournamentStoreManager.INACTIVE_STORE_ID,
            newStoreID = TournamentStoreManager.ACTIVE_STORE_ID,
            instance = builder,
        )

        if (!transferred) {
            ctx.source.player.displayCommandFail(reason = "Failed inside store during transfer.")
            return 0
        }

        ctx.source.player.displayCommandSuccess(
            text = "ACTIVATED Tournament Builder \"${builder.name}\"",
        )

        return Command.SINGLE_SUCCESS
    }

}
