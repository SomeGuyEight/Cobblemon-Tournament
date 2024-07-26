package com.cobblemontournament.common.commands.testing

import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.generator.indexedseed.IndexedSeedGenerator
import com.cobblemontournament.common.generator.indexedseed.SortType
import com.cobblemontournament.common.util.displayInChat
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
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

    private fun pong(ctx: CommandContext): Int {
        val player = ctx.source.player
        player?.displayInChat(text = "Pong.")
        return Command.SINGLE_SUCCESS
    }

    private fun sum(ctx: CommandContext, a: Int, b: Int): Int {
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
                player.displayInChat(text = string)
            }
            player.displayInChat(text = "Sum (a + b) = " + (a + b))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun runSeedGen(ctx: CommandContext, seedCount: Int): Int {
        val indexedSeeds = IndexedSeedGenerator.getIndexedSeedArray(
            seedCount = seedCount,
            sortType = SortType.INDEX_ASCENDING,
        )
        indexedSeeds.print()
        ctx.source.player?.displayInChat(text = "Ran & printed Indexed Seed Generation")
        return Command.SINGLE_SUCCESS
    }

}
