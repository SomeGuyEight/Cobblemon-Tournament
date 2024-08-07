package com.sg8.api.command.node.nested

import com.mojang.brigadier.arguments.ArgumentType
import com.sg8.api.command.LiteralArgumentBuilder
import com.sg8.api.command.node.ExecutionNode
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

open class RequiredNestedNode<T : ArgumentType<*>>(
    nodeKey: String,
    parentNode: NestedNode,
    val argumentType: T,
    val suggestionProvider: SuggestionProvider<CommandSourceStack>? = null,
) : NestedNode(nodeKey, parentNode) {

    override fun nest(
        builder: ArgumentBuilder<CommandSourceStack, *>,
        execution: ExecutionNode?,
    ): LiteralArgumentBuilder {
        return if (suggestionProvider != null) {
            parentNode!!.nest(
                Commands
                    .argument(nodeKey, argumentType)
                    .suggests(suggestionProvider)
                    .executes((execution ?: this.executionNode).handler)
                    .then(builder)
            )
        } else {
            parentNode!!.nest(
                Commands
                    .argument(nodeKey, argumentType)
                    .executes((execution ?: this.executionNode).handler)
                    .then(builder)
            )
        }
    }

    override fun nest(
        newParent: NestedNode,
        builder: ArgumentBuilder<CommandSourceStack, *>,
        parentExecution: ExecutionNode,
        execution: ExecutionNode,
    ): LiteralArgumentBuilder {
        return newParent.nest(
            Commands
                .argument(nodeKey, argumentType)
                .suggests(suggestionProvider)
                .executes(execution.handler)
                .then(builder),
            execution = parentExecution,
        )
    }

    override fun getAsFinalNode(execution: ExecutionNode?): LiteralArgumentBuilder {
        return if (suggestionProvider != null) {
            parentNode!!.nest(
                Commands
                    .argument(nodeKey, argumentType)
                    .suggests(suggestionProvider)
                    .executes((execution ?: this.executionNode).handler)
            )
        } else {
            parentNode!!.nest(
                Commands
                    .argument(nodeKey, argumentType)
                    .executes((execution ?: this.executionNode).handler)
            )
        }
    }

    override fun copyWithParent(newParent: NestedNode): RequiredNestedNode<T> {
        return RequiredNestedNode(nodeKey, newParent, argumentType, suggestionProvider)
    }
}
