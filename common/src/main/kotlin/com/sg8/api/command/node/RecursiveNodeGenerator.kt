package com.sg8.api.command.node

import com.mojang.brigadier.CommandDispatcher
import com.sg8.api.command.LiteralArgumentBuilder
import com.sg8.api.command.node.nested.*
import com.sg8.collections.allPermutations
import net.minecraft.commands.CommandSourceStack

object RecursiveNodeGenerator {

    fun registerAllPermutations(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        rootNode: NestedNode,
        executionNode: ExecutionNode,
        vararg nodeLists: List<NestedNode>,
    ) {
        registerAllPermutations(dispatcher, rootNode, executionNode, listOf(*nodeLists))
    }

    fun registerAllPermutations(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        rootNode: NestedNode,
        executionNode: ExecutionNode,
        nodeLists: List<List<NestedNode>>,
    ) {
        val allNodePermutations = nodeLists.allPermutations()
        allNodePermutations.forEach { nodeQueue ->
            if (nodeQueue.size > 0) {
                dispatcher.register(
                    nodeQueue.buildRecursive(
                        currentQueue = ArrayDeque(nodeQueue.removeFirst()),
                        node = rootNode,
                        executionNode = executionNode,
                    )
                )
            }
        }
    }

    fun ArrayDeque<List<NestedNode>>.buildRecursive(
        currentQueue: ArrayDeque<NestedNode>,
        node: NestedNode,
        executionNode: ExecutionNode
    ): LiteralArgumentBuilder {
        val nextNode = if (currentQueue.isNotEmpty()) {
            currentQueue.buildNodeRecursive(node, executionNode)
        } else {
            node
        }
        return if (this.isNotEmpty()) {
            this.buildRecursive(ArrayDeque(this.removeFirst()), nextNode, executionNode)
        } else {
            return nextNode.getAsFinalNode(executionNode)
        }
    }

    fun ArrayDeque<NestedNode>.buildNodeRecursive(
        node: NestedNode,
        executionNode: ExecutionNode
    ): NestedNode {
        return if (this.isNotEmpty()) {
            this.buildNodeRecursive(this.removeFirst().copyWithParent(node), executionNode)
        } else {
            node
        }
    }

    fun ArrayDeque<NestedNode>.buildRecursive(
        node: NestedNode,
        executionNode: ExecutionNode
    ): LiteralArgumentBuilder {
        return if (this.isNotEmpty()) {
            this.buildRecursive(this.removeFirst().copyWithParent(node), executionNode)
        } else {
            return node.getAsFinalNode(executionNode)
        }
    }
}