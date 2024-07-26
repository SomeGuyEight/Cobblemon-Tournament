package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.commands.*
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.suggestions.ClassStoredNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT] - [TOURNAMENT] - [PUBLIC] - [TOURNAMENT_NAME]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [TOURNAMENT]    ->
 *      literal     [PUBLIC]        ->
 *      argument    [TOURNAMENT_NAME] , StringType ->
 *      _
 */
//object AllPublicTournamentNameNode : RequiredNestedNode<StringArgumentType>(
//    nodeKey = TOURNAMENT_NAME,
//    parentNode = AllPublicTournamentNode,
//    argumentType = StringArgumentType.string(),
//) {
//
//    override val suggestionProvider by lazy {
//        ClassStoredNameSuggestionProvider(
//            storeClass = TournamentStore::class.java,
//            getActive = true,
//        )
//    }
//
//    override fun inner(
//        literal: LiteralBuilder?,
//        required: RequiredBuilder?,
//        execution: ExecutionNode?
//    ): LiteralBuilder {
//        return AllPublicTournamentNode
//            .nest(Commands
//                .argument(TOURNAMENT_NAME, StringArgumentType.word())
//                // TODO add 'public' to to Tournament properties
//                .suggests(suggestionProvider)
//                .executes((execution?: this.executionNode).node)
//                .then((literal?: required))
//            )
//    }
//}
