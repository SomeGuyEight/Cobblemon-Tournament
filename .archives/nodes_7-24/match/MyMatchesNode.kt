package com.cobblemontournament.common.commands.nodes.match

//import com.cobblemontournament.common.commands.match.MyMatchesCommand
//import com.cobblemontournament.common.commands.nodes.*
//import com.cobblemontournament.common.commands.nodes.nested.NestedNode
//import com.mojang.brigadier.builder.LiteralArgumentBuilder
//import com.mojang.brigadier.builder.RequiredArgumentBuilder
//import net.minecraft.commands.CommandSourceStack
//import net.minecraft.commands.Commands
//
///**
// * [TOURNAMENT] - [MY_MATCHES]
// *
// *      literal     [TOURNAMENT]        ->
// *      literal     [MY_MATCHES]        ->
// *      _
// */
//object MyMatchesNode : NestedNode() {
//
//    override val executionNode get() = MyMatchesCommand.executionNode
//
//    override fun inner(
//        literal: LiteralArgumentBuilder<CommandSourceStack>?,
//        required: RequiredArgumentBuilder<CommandSourceStack, *>?,
//        execution: ExecutionNode?,
//    ): LiteralArgumentBuilder<CommandSourceStack> {
//        return TournamentRootNode
//            .nest(Commands
//                .literal(MY_MATCHES)
//                .executes((execution ?: this.executionNode).node)
//                .then((literal ?: required))
//            )
//    }
//
//}
