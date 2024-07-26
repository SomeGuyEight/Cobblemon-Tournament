package com.cobblemontournament.common.commands.nodes.tournament
//
//import com.cobblemontournament.common.commands.nodes.*
//import com.cobblemontournament.common.util.CommandUtil
//import com.mojang.brigadier.builder.LiteralArgumentBuilder
//import com.mojang.brigadier.builder.RequiredArgumentBuilder
//import com.sun.tools.javac.parser.Tokens
//import net.minecraft.commands.CommandSourceStack
//import net.minecraft.commands.Commands
// {
//
//    override val executionNode get() = ExecutionNode {
//        CommandUtil.displayNoArgument(
//            player = it.source.player,
//            nodeKey = "$TOURNAMENT $TOURNAMENT $HISTORY_KEY",
//        )
//    }
//
//    override fun inner(
//        literal: LiteralArgumentBuilder<CommandSourceStack>?,
//        required: RequiredArgumentBuilder<CommandSourceStack, *>?,
//        execution: ExecutionNode?,
//    ): LiteralArgumentBuilder<CommandSourceStack> {
//        return TournamentSubNode
//            .nest(Commands
//                .literal(HISTORY_KEY)
//                .executes((execution?: this.executionNode).node)
//                .then(literal?: required)
//            )
//    }
//
//}
