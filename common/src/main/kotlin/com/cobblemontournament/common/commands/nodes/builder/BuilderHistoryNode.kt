package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [BUILDER] - [HISTORY]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [BUILDER]       ->
 *      literal     [HISTORY]       ->
 *      _
 */
object BuilderHistoryNode : NestedNode() {

    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player  = it.source.player,
            nodeKey = "$TOURNAMENT $BUILDER $HISTORY",
            )
    }

    override fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>?,
        argument: RequiredArgumentBuilder<CommandSourceStack, *>?,
        execution: ExecutionNode?,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return BuilderNode
            .nest(Commands
                .literal(HISTORY)
                .executes((execution ?: this.executionNode).node)
                .then((literal ?: argument))
            )
    }

}
