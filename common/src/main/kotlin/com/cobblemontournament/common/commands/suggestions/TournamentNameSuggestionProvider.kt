package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.TournamentStoreManager
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack

class TournamentNameSuggestionProvider(
    private val restrictToPlayer    : Boolean = false,
    private val getActive           : Boolean = true,
): SuggestionProvider<CommandSourceStack>
{
    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions>
    {
        val storeKey = if ( getActive ) {
            TournamentStoreManager.activeStoreKey
        } else TournamentStoreManager.inactiveStoreKey

        val names = if (restrictToPlayer) {
            TournamentStoreManager.getTournamentNames(
                storeID     = storeKey,
                playerID    = context.source.player?.uuid )
        } else {
            TournamentStoreManager.getTournamentNames( storeID = storeKey)
        }
        names.forEach { builder.suggest( it ) }
        return builder.buildFuture()
    }
}
