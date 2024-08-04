package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.storage.store.TournamentBuilderStore
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.sg8.api.command.CommandContext
import com.sg8.storage.UuidPosition

private val containsPlayerIDPredicate: (CommandContext) -> (TournamentBuilder) -> Boolean = {
    ctx: CommandContext
    ->
    ctx.source.player?.uuid?.let { playerID ->
        { tournament: TournamentBuilder ->
            playerID.let { tournament.containsPlayer(it) }
        }
    } ?: { false }
}

class BuilderNameSuggestionProvider (
    getActive: Boolean = true,
    predicate: (CommandContext) -> (TournamentBuilder) -> Boolean =  containsPlayerIDPredicate,
) : InstanceNameSuggestionProvider<UuidPosition, TournamentBuilder, TournamentBuilderStore>(
    storeClass = TournamentBuilderStore::class.java,
    getActive = getActive,
    predicate = predicate
)
