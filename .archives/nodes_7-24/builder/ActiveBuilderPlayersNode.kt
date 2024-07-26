package com.cobblemontournament.common.commands.nodes.builder

//import com.cobblemontournament.common.commands.nodes.*
//import com.cobblemontournament.common.commands.nodes.nested.NestedNode
//import com.cobblemontournament.common.util.CommandUtil
//import com.mojang.brigadier.builder.LiteralArgumentBuilder
//import com.mojang.brigadier.builder.RequiredArgumentBuilder
//import net.minecraft.commands.CommandSourceStack
//import net.minecraft.commands.Commands
//
///**
// * [TOURNAMENT] - [BUILDER] - [BUILDER_NAME] - [PLAYER]
// *
// *      literal     [TOURNAMENT]        ->
// *      literal     [BUILDER]           ->
// *      argument    [BUILDER_NAME] , StringType ->
// *      literal     [PLAYER]            ->
// *      _
// */
//object ActiveBuilderPlayersNode : NestedNode() {
//
//    override val executionNode = ExecutionNode {
//        CommandUtil.displayNoArgument(
//            player  = it.source.player,
//            nodeKey = "$TOURNAMENT $BUILDER $BUILDER_NAME $PLAYER",
//            )
//    }
//
//    override fun inner(
//        literal: LiteralArgumentBuilder<CommandSourceStack>?,
//        required: RequiredArgumentBuilder<CommandSourceStack, *>?,
//        execution: ExecutionNode?,
//    ): LiteralArgumentBuilder<CommandSourceStack> {
//        return ActiveBuilderNameNode
//            .nest(Commands
//                .literal(PLAYER)
//                .executes((execution ?: this.executionNode).node)
//                .then((literal ?: required))
//            )
//    }
//
//}
