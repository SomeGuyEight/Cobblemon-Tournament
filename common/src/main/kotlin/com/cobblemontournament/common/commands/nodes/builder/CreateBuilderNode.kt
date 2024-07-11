package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.CREATE
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.NEW
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object CreateBuilderNode
{
    /**
     * [TOURNAMENT] - [BUILDER] - [CREATE] - [NEW]+[BUILDER_NAME]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      literal     [CREATE]        ->
     *      argument    [NEW]+[BUILDER_NAME] , StringType ->
     *      _
     */
    @JvmStatic
    fun createBuilder(
        literal: LiteralArgumentBuilder <CommandSourceStack>
    ): LiteralArgumentBuilder <CommandSourceStack> {
        return inner( literal = literal, argument = null )
    }

    /**
     * [TOURNAMENT] - [BUILDER] - [CREATE] - [NEW]+[BUILDER_NAME]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      literal     [CREATE]        ->
     *      argument    [NEW]+[BUILDER_NAME] , StringType ->
     *      _
     */
    @JvmStatic
    fun createBuilder(
        argument: RequiredArgumentBuilder<CommandSourceStack, *>
    ): LiteralArgumentBuilder <CommandSourceStack> {
        return inner( literal = null, argument = argument )
    }

    @JvmStatic
    private fun inner(
        literal     : LiteralArgumentBuilder <CommandSourceStack>?      = null,
        argument    : RequiredArgumentBuilder <CommandSourceStack,*>?   = null
    ): LiteralArgumentBuilder <CommandSourceStack>
    {
        val builder = literal ?: argument
        return BuilderNode.initialNode(
            Commands.literal( CREATE )
                .then( Commands.argument( "$NEW$BUILDER_NAME", StringArgumentType.string() )
                    .executes { ctx ->
                        CommandUtil.displayNoArgument(
                            player = ctx.source.player,
                            nodeKey = "$CREATE $NEW$BUILDER_NAME" )
                    }
                    .then( builder ) )
        )
    }

}
