package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object ActivePlayersBuilderNode
{
    /**
     * [TOURNAMENT] - [BUILDER] - [BUILDER_NAME] - [PLAYER]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [BUILDER]           ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [PLAYER]            ->
     *      _
     */
    @JvmStatic
    fun player(
        literal: LiteralArgumentBuilder<CommandSourceStack>,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner( literal = literal, argument = null)
    }

    /**
     * [TOURNAMENT] - [BUILDER] - [BUILDER_NAME] - [PLAYER]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [BUILDER]           ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [PLAYER]            ->
     *      _
     */
    @JvmStatic
    fun player(
        argument: RequiredArgumentBuilder<CommandSourceStack, *>,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner( literal = null, argument = argument)
    }

    @JvmStatic
    private fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>? = null,
        argument: RequiredArgumentBuilder<CommandSourceStack,*>? = null
    ): LiteralArgumentBuilder<CommandSourceStack>
    {
        val builder = literal?: argument
        return ActiveBuilderNode.node(
            Commands.literal(PLAYER)
                .executes { ctx ->
                    CommandUtil.displayNoArgument(
                        player = ctx.source.player,
                        nodeKey = PLAYER
                    )
                }
                .then(builder)
        )
    }

}
