package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTIONS
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] - [TOURNAMENT_NAME] - [ACTIONS]
 *
 *      literal     [TOURNAMENT]        ->
 *      literal     [TOURNAMENT]        ->
 *      literal     [MY_ACTIVE]         ->
 *      argument    [TOURNAMENT_NAME] , StringType ->
 *      literal     [ACTIONS]           ->
 *      _
 */
object MyActiveTournamentActionsNode : NestedNode()
{
    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player  = it.source.player,
            nodeKey = "$TOURNAMENT $TOURNAMENT $MY_ACTIVE $TOURNAMENT_NAME $ACTIONS" )
    }

    override fun inner(
        literal     : LiteralArgumentBuilder <CommandSourceStack>?,
        argument    : RequiredArgumentBuilder <CommandSourceStack,*>?,
        execution   : ExecutionNode?
    ): LiteralArgumentBuilder <CommandSourceStack>
    {
        val stack = literal ?: argument
        return MyActiveTournamentNameNode.nest(
            Commands.literal( ACTIONS )
                .executes( ( execution ?: this.executionNode ).node )
                .then( stack )
        )
    }
}
