package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.commands.suggestions.BuilderNameSuggestionProvider
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object BuilderHistoryNode
{
    /**
     * [TOURNAMENT] - [BUILDER] - [HISTORY] - [BUILDER_NAME]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      literal     [HISTORY]       ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      _
     */
    @JvmStatic
    fun getHistory(
        literal: LiteralArgumentBuilder<CommandSourceStack>
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner( literal = literal, argument = null)
    }

    /**
     * [TOURNAMENT] - [BUILDER] - [HISTORY] - [BUILDER_NAME]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      literal     [HISTORY]       ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      _
     */
    @JvmStatic
    fun getHistory(
        argument: RequiredArgumentBuilder<CommandSourceStack, *>
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
        return BuilderNode.initialNode(
            Commands.literal(HISTORY)
                .executes { ctx ->
                    CommandUtil.displayNoArgument(
                        player = ctx.source.player,
                        nodeKey = HISTORY
                    )
                }
                .then(Commands.argument(BUILDER_NAME, StringArgumentType.string())
                    .suggests(BuilderNameSuggestionProvider())
                    .executes { ctx ->
                        CommandUtil.displayNoArgument(
                            player = ctx.source.player,
                            nodeKey = "$HISTORY $BUILDER_NAME"
                        )
                    }
                    .then(builder))
        )
    }

}
