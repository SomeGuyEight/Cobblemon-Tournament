package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.tournament.Tournament
import com.someguy.storage.position.UuidPosition

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
) : ClassStoredNameSuggestionProvider<UuidPosition, Tournament, TournamentStore>(
    storeClass = TournamentStore::class.java,
    getActive = getActive,
    predicate = predicate,
)
