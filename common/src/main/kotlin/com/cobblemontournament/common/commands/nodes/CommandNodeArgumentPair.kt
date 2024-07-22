package com.cobblemontournament.common.commands.nodes

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack

data class CommandNodeArgumentPair(
    val node: (LiteralArgumentBuilder<CommandSourceStack>?, RequiredArgumentBuilder<CommandSourceStack, *>?) -> LiteralArgumentBuilder<CommandSourceStack>,
    val literal: LiteralArgumentBuilder<CommandSourceStack>? = null,
    val argument: RequiredArgumentBuilder<CommandSourceStack, *>? = null
) {

    fun isValid() = !( literal == null && argument == null )

    companion object {
        fun nestAllNodes(
            nodes: Collection<CommandNodeArgumentPair>,
        ) : LiteralArgumentBuilder<CommandSourceStack> {
            var builder: LiteralArgumentBuilder<CommandSourceStack>? = null
            nodes.forEach { builder = nestNode(it) }
            return builder ?: throw Exception("argument builder cannot be null")
        }

        private fun nestNode(value: CommandNodeArgumentPair) = value.node(value.literal, value.argument)
    }

}
