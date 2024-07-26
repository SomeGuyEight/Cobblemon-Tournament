package com.cobblemontournament.common.commands.nodes.tournament

//import com.cobblemontournament.common.commands.LiteralBuilder
//import com.cobblemontournament.common.commands.RequiredBuilder
//import com.cobblemontournament.common.commands.nodes.*
//import com.cobblemontournament.common.util.CommandUtil
//import com.mojang.brigadier.builder.LiteralArgumentBuilder
//import com.mojang.brigadier.builder.RequiredArgumentBuilder
//import net.minecraft.commands.CommandSourceStack
//import net.minecraft.commands.Commands
//
///**
// * [TOURNAMENT] - [TOURNAMENT] - [HISTORY_KEY] - [TOURNAMENT_NAME] - [INFO]
// *
// *      literal     [TOURNAMENT]    ->
// *      literal     [TOURNAMENT]    ->
// *      literal     [HISTORY]       ->
// *      argument    [TOURNAMENT_NAME] , StringType ->
// *      literal     [INFO]          ->
// *      _
// */
//object TournamentHistoryInfoNode : LiteralNestedNode(INFO, TournamentHistoryNameNode) {
//
//    override val nodeKey: String =
//
//    override fun inner(
//        literal: LiteralBuilder?,
//        required: RequiredBuilder?,
//        execution: ExecutionNode?,
//    ): LiteralBuilder {
//        return
//            .nest(Commands
//                .literal(INFO)
//                .executes((execution?: this.executionNode).node)
//                .then(literal?: required)
//            )
//    }
//}
