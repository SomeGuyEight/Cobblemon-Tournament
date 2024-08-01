package com.cobblemontournament.common.commands.nodes

import com.sg8.api.command.node.nested.LiteralNestedNode
import com.sg8.api.command.node.nested.RequiredNestedNode
import com.cobblemontournament.common.commands.suggestions.TournamentNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType

/**
 * [TOURNAMENT]-[TOURNAMENT]
 */
object TournamentSubNode : LiteralNestedNode(TOURNAMENT, TournamentRootNode)

/**
 * [TOURNAMENT]-[TOURNAMENT]-[HISTORY]
 */
object TournamentHistoryNode : LiteralNestedNode(HISTORY, TournamentSubNode)

/**
 * [TOURNAMENT]-[TOURNAMENT]-[HISTORY]-[TOURNAMENT_NAME]
 */
object TournamentHistoryNameNode : RequiredNestedNode<StringArgumentType>(
    nodeKey = TOURNAMENT_NAME,
    parentNode = TournamentHistoryNode,
    argumentType = StringArgumentType.string(),
    suggestionProvider = TournamentNameSuggestionProvider(getActive = false)
)

/**
 * [TOURNAMENT]-[TOURNAMENT]-[HISTORY]-[TOURNAMENT_NAME]-[INFO]
 */
object TournamentHistoryInfoNode : LiteralNestedNode(INFO, TournamentHistoryNameNode)

/**
 * [TOURNAMENT]-[TOURNAMENT]-[ACTIVE]
 */
object ActiveTournamentNode : LiteralNestedNode(ACTIVE, TournamentSubNode)

/**
 * [TOURNAMENT]-[TOURNAMENT]-[ACTIVE]-[TOURNAMENT_NAME]
 */
object ActiveTournamentNameNode : RequiredNestedNode<StringArgumentType>(
    nodeKey = TOURNAMENT_NAME,
    parentNode = ActiveTournamentNode,
    argumentType = StringArgumentType.string(),
    suggestionProvider = TournamentNameSuggestionProvider(getActive = true),
)

/**
 * [TOURNAMENT]-[TOURNAMENT]-[ACTIVE]-[TOURNAMENT_NAME]-[INFO]
 */
object ActiveTournamentInfoNode : LiteralNestedNode(INFO, ActiveTournamentNameNode)
