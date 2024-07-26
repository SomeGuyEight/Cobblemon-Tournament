package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[BUILDER]-[HISTORY]-[BUILDER_NAME]-[ACTIVATE]
 */
object ActivateBuilderCommand {

    val executionNode by lazy { ExecutionNode { activate(ctx = it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher
            .register(BuilderHistoryNameNode
                .nest(Commands
                    .literal(ACTIVATE)
                    .executes(this.executionNode.action)
                )
            )
    }

    private fun activate(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.source.player

        val builder = ctx.getTournamentBuilderOrDisplayFail(
            storeID = TournamentStoreManager.INACTIVE_STORE_ID
        ) ?: return 0

        val transferred = TournamentStoreManager.transferInstance(
            storeClass = TournamentBuilderStore::class.java,
            storeID = TournamentStoreManager.INACTIVE_STORE_ID,
            newStoreID = TournamentStoreManager.ACTIVE_STORE_ID,
            instance = builder,
        )

        if (!transferred) {
            player.displayCommandFail(reason = "Failed inside store during transfer.")
            return 0
        }

        player.displayCommandSuccess(text = "ACTIVATED Tournament Builder \"${builder.name}\"")

        return Command.SINGLE_SUCCESS
    }

}
