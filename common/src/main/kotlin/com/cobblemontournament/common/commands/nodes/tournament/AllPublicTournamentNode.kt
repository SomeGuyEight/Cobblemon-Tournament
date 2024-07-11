package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.PUBLIC
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.suggestions.TournamentNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object AllPublicTournamentNode
{
    /**
     * [TOURNAMENT] - [TOURNAMENT] - [PUBLIC] - [TOURNAMENT_NAME]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [TOURNAMENT]    ->
     *      literal     [PUBLIC]        ->
     *      argument    [TOURNAMENT_NAME] , StringType ->
     *      _
     */
    @JvmStatic
    fun allPublicNode(
        literal: LiteralArgumentBuilder<CommandSourceStack>
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner(literal = literal, argument = null)
    }

    /**
     * [TOURNAMENT] - [TOURNAMENT] - [PUBLIC] - [TOURNAMENT_NAME]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [TOURNAMENT]    ->
     *      literal     [PUBLIC]        ->
     *      argument    [TOURNAMENT_NAME] , StringType ->
     *      _
     */
    @JvmStatic
    fun allPublicNode(
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
            Commands.literal( PUBLIC )
                .then( Commands.argument( TOURNAMENT_NAME, StringArgumentType.word() )
                    // TODO add 'public' to to Tournament properties
                    .suggests( TournamentNameSuggestionProvider( restrictToPlayer = false ) )
                    .executes { ctx ->
                        CommandUtil.displayNoArgument(
                            player = ctx.source.player,
                            nodeKey = "$TOURNAMENT $PUBLIC $TOURNAMENT_NAME" )
                    }
                    .then( argumentBuilder ) )
        )
    }

}
