package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.PUBLIC
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.suggestions.ClassStoredNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [TOURNAMENT] - [PUBLIC] - [TOURNAMENT_NAME]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [TOURNAMENT]    ->
 *      literal     [PUBLIC]        ->
 *      argument    [TOURNAMENT_NAME] , StringType ->
 *      _
 */
object AllPublicTournamentNameNode : NestedNode()
{
    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player = it.source.player,
            nodeKey = "$TOURNAMENT $TOURNAMENT $PUBLIC $TOURNAMENT_NAME"
        )
    }

    private val suggestionProvider by lazy {
        ClassStoredNameSuggestionProvider(
            storeClass = TournamentStore::class.java,
            getActive = true,
            )
    }

    override fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>?,
        argument: RequiredArgumentBuilder<CommandSourceStack, *>?,
        execution: ExecutionNode?
    ): LiteralArgumentBuilder<CommandSourceStack> {
        val stack = literal?: argument
        return AllPublicTournamentNode.nest(
            Commands.argument( TOURNAMENT_NAME, StringArgumentType.word() )
                // TODO add 'public' to to Tournament properties
                .suggests( suggestionProvider )
                .executes( (execution?: this.executionNode).node )
                .then( stack )
        )
    }
}
