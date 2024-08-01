package com.cobblemontournament.common.commands.nodes

import com.sg8.api.command.node.nested.LiteralNestedNode

/**
 * [TOURNAMENT]-[MY_MATCHES]
 */
object MyMatchesNode : LiteralNestedNode(MY_MATCHES, TournamentRootNode)
