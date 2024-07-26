package com.cobblemontournament.common.commands.nodes.tournament

import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.suggestions.ClassStoredNameSuggestionProvider
import com.mojang.brigadier.arguments.StringArgumentType
//
///** [TOURNAMENT] - [TOURNAMENT] - [HISTORY_KEY] - [TOURNAMENT_NAME] */
//object TournamentHistoryNameNode : RequiredNestedNode<StringArgumentType>(
//    nodeKey = TOURNAMENT_NAME,
//    parentNode = TournamentHistoryNode,
//    argumentType = StringArgumentType.string(),
//    suggestionProvider = ClassStoredNameSuggestionProvider(
//        storeClass = TournamentStore::class.java,
//        getActive = false,
//    )
//)
