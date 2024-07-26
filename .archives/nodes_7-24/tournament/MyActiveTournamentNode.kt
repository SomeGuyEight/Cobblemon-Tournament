package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.util.CommandUtil
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

///** [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] */
//object MyActiveTournamentNode : LiteralNestedNode(MY_ACTIVE, TournamentSubNode) {
//
//    override val executionNode get() = ExecutionNode {
//        CommandUtil.displayNoArgument(
//            player = it.source.player,
//            nodeKey = "$TOURNAMENT $TOURNAMENT $",
//        )
//    }
//
//    override fun inner(
//        literal: LiteralArgumentBuilder<CommandSourceStack>?,
//        required: RequiredArgumentBuilder<CommandSourceStack, *>?,
//        execution: ExecutionNode?,
//    ): LiteralArgumentBuilder<CommandSourceStack> {
//        return
//            .nest(Commands
//                .literal(MY_ACTIVE)
//                .executes((execution?: this.executionNode).node)
//                .then(literal?: required)
//            )
//    }
//
//}
