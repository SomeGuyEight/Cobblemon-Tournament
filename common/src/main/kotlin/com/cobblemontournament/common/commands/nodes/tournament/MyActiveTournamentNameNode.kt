package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.suggestions.ClassStoredNameSuggestionProvider
import com.cobblemontournament.common.tournament.Tournament
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import java.util.UUID

/**
 * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] - [TOURNAMENT_NAME]
 *
 *      literal     [TOURNAMENT]        ->
 *      literal     [TOURNAMENT]        ->
 *      literal     [MY_ACTIVE]         ->
 *      argument    [TOURNAMENT_NAME] , StringType ->
 *      _
 */
object MyActiveTournamentNameNode : NestedNode()
{
    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player  = it.source.player,
            nodeKey = "$TOURNAMENT $TOURNAMENT $MY_ACTIVE $TOURNAMENT_NAME" )
    }

    private val predicate: (UUID) -> (Tournament) -> Boolean = {
            playerID -> { it.containsPlayerID( playerID ) }
    }

    private val suggestionProvider get() = ClassStoredNameSuggestionProvider(
        storeClass      = TournamentStore::class.java,
        getActive       = true,
        playerPredicate = predicate )

    override fun inner(
        literal     : LiteralArgumentBuilder <CommandSourceStack>?,
        argument    : RequiredArgumentBuilder <CommandSourceStack,*>?,
        execution   : ExecutionNode?
    ): LiteralArgumentBuilder <CommandSourceStack>
    {
        val stack = literal ?: argument
        return MyActiveTournamentNode.nest(
            Commands.argument( TOURNAMENT_NAME, StringArgumentType.word() )
                .suggests( suggestionProvider )
                .executes( ( execution ?: this.executionNode ).node )
                .then( stack )
        )
    }
}
