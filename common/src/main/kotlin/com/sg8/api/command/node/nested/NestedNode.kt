package com.sg8.api.command.node.nested

import com.sg8.api.command.LiteralArgumentBuilder
import com.sg8.api.command.node.ExecutionNode
import com.mojang.brigadier.builder.ArgumentBuilder
import com.sg8.util.displayNoArgument
import net.minecraft.commands.CommandSourceStack

abstract class NestedNode (val nodeKey: String, val parentNode: NestedNode?) {

    private val nodePath: String = parentNode?.nodePath?.plus(" $nodeKey") ?: nodeKey

    val executionNode: ExecutionNode = ExecutionNode { it.source.player.displayNoArgument(nodePath) }

    abstract fun nest(
        builder: ArgumentBuilder<CommandSourceStack, *>,
        execution: ExecutionNode? = null,
    ): LiteralArgumentBuilder

    abstract fun nest(
        newParent: NestedNode,
        builder: ArgumentBuilder<CommandSourceStack, *>,
        parentExecution: ExecutionNode,
        execution: ExecutionNode,
    ): LiteralArgumentBuilder

    abstract fun getAsFinalNode(execution: ExecutionNode?): LiteralArgumentBuilder

    abstract fun copyWithParent(newParent: NestedNode): NestedNode
}
