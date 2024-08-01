package com.cobblemontournament.common.commands.nodes

import com.sg8.api.command.node.nested.LiteralNestedNode
import com.sg8.api.command.node.nested.RequiredNestedNode
import com.cobblemontournament.common.commands.suggestions.BuilderNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType
import com.sg8.api.command.node.PLAYER

/**
 * [TOURNAMENT]-[BUILDER]
 */
object BuilderNode : LiteralNestedNode(BUILDER, TournamentRootNode)

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]
 */
object ActiveBuilderNode : LiteralNestedNode(ACTIVE, BuilderNode)

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]
 */
object ActiveBuilderNameNode : RequiredNestedNode<StringArgumentType>(
    nodeKey = BUILDER_NAME,
    parentNode = ActiveBuilderNode,
    argumentType = StringArgumentType.string(),
    suggestionProvider = BuilderNameSuggestionProvider(getActive = true) { { true } },
)

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]-[INFO]
 */
object ActiveBuilderInfoNode : LiteralNestedNode(INFO, ActiveBuilderNameNode)

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]-[PLAYER]
 */
object ActiveBuilderPlayerNode : LiteralNestedNode(PLAYER, ActiveBuilderNameNode)

/**
 * [TOURNAMENT]-[BUILDER]-[CREATE]
 */
object CreateBuilderNode : LiteralNestedNode(CREATE, BuilderNode)

/**
 * [TOURNAMENT]-[BUILDER]-[HISTORY]
 */
object BuilderHistoryNode : LiteralNestedNode(HISTORY, BuilderNode)

/**
 * [TOURNAMENT]-[BUILDER]-[HISTORY]-[BUILDER_NAME]
 */
object BuilderHistoryNameNode : RequiredNestedNode<StringArgumentType>(
    nodeKey = BUILDER_NAME,
    parentNode = BuilderHistoryNode,
    argumentType = StringArgumentType.string(),
    suggestionProvider = BuilderNameSuggestionProvider(getActive = false) { { true } },
)

/**
 * [TOURNAMENT]-[BUILDER]-[HISTORY]-[BUILDER_NAME]-[INFO]
 */
object BuilderHistoryInfoNode : LiteralNestedNode(INFO, BuilderHistoryNameNode)
