package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.DEACTIVATE
import com.cobblemontournament.common.commands.nodes.NodeKeys.DELETE
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderNameNode
import com.cobblemontournament.common.commands.nodes.builder.BuilderHistoryNameNode
import com.cobblemontournament.common.util.CommandUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util

/**
 * [TOURNAMENT] - [BUILDER] - [ACTIVE] or [HISTORY]
 *
 * [BUILDER_NAME] - [DEACTIVATE] or [DELETE] -> [deactivate]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [BUILDER]       ->
 *      literal     [ACTIVE] or [HISTORY]       ->
 *      argument    [BUILDER_NAME] , StringType ->
 *      literal     [DEACTIVATE] or [DELETE]    ->
 *      * literal   [DELETE]        ->
 *      function    [deactivate]
 *
 *      * - optional with [ACTIVE]
 */
object DeactivateBuilderCommand : ExecutableCommand {

    override val executionNode get() = ExecutionNode { deactivate(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val deleteStack = Commands
            .literal(DELETE)
            .executes(this.executionNode.node)

        val deactivateOrDeleteStack = Commands
            .literal(DEACTIVATE)
            .executes(this.executionNode.node)
            .then(deleteStack)

        dispatcher.register(ActiveBuilderNameNode.nest(deactivateOrDeleteStack))
        dispatcher.register(BuilderHistoryNameNode.nest(deleteStack))
    }

    private fun deactivate(ctx: CommandContext<CommandSourceStack>): Int {
        val (nodeEntries, tournamentBuilder) = CommandUtil
            .getNodesAndTournamentBuilder(
                ctx = ctx,
                storeID = null,
                )

        var success = 0
        val text: MutableComponent = when {
            tournamentBuilder == null -> {
                CommandUtil.failedCommand(reason = "Tournament Builder was null")
            }
            nodeEntries.any { it.key == DELETE } -> {
                val deleted = TournamentStoreManager.deleteInstance(
                    storeClass = TournamentBuilderStore::class.java,
                    storeID = TournamentStoreManager.ACTIVE_STORE_ID,
                    instance = tournamentBuilder,
                    )
                if (deleted) {
                    success = Command.SINGLE_SUCCESS
                    CommandUtil.successfulCommand(text = "DELETED Tournament Builder \"${tournamentBuilder.name}\"",)
                } else {
                    CommandUtil.failedCommand(reason = "Failed to DELETE in store \"${tournamentBuilder.name}\"")
                }
            }
            else -> {
                val transferred = TournamentStoreManager.transferInstance(
                    storeClass = TournamentBuilderStore::class.java,
                    storeID = TournamentStoreManager.ACTIVE_STORE_ID,
                    newStoreID = TournamentStoreManager.INACTIVE_STORE_ID,
                    instance = tournamentBuilder,
                    )
                if (transferred) {
                    success = Command.SINGLE_SUCCESS
                    CommandUtil.successfulCommand(text = "DEACTIVATED Tournament Builder \"${tournamentBuilder.name}\"")
                } else {
                    CommandUtil.failedCommand(reason = "Failed to DEACTIVATE in store \"${tournamentBuilder.name}\"")
                }
            }
        }

        ctx.source.player?.displayClientMessage(text ,false)
            ?: Util.report(text.string)

        return success
    }
}
