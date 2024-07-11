package com.cobblemontournament.common.commands.nodes

import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object TournamentRootNode
{
    /**
     * [TOURNAMENT]
     *
     *      literal     [TOURNAMENT] ->
     *      _
     */
    fun initialNode(
        literal: LiteralArgumentBuilder<CommandSourceStack>
    ): LiteralArgumentBuilder<CommandSourceStack> {
            return inner( literal = literal, argument = null )
    }

    /**
     * [TOURNAMENT]
     *
     *      literal     [TOURNAMENT] ->
     *      _
     */
    fun initialNode(
        argument: RequiredArgumentBuilder <CommandSourceStack, String>
    ): LiteralArgumentBuilder <CommandSourceStack> {
        return inner( literal = null, argument = argument )
    }

    private fun inner(
        literal: LiteralArgumentBuilder <CommandSourceStack>? = null,
        argument: RequiredArgumentBuilder <CommandSourceStack, *>? = null
    ): LiteralArgumentBuilder<CommandSourceStack>
    {
        val builder = literal?: argument
        return Commands.literal( TOURNAMENT )
            .executes { ctx ->
                CommandUtil.displayNoArgument(
                    player  = ctx.source.player,
                    nodeKey = TOURNAMENT )
            }
            .then( builder )
    }

}