package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.suggestions.ClassStoredNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [BUILDER] - [HISTORY] - [BUILDER_NAME]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [BUILDER]       ->
 *      literal     [HISTORY]       ->
 *      argument    [BUILDER_NAME] , StringType ->
 *      _
 */
object BuilderHistoryNameNode : NestedNode() {

    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player = it.source.player,
            nodeKey = "$TOURNAMENT $BUILDER $HISTORY $BUILDER_NAME",
            )
    }

    private val suggestionProvider by lazy {
        ClassStoredNameSuggestionProvider(
            storeClass = TournamentBuilderStore::class.java,
            getActive = false,
            )
    }

    override fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>?,
        argument: RequiredArgumentBuilder<CommandSourceStack, *>?,
        execution: ExecutionNode?,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return BuilderHistoryNode.nest(
            Commands.argument(BUILDER_NAME, StringArgumentType.string())
                .suggests(suggestionProvider)
                .executes((execution ?: this.executionNode).node)
                .then((literal ?: argument))
        )
    }

}
