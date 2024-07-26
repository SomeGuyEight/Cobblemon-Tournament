package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.someguy.storage.position.UuidPosition

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
) : ClassStoredNameSuggestionProvider<UuidPosition, TournamentBuilder, TournamentBuilderStore>(
    storeClass = TournamentBuilderStore::class.java,
    getActive = getActive,
    predicate = predicate
)
