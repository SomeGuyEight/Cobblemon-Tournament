package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.commands.nodes.TournamentRootNode
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.util.CommandUtil
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [TOURNAMENT]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [TOURNAMENT]    ->
 *      _
 */
object TournamentSubNode : NestedNode()
{
    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player  = it.source.player,
            nodeKey = "$TOURNAMENT $TOURNAMENT" )
    }

    override fun inner(
        literal     : LiteralArgumentBuilder <CommandSourceStack>?,
        argument    : RequiredArgumentBuilder <CommandSourceStack,*>?,
        execution   : ExecutionNode?
    ): LiteralArgumentBuilder <CommandSourceStack>
    {
        val stack = literal ?: argument
        return TournamentRootNode.nest(
            Commands.literal( TOURNAMENT )
                .executes( ( execution ?: this.executionNode ).node )
                .then( stack )
        )
    }
}
