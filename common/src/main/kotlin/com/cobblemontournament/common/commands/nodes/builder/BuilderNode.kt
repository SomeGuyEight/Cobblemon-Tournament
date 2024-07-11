package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.commands.nodes.TournamentRootNode
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.util.CommandUtil
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object BuilderNode
{
    /**
     * [TOURNAMENT] - [BUILDER]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      _
     */
    @JvmStatic
    fun initialNode(
        literal: LiteralArgumentBuilder<CommandSourceStack>
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner(literal = literal, argument = null)
    }

    /**
     * [TOURNAMENT] - [BUILDER]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      _
     */
    @JvmStatic
    fun initialNode(
        argument: RequiredArgumentBuilder<CommandSourceStack, *>
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner(literal = null, argument = argument)
    }

    @JvmStatic
    private fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>? = null,
        argument: RequiredArgumentBuilder<CommandSourceStack,*>? = null
    ): LiteralArgumentBuilder<CommandSourceStack>
    {
        val builder = literal?: argument
        return TournamentRootNode.initialNode(
            Commands.literal(BUILDER)
                .executes { ctx ->
                    CommandUtil.displayNoArgument(
                        player  = ctx.source.player,
                        nodeKey = BUILDER)
                }
                .then( builder)
        )
    }

}
