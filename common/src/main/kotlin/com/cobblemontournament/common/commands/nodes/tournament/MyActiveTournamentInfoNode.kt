package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.INFO
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object MyActiveTournamentInfoNode
{
    /**
     * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] - [TOURNAMENT_NAME] - [INFO]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [TOURNAMENT]        ->
     *      literal     [MY_ACTIVE]         ->
     *      argument    [TOURNAMENT_NAME] , StringType ->
     *      literal     [INFO]              ->
     *      _
     */
    @JvmStatic
    fun node(
        literal: LiteralArgumentBuilder <CommandSourceStack>
    ): LiteralArgumentBuilder <CommandSourceStack> {
        return inner( literal = literal, argument = null )
    }

    /**
     * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] - [TOURNAMENT_NAME] - [INFO]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [TOURNAMENT]        ->
     *      literal     [MY_ACTIVE]         ->
     *      argument    [TOURNAMENT_NAME] , StringType ->
     *      literal     [INFO]              ->
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
        return MyActiveTournamentNode.node(
            Commands.literal( INFO )
                .executes { ctx ->
                    CommandUtil.displayNoArgument(
                        player = ctx.source.player,
                        nodeKey = "$TOURNAMENT $MY_ACTIVE $INFO"
                    )
                }
                .then(argumentBuilder))
    }

}
