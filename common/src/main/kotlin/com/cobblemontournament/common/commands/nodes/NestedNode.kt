package com.cobblemontournament.common.commands.nodes

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack

abstract class NestedNode {

    abstract val executionNode: ExecutionNode

    fun nest(
        literal: LiteralArgumentBuilder<CommandSourceStack>,
        execution: ExecutionNode? = null,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner(
            literal = literal,
            argument = null,
            execution = execution,
            )
    }

    fun nest(
        argument: RequiredArgumentBuilder<CommandSourceStack, *>,
        execution: ExecutionNode? = null,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner(
            literal = null,
            argument = argument,
            execution = execution,
            )
    }

    protected abstract fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>? = null,
        argument: RequiredArgumentBuilder<CommandSourceStack, *>? = null,
        execution: ExecutionNode? = null,
    ): LiteralArgumentBuilder<CommandSourceStack>

}
