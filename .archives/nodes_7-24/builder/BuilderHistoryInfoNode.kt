package com.cobblemontournament.common.commands.nodes.builder
//
//import com.cobblemontournament.common.commands.nodes.*
//import com.cobblemontournament.common.commands.nodes.nested.NestedNode
//import com.cobblemontournament.common.util.CommandUtil
//import com.mojang.brigadier.builder.LiteralArgumentBuilder
//import com.mojang.brigadier.builder.RequiredArgumentBuilder
//import net.minecraft.commands.CommandSourceStack
//import net.minecraft.commands.Commands
//
///**
// * [TOURNAMENT] - [BUILDER] - [HISTORY_KEY] - [BUILDER_NAME] - [INFO]
// *
// *      literal     [TOURNAMENT]    ->
// *      literal     [BUILDER]       ->
// *      literal     [HISTORY]       ->
// *      argument    [BUILDER_NAME] , StringType ->
// *      literal     [INFO]       ->
// *      _
// */
//object BuilderHistoryInfoNode : NestedNode() {
//
//    override val executionNode get() = ExecutionNode {
//        CommandUtil.displayNoArgument(
//            player = it.source.player,
//            nodeKey = "$TOURNAMENT $BUILDER $HISTORY_KEY $BUILDER_NAME $INFO",
//            )
//    }
//
//    override fun inner(
//        literal: LiteralArgumentBuilder<CommandSourceStack>?,
//        required: RequiredArgumentBuilder<CommandSourceStack, *>?,
//        execution: ExecutionNode?,
//    ): LiteralArgumentBuilder<CommandSourceStack> {
//        return BuilderHistoryNameNode
//            .nest(Commands
//                .literal(INFO)
//                .executes((execution ?: this.executionNode).node)
//                .then((literal ?: required))
//            )
//    }
//
//}
