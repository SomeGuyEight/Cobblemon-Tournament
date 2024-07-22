package com.cobblemontournament.common.commands.testing

import com.cobblemontournament.common.generator.indexedseed.IndexedSeedGenerator
import com.cobblemontournament.common.util.ChatUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.someguy.collections.SortType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object TestCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(Commands
            .literal("tournament")
                .then(Commands
                    .literal("test")
                    .then(Commands
                        .literal("ping")
                            .executes { ctx -> pong( ctx) }
                    )))

        dispatcher.register(Commands
            .literal("tournament")
                .then(Commands
                    .literal("test")
                    .then(Commands
                        .literal("sum")
                        .then(Commands
                            .argument("a", IntegerArgumentType.integer(0, 100))
                            .then(Commands
                                .argument("b", IntegerArgumentType.integer(0, 100))
                                .executes { ctx ->
                                    sum(
                                        ctx = ctx,
                                        a = IntegerArgumentType.getInteger(ctx, "a"),
                                        b = IntegerArgumentType.getInteger(ctx, "b"))
                                })))))

        dispatcher.register(Commands
            .literal("tournament")
                .then(Commands
                    .literal("test")
                    .then(Commands
                        .literal("run-seed-gen")
                        .then(Commands
                            .argument("size", IntegerArgumentType.integer())
                            .executes { ctx ->
                                runSeedGen( ctx, IntegerArgumentType.getInteger(ctx, "size"))
                            }
                        ))))
    }

    private fun pong(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.source.player
        if (player != null) {
            ChatUtil.displayInPlayerChat(player = player, text = "Pong.")
        }
        return Command.SINGLE_SUCCESS
    }

    private fun sum(ctx: CommandContext<CommandSourceStack>, a: Int, b: Int): Int {
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
                ChatUtil.displayInPlayerChat(player = player, text = string)
            }
            ChatUtil.displayInPlayerChat(player = player, text = "Sum (a + b) = " + (a + b))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun runSeedGen(ctx: CommandContext<CommandSourceStack>, seedCount: Int): Int {
        val indexedSeeds = IndexedSeedGenerator.getIndexedSeedArray(
            seedCount = seedCount,
            currentSortType = SortType.INDEX_ASCENDING,
            )
        indexedSeeds.print()
        val player = ctx.source.player
        if (player != null) {
            ChatUtil.displayInPlayerChat(player = player, text = "Ran & printed Indexed Seed Generation")
        }
        return Command.SINGLE_SUCCESS
    }

}
