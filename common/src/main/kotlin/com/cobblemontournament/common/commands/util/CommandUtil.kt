package com.cobblemontournament.common.commands.util

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.ParsedCommandNode
import org.slf4j.helpers.Util

object CommandUtil
{
    @JvmStatic
    fun logNoArgument(name: String,target: String): Int
    {
        Util.report("$target \"$name\" command had no arguments.")
        return Command.SINGLE_SUCCESS
    }

    @JvmStatic
    fun getNodeEntries(
        parsedNodes: List<ParsedCommandNode<*>>,
        input: String
    ): List<NodeEntry>
    {
        val list = mutableListOf<NodeEntry>()
        for (parsedNode in parsedNodes) {
            val range = parsedNode.range
            val value = input.subSequence(range.start, range.end)
            list.add(NodeEntry(parsedNode.node.name,value.toString()))
        }
        return list
    }

}
