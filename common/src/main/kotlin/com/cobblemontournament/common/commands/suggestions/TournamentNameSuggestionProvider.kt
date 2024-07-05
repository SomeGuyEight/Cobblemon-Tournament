package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.TournamentManager
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack
import java.util.UUID

class TournamentNameSuggestionProvider (
    val storeID: UUID
) : SuggestionProvider<CommandSourceStack>
{
    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions>
    {
        val store = TournamentManager.getTournamentStore()?: return builder.buildFuture()
        for (instance in store.iterator()) {
            builder.suggest(instance.name)
        }
        return builder.buildFuture()
    }
}
