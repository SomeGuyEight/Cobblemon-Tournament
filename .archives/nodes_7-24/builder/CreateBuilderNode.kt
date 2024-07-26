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
// * [TOURNAMENT] - [BUILDER] - [CREATE]
// *
// *      literal     [TOURNAMENT]    ->
// *      literal     [BUILDER]       ->
// *      literal     [CREATE]        ->
// *      _
// */
//object CreateBuilderNode : NestedNode() {
//
//    override val executionNode get() = ExecutionNode {
//        CommandUtil.displayNoArgument(
//            player  = it.source.player,
//            nodeKey = "$TOURNAMENT $BUILDER $CREATE",
//        )
//    }
//
//    override fun inner(
//        literal: LiteralArgumentBuilder<CommandSourceStack>?,
//        required: RequiredArgumentBuilder<CommandSourceStack, *>?,
//        execution: ExecutionNode?,
//    ): LiteralArgumentBuilder<CommandSourceStack> {
//        return BuilderNode
//            .nest(Commands
//                .literal(CREATE)
//                .executes((execution ?: this.executionNode).node)
//                .then((literal ?: required))
//            )
//    }
//
//}
