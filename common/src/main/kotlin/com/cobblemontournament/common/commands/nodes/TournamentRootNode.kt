package com.cobblemontournament.common.commands.nodes

import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]
 *
 *      literal     [TOURNAMENT] ->
 *      _
 */
object TournamentRootNode : NestedNode()
{
    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player  = it.source.player,
            nodeKey = TOURNAMENT )
    }

    override fun inner(
        literal     : LiteralArgumentBuilder <CommandSourceStack>?,
        argument    : RequiredArgumentBuilder <CommandSourceStack,*>?,
        execution   : ExecutionNode?
    ): LiteralArgumentBuilder <CommandSourceStack>
    {
        val stack = literal ?: argument
        return Commands.literal( TOURNAMENT )
            .executes( ( execution ?: this.executionNode ).node )
            .then( stack )
    }

}