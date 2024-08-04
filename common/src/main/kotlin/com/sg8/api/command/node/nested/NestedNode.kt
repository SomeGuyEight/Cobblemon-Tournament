package com.sg8.api.command.node.nested

import com.sg8.api.command.LiteralArgumentBuilder
import com.sg8.api.command.node.ExecutionNode
import com.mojang.brigadier.builder.ArgumentBuilder
import com.sg8.util.displayNoArgument
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
