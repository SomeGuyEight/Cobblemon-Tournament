package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.INFO
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [TOURNAMENT] - [HISTORY] - [TOURNAMENT_NAME] - [INFO]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [TOURNAMENT]    ->
 *      literal     [HISTORY]       ->
 *      argument    [TOURNAMENT_NAME] , StringType ->
 *      literal     [INFO]          ->
 *      _
 */
object TournamentHistoryInfoNode : NestedNode()
{
    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player  = it.source.player,
            nodeKey = "$TOURNAMENT $TOURNAMENT $HISTORY $TOURNAMENT_NAME $INFO" )
    }

    override fun inner(
        literal     : LiteralArgumentBuilder <CommandSourceStack>?,
        argument    : RequiredArgumentBuilder <CommandSourceStack,*>?,
        execution   : ExecutionNode?
    ): LiteralArgumentBuilder <CommandSourceStack>
    {
        val stack = literal ?: argument
        return TournamentHistoryNameNode.nest(
            Commands.literal( INFO )
                .executes( ( execution ?: this.executionNode ).node )
                .then( stack )
        )
    }
}
