package com.cobblemontournament.common.commands.nodes.builder

//import com.cobblemontournament.common.api.storage.TournamentBuilderStore
//import com.cobblemontournament.common.commands.nodes.*
//import com.cobblemontournament.common.commands.nodes.nested.NestedNode
//import com.cobblemontournament.common.commands.suggestions.ClassStoredNameSuggestionProvider
//import com.cobblemontournament.common.util.CommandUtil
//import com.mojang.brigadier.arguments.StringArgumentType
//import com.mojang.brigadier.builder.LiteralArgumentBuilder
//import com.mojang.brigadier.builder.RequiredArgumentBuilder
//import net.minecraft.commands.CommandSourceStack
//import net.minecraft.commands.Commands
//
///**
// * [TOURNAMENT] - [BUILDER] - [HISTORY_KEY] - [BUILDER_NAME]
// *
// *      literal     [TOURNAMENT]    ->
// *      literal     [BUILDER]       ->
// *      literal     [HISTORY]       ->
// *      argument    [BUILDER_NAME] , StringType ->
// *      _
// */
//object BuilderHistoryNameNode : NestedNode() {
//
//    override val executionNode get() = ExecutionNode {
//        CommandUtil.displayNoArgument(
//            player = it.source.player,
//            nodeKey = "$TOURNAMENT $BUILDER $HISTORY_KEY $BUILDER_NAME",
//        )
//    }
//
//    private val suggestionProvider by lazy {
//        ClassStoredNameSuggestionProvider(
//            storeClass = TournamentBuilderStore::class.java,
//            getActive = false,
//        )
//    }
//
//    override fun inner(
//        literal: LiteralArgumentBuilder<CommandSourceStack>?,
//        required: RequiredArgumentBuilder<CommandSourceStack, *>?,
//        execution: ExecutionNode?,
//    ): LiteralArgumentBuilder<CommandSourceStack> {
//        return BuilderHistoryNode
//            .nest(Commands
//                .argument(BUILDER_NAME, StringArgumentType.string())
//                .suggests(suggestionProvider)
//                .executes((execution ?: this.executionNode).node)
//                .then((literal ?: required))
//            )
//    }
//
//}
