package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.suggestions.ClassStoredNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [TOURNAMENT] - [HISTORY] - [TOURNAMENT_NAME]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [TOURNAMENT]    ->
 *      literal     [HISTORY]       ->
 *      argument    [TOURNAMENT_NAME] , StringType ->
 *      _
 */
object TournamentHistoryNameNode : NestedNode() {

    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player = it.source.player,
            nodeKey = "$TOURNAMENT $TOURNAMENT $HISTORY $TOURNAMENT_NAME",
            )
    }

    private val suggestionProvider by lazy {
        ClassStoredNameSuggestionProvider(
            storeClass = TournamentStore::class.java,
            getActive = false,
            )
    }

    override fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>?,
        argument: RequiredArgumentBuilder<CommandSourceStack, *>?,
        execution: ExecutionNode?
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return TournamentHistoryNode
            .nest(Commands
                .argument(TOURNAMENT_NAME, StringArgumentType.word())
                .suggests(suggestionProvider)
                .executes((execution ?: this.executionNode).node)
                .then((literal ?: argument))
            )
    }
}
