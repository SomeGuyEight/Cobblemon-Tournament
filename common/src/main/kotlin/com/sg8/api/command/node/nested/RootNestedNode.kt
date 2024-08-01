package com.sg8.api.command.node.nested

import com.sg8.api.command.LiteralArgumentBuilder
import com.sg8.api.command.node.ExecutionNode
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [nodeKey] will be a [LiteralArgumentBuilder] to enter a command tree
 */
open class RootNestedNode(nodeKey: String) : NestedNode(nodeKey) {

    override fun tryGetParentNode(): NestedNode? = null

    override fun nest(
        builder: ArgumentBuilder<CommandSourceStack, *>,
        execution: ExecutionNode?
    ): LiteralArgumentBuilder {
        return Commands
            .literal(nodeKey)
            .executes((execution ?: this.executionNode).handler)
            .then(builder)
    }

}
