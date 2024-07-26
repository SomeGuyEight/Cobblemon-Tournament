package com.cobblemontournament.common.commands.nodes.nested

import com.cobblemontournament.common.commands.LiteralArgumentBuilder
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.util.displayNoArgument
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack

abstract class NestedNode (val nodeKey: String) {

    private val nodePath: String by lazy {
        tryGetParentNode()?.nodePath?.plus(" $nodeKey") ?: nodeKey
    }

    val executionNode: ExecutionNode by lazy {
        ExecutionNode { it.source.player.displayNoArgument(nodePath) }
    }

    abstract fun tryGetParentNode(): NestedNode?

    abstract fun nest(
        builder: ArgumentBuilder<CommandSourceStack, *>,
        execution: ExecutionNode? = null,
    ): LiteralArgumentBuilder

}
