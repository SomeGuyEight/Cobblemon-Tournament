package com.cobblemontournament.common.commands.testing

import com.cobblemontournament.common.generator.IndexedSeedGenerator
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.context.StringRange
import com.someguy.collections.SortType
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import org.slf4j.helpers.Util

object TestCommands
{
    @JvmStatic
    fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registry: CommandBuildContext,
        selection: Commands.CommandSelection
    )
    {
        dispatcher.register(
            literal("tournament")
                .then(literal("test")
                    .then(literal("ping")
                            .executes { pong() }
                    )))

        dispatcher.register(
            literal("tournament")
                .then(literal("test")
                    .then(literal("sum")
                        .then(argument("a", IntegerArgumentType.integer(0, 100))
                            .then(argument("b", IntegerArgumentType.integer(0, 100))
                                .executes { ctx ->
                                    sum(
                                        ctx = ctx,
                                        a   = IntegerArgumentType.getInteger(ctx, "a"),
                                        b   = IntegerArgumentType.getInteger(ctx, "b"))
                                })))))

        dispatcher.register(
            literal("tournament")
                .then(literal("test")
                    .then(literal("run-seed-gen")
                        .then(argument("size", IntegerArgumentType.integer())
                            .executes { ctx ->
                                runSeedGen(IntegerArgumentType.getInteger(ctx, "size"))
                            }
                        ))))
    }

    private fun pong(): Int {
        Util.report("Pong.")
        return Command.SINGLE_SUCCESS
    }

    private fun sum(
        ctx: CommandContext<CommandSourceStack>,
        a: Int,
        b: Int
    ): Int
    {
        val nodes = ctx.nodes
        for (parsed in nodes) {
            val name = parsed.node.name
            val range = parsed.range
            Util.report("node -> name == $name & range == $range")
            val argument = ctx.input.subSequence(range.start, range.end)
            Util.report("node -> name == $name & argument == $argument")
        }
        Util.report("Sum (a + b) = " + (a + b))
        return Command.SINGLE_SUCCESS
    }

    private fun runSeedGen(
        seeds: Int
    ): Int
    {
        val indexedSeeds = IndexedSeedGenerator.getIndexedSeedArray(seeds, SortType.INDEX_ASCENDING)
        indexedSeeds.print()
        return Command.SINGLE_SUCCESS
    }

    private fun log(
        name: String,
        range: StringRange
    ) {
        Util.report("node -> name == $name & range == $range")
    }

}
