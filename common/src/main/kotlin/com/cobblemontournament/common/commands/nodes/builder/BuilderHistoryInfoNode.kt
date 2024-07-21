package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.INFO
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.suggestions.ClassStoredNameSuggestionProvider
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [BUILDER] - [HISTORY] - [BUILDER_NAME] - [INFO]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [BUILDER]       ->
 *      literal     [HISTORY]       ->
 *      argument    [BUILDER_NAME] , StringType ->
 *      literal     [INFO]       ->
 *      _
 */
object BuilderHistoryInfoNode : NestedNode()
{
    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player  = it.source.player,
            nodeKey = "$TOURNAMENT $BUILDER $HISTORY $BUILDER_NAME $INFO" )
    }

    override fun inner(
        literal     : LiteralArgumentBuilder <CommandSourceStack>?,
        argument    : RequiredArgumentBuilder <CommandSourceStack,*>?,
        execution   : ExecutionNode?
    ): LiteralArgumentBuilder <CommandSourceStack>
    {
        val stack = literal ?: argument
        return BuilderHistoryNameNode.nest(
            Commands.literal( INFO )
                .executes( ( execution ?: this.executionNode ).node )
                .then( stack )
        )
    }
}
