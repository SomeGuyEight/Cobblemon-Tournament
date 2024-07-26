package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[BUILDER]-[CREATE]-[NEW]+[BUILDER_NAME]
 */
object CreateTournamentBuilderCommand {

    val executionNode by lazy { ExecutionNode { createNewBuilder(ctx = it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher
            .register(CreateBuilderNode
                .nest(Commands
                    .argument("$NEW$BUILDER_NAME", StringArgumentType.string())
                    .executes(this.executionNode.action)
                    // TODO add more optional properties to assign
                )
            )
    }

    private fun createNewBuilder(ctx: CommandContext<CommandSourceStack>): Int {
        val name = ctx.getNodeInputRangeOrDisplayFail(nodeName = "$NEW$BUILDER_NAME") ?: return 0

        CommandUtil.tryGetInstance(TournamentBuilderStore::class.java, name)
            ?: let { _ ->
                ctx.source.player.displayCommandFail(
                    reason = "A tournament builder named \"$name\" exists."
                )
                return 0
            }

        val success = TournamentStoreManager.addInstance(
            storeClass = TournamentBuilderStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            instance = TournamentBuilder().initialize(),
        )

        if (!success) {
            ctx.source.player.displayCommandFail(reason = "Failed to add inside store.")
            return 0
        }

        ctx.source.player.displayCommandSuccess(text = "CREATED Tournament Builder \"$name\"")

        return Command.SINGLE_SUCCESS
    }

}
