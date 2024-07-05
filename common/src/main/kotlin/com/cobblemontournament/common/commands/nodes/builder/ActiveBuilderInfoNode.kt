package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.commands.util.CommandUtil.logNoArgument
import com.cobblemontournament.common.commands.util.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.INFO
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object ActiveBuilderInfoNode
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [ACTIVE] -> [BUILDER_NAME] -> [INFO]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      literal     [ACTIVE]        ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [INFO]          ->
     *      _
     */
    @JvmStatic
    fun getInfo(
        literal: LiteralArgumentBuilder<CommandSourceStack>
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner(literal = literal, argument = null)
    }

    /**
     * [TOURNAMENT] -> [BUILDER] -> [ACTIVE] -> [BUILDER_NAME] -> [INFO]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      literal     [ACTIVE]        ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [INFO]          ->
     *      _
     */
    @JvmStatic
    fun getInfo(
        argument: RequiredArgumentBuilder<CommandSourceStack, *>
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner( literal = null, argument = argument)
    }

    @JvmStatic
    private fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>? = null,
        argument: RequiredArgumentBuilder<CommandSourceStack, *>? = null
    ): LiteralArgumentBuilder<CommandSourceStack>
    {
        val builder = literal?: argument
        return ActiveBuilderNode.activeBuilder(
            Commands.literal( INFO)
                .executes { logNoArgument( INFO, TournamentBuilder::class.java.simpleName) }
                .executes { c: CommandContext<CommandSourceStack> ->
                    logNoArgument(
                        StringArgumentType.getString(c, BUILDER_NAME),
                        TournamentBuilder::class.java.simpleName)
                }
                .then( builder)
        )
    }
}
