package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.storage.store.TournamentStore
import com.sg8.api.command.CommandContext
import com.cobblemontournament.common.tournament.Tournament
import com.sg8.storage.UuidPosition

private val containsPlayerIDPredicate: (CommandContext) -> (Tournament) -> Boolean = {
    ctx: CommandContext
    ->
    ctx.source.player?.uuid?.let { playerID ->
        { tournament: Tournament ->
            playerID.let { tournament.containsPlayer(it) }
        }
    } ?: { false }
}

class TournamentNameSuggestionProvider (
    getActive: Boolean = true,
    predicate: (CommandContext) -> (Tournament) -> Boolean = containsPlayerIDPredicate,
) : InstanceNameSuggestionProvider<UuidPosition, Tournament, TournamentStore>(
    storeClass = TournamentStore::class.java,
    getActive = getActive,
    predicate = predicate,
)
