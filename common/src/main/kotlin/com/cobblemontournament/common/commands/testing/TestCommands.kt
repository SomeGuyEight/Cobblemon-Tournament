package com.cobblemontournament.common.commands.testing

import com.cobblemontournament.common.generator.indexedseed.IndexedSeedGenerator
import com.cobblemontournament.common.util.ChatUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.someguy.collections.SortType
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal

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
                            .executes { ctx -> pong( ctx) }
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
                                runSeedGen( ctx, IntegerArgumentType.getInteger(ctx, "size"))
                            }
                        ))))
    }

    private fun pong(
        ctx: CommandContext<CommandSourceStack>
    ): Int {
        val player = ctx.source.player
        if (player != null) {
            ChatUtil.displayInPlayerChat( player, text = "Pong.")
        }
        return Command.SINGLE_SUCCESS
    }

    private fun sum(
        ctx: CommandContext<CommandSourceStack>,
        a: Int,
        b: Int
    ): Int
    {
        val nodes = ctx.nodes
        val strings = mutableListOf<String>()
        for (parsed in nodes) {
            val name = parsed.node.name
            val range = parsed.range
            strings.add("node -> name == $name & range == $range")
            val argument = ctx.input.subSequence(range.start, range.end)
            strings.add("node -> name == $name & argument == $argument")
        }
        val player = ctx.source.player
        if (player != null) {
            for (string in strings) {
                ChatUtil.displayInPlayerChat( player, text = string)
            }
            ChatUtil.displayInPlayerChat( player, text = "Sum (a + b) = " + (a + b))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun runSeedGen(
        ctx: CommandContext<CommandSourceStack>,
        seeds: Int
    ): Int
    {
        val indexedSeeds = IndexedSeedGenerator.getIndexedSeedArray(seeds, SortType.INDEX_ASCENDING)
        indexedSeeds.print()
        val player = ctx.source.player
        if (player != null) {
            ChatUtil.displayInPlayerChat( player, text = "Ran & printed Indexed Seed Generation")
        }
        return Command.SINGLE_SUCCESS
    }

}
