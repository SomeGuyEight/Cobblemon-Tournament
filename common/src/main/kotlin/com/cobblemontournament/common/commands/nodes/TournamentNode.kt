package com.cobblemontournament.common.commands.nodes

import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.util.CommandUtil.logNoArgument
import com.cobblemontournament.common.tournament.Tournament
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object TournamentNode
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
            return inner(literal = literal, argument = null)
    }

    /**
     * [TOURNAMENT]
     *
     *      literal     [TOURNAMENT] ->
     *      _
     */
    fun initialNode(
        argument: RequiredArgumentBuilder<CommandSourceStack, String>
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner(literal = null, argument = argument)
    }

    private fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>? = null,
        argument: RequiredArgumentBuilder<CommandSourceStack,*>? = null
    ): LiteralArgumentBuilder<CommandSourceStack>
    {
        val builder = literal?: argument
        return Commands.literal(TOURNAMENT)
            .executes { c: CommandContext<CommandSourceStack> ->
                logNoArgument(
                    StringArgumentType.getString(c,TOURNAMENT),
                    Tournament::class.java.simpleName)
            }
            .then( builder)
    }

}