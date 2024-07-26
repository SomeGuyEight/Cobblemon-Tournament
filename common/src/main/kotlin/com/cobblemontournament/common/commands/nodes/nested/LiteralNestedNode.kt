package com.cobblemontournament.common.commands.nodes.nested

import com.cobblemontournament.common.commands.LiteralArgumentBuilder
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.mojang.brigadier.builder.ArgumentBuilder
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
        return parentNode
            .nest(Commands
                .literal(nodeKey)
                .executes((execution ?: this.executionNode).action)
                .then(builder)
            )
    }

}
