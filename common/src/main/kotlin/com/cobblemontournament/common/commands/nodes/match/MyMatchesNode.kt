package com.cobblemontournament.common.commands.nodes.match

import com.cobblemontournament.common.commands.match.MyMatchesCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_MATCHES
import com.cobblemontournament.common.commands.nodes.TournamentRootNode
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [MY_MATCHES]
 *
 *      literal     [TOURNAMENT]        ->
 *      literal     [MY_MATCHES]        ->
 *      _
 */
object MyMatchesNode : NestedNode()
{
    override val executionNode get() = MyMatchesCommand.executionNode

    override fun inner(
        literal     : LiteralArgumentBuilder <CommandSourceStack>?,
        argument    : RequiredArgumentBuilder <CommandSourceStack,*>?,
        execution   : ExecutionNode?
    ): LiteralArgumentBuilder <CommandSourceStack>
    {
        val stack = literal ?: argument
        return TournamentRootNode.nest(
            Commands.literal( MY_MATCHES )
                .executes( ( execution ?: this.executionNode ).node )
                .then( stack )
        )
    }

}
