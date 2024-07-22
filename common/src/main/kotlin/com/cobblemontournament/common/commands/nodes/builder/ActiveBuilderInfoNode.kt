package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.INFO
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [BUILDER] - [ACTIVE] - [BUILDER_NAME] - [INFO]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [BUILDER]       ->
 *      literal     [ACTIVE]        ->
 *      argument    [BUILDER_NAME] , StringType ->
 *      literal     [INFO]          ->
 *      _
 */
object ActiveBuilderInfoNode : NestedNode() {

    override val executionNode = ExecutionNode {
        CommandUtil.displayNoArgument(
            player = it.source.player,
            nodeKey = "$TOURNAMENT $BUILDER $ACTIVE $BUILDER_NAME $INFO",
            )
    }

    override fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>?,
        argument: RequiredArgumentBuilder<CommandSourceStack, *>?,
        execution: ExecutionNode?,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return ActiveBuilderNameNode
            .nest(Commands
                .literal(INFO)
                .executes((execution ?: this.executionNode).node)
                .then((literal ?: argument))
            )
    }

}
