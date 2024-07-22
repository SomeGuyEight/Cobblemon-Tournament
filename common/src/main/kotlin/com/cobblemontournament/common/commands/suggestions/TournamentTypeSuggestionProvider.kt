package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.tournament.TournamentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack

class TournamentTypeSuggestionProvider : SuggestionProvider<CommandSourceStack> {

    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        for (instance in TournamentType.entries) {
            builder.suggest(instance.name)
        }
        return builder.buildFuture()
    }

}
