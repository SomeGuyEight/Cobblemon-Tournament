package com.sg8.api.command.node.nested

import com.sg8.api.command.LiteralArgumentBuilder
import com.sg8.api.command.node.ExecutionNode
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.*

/**
 * [nodeKey] will be a [LiteralArgumentBuilder] entry point.
 */
open class RootNestedNode(nodeKey: String) : NestedNode(nodeKey, null) {

    override fun nest(
        builder: ArgumentBuilder<CommandSourceStack, *>,
        execution: ExecutionNode?
    ): LiteralArgumentBuilder {
        return Commands
            .literal(nodeKey)
            .executes((execution ?: this.executionNode).handler)
            .then(builder)
    }

    override fun nest(
        newParent: NestedNode,
        builder: ArgumentBuilder<CommandSourceStack, *>,
        parentExecution: ExecutionNode,
        execution: ExecutionNode,
    ): LiteralArgumentBuilder {
        return newParent.nest(
            Commands
                .literal(nodeKey)
                .executes(execution.handler)
                .then(builder),
            execution = parentExecution,
        )
    }

    override fun getAsFinalNode(execution: ExecutionNode?): LiteralArgumentBuilder {
        return Commands
            .literal(nodeKey)
            .executes((execution ?: this.executionNode).handler)
    }

    override fun copyWithParent(newParent: NestedNode): LiteralNestedNode {
        return LiteralNestedNode(nodeKey, newParent)
    }
}
