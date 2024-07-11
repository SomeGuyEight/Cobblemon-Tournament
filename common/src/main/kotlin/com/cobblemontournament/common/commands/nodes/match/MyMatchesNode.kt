package com.cobblemontournament.common.commands.nodes.match

import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_MATCHES
import com.cobblemontournament.common.commands.nodes.TournamentRootNode
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object MyMatchesNode
{
    /**
     * [TOURNAMENT] - [MY_MATCHES]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [MY_MATCHES]        ->
     *      _
     */
    @JvmStatic
    fun node(
        literal: LiteralArgumentBuilder <CommandSourceStack>
    ): LiteralArgumentBuilder <CommandSourceStack> {
        return inner( literal = literal, argument = null )
    }

    /**
     * [TOURNAMENT] - [MY_MATCHES]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [MY_MATCHES]        ->
     *      _
     */
    @JvmStatic
    fun node(
        argument: RequiredArgumentBuilder <CommandSourceStack,*>
    ): LiteralArgumentBuilder <CommandSourceStack> {
        return inner( literal = null, argument = argument )
    }

    @JvmStatic
    private fun inner(
        literal     : LiteralArgumentBuilder <CommandSourceStack>?      = null,
        argument    : RequiredArgumentBuilder <CommandSourceStack,*>?   = null
    ): LiteralArgumentBuilder <CommandSourceStack>
    {
        val argumentBuilder = literal ?: argument
        return TournamentRootNode.initialNode(
            Commands.literal( MY_MATCHES )
                .executes { ctx ->
                    CommandUtil.displayNoArgument(
                        player  = ctx.source.player,
                        nodeKey = MY_MATCHES )
                }
                .then( argumentBuilder )
        )
    }

}
