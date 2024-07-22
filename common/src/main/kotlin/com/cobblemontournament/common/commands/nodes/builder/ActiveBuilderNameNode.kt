package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NestedNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.suggestions.ClassStoredNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [BUILDER] - [ACTIVE] - [BUILDER_NAME]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [BUILDER]       ->
 *      literal     [ACTIVE]        ->
 *      argument    [BUILDER_NAME] , StringType ->
 *      _
 */
object ActiveBuilderNameNode : NestedNode() {

    override val executionNode get() = ExecutionNode {
        CommandUtil.displayNoArgument(
            player = it.source.player,
            nodeKey = "$TOURNAMENT $BUILDER $ACTIVE $BUILDER_NAME",
            )
    }

    private val suggestionProvider by lazy {
        ClassStoredNameSuggestionProvider(
            storeClass = TournamentBuilderStore::class.java,
            getActive = true,
            )
    }

    override fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>?,
        argument: RequiredArgumentBuilder<CommandSourceStack, *>?,
        execution: ExecutionNode?,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return ActiveBuilderNode
            .nest(Commands
                .argument(BUILDER_NAME, StringArgumentType.string())
                .suggests(suggestionProvider)
                .executes((execution ?: this.executionNode).node)
                .then((literal ?: argument))
            )
    }
}
