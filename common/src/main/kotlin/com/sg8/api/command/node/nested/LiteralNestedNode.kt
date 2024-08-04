package com.sg8.api.command.node.nested

import com.mojang.brigadier.builder.ArgumentBuilder
import com.sg8.api.command.LiteralArgumentBuilder
import com.sg8.api.command.node.ExecutionNode
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands


open class LiteralNestedNode(
    nodeKey: String,
    private val parentNode: NestedNode,
) : NestedNode(nodeKey) {

    override fun tryGetParentNode(): NestedNode = parentNode

    override fun nest(
        builder: ArgumentBuilder<CommandSourceStack, *>,
        execution: ExecutionNode?,
    ): LiteralArgumentBuilder {
        return parentNode.nest(
            Commands
                .literal(nodeKey)
                .executes((execution ?: this.executionNode).handler)
                .then(builder)
        )
    }

}
