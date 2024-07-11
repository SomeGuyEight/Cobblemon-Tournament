package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.TournamentStoreManager
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack

class BuilderNameSuggestionProvider (
    private val restrictToPlayer: Boolean = false
): SuggestionProvider<CommandSourceStack>
{
    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions>
    {
        val names = if (restrictToPlayer) {
            TournamentStoreManager.getTournamentBuilderNames( playerID = context.source.player?.uuid )
        } else {
            TournamentStoreManager.getTournamentBuilderNames()
        }
        names.forEach { builder.suggest( it ) }
        return builder.buildFuture()
    }
}