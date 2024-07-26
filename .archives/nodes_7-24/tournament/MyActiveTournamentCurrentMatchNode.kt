package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.util.CommandUtil
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
//
///**
// * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] - [TOURNAMENT_NAME] - [CURRENT_MATCH]
// *
// *      literal     [TOURNAMENT]        ->
// *      literal     [TOURNAMENT]        ->
// *      literal     [MY_ACTIVE]         ->
// *      argument    [TOURNAMENT_NAME] , StringType ->
// *      literal     [CURRENT_MATCH]     ->
// *      _
// */
//object MyActiveTournamentCurrentMatchNode : NestedNode() {
//
//    override val executionNode get() = ExecutionNode {
//        CommandUtil.displayNoArgument(
//            player  = it.source.player,
//            nodeKey = "$TOURNAMENT $TOURNAMENT $MY_ACTIVE $TOURNAMENT_NAME $CURRENT_MATCH",
//        )
//    }
//
//    override fun inner(
//        literal: LiteralArgumentBuilder<CommandSourceStack>?,
//        required: RequiredArgumentBuilder<CommandSourceStack, *>?,
//        execution: ExecutionNode?,
//    ): LiteralArgumentBuilder<CommandSourceStack> {
//        return MyActiveTournamentNameNode
//            .nest(Commands
//                .literal(CURRENT_MATCH)
//                .executes((execution ?: this.executionNode).node)
//                .then((literal ?: required))
//            )
//    }
//}
