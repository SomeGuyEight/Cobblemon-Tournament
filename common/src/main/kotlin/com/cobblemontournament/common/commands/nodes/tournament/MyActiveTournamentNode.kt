package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.suggestions.TournamentNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object MyActiveTournamentNode
{
    /**
     * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] - [TOURNAMENT_NAME]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [TOURNAMENT]        ->
     *      literal     [MY_ACTIVE]            ->
     *      argument    [TOURNAMENT_NAME] , StringType ->
     *      _
     */
    @JvmStatic
    fun node(
        literal: LiteralArgumentBuilder <CommandSourceStack>
    ): LiteralArgumentBuilder <CommandSourceStack> {
        return inner( literal = literal, argument = null )
    }

    /**
     * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] - [TOURNAMENT_NAME]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [TOURNAMENT]        ->
     *      literal     [MY_ACTIVE]         ->
     *      argument    [TOURNAMENT_NAME] , StringType ->
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
        return TournamentSubNode.node(
            Commands.literal( MY_ACTIVE )
                .then( Commands.argument( TOURNAMENT_NAME, StringArgumentType.word() )
                    .suggests( TournamentNameSuggestionProvider( restrictToPlayer = true ) )
                    .executes { ctx ->
                        CommandUtil.displayNoArgument(
                            player  = ctx.source.player,
                            nodeKey = "$TOURNAMENT $MY_ACTIVE" )
                    }
                    .then( argumentBuilder ) )
        )
    }

}
