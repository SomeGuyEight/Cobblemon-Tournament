package com.cobblemontournament.common.commands.nodes

import com.cobblemontournament.common.commands.nodes.nested.LiteralNestedNode

/**
 * [TOURNAMENT]-[MY_MATCHES]
 */
object MyMatchesNode : LiteralNestedNode(MY_MATCHES, TournamentRootNode)
