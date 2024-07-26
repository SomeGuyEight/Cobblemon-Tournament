package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.tournament.TournamentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack

object TournamentTypeSuggestionProvider : SuggestionProvider<CommandSourceStack> {

    override fun getSuggestions(
        context: CommandContext,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        for (constant in TournamentType.entries) {
            builder.suggest(constant.name)
        }
        return builder.buildFuture()
    }

}
