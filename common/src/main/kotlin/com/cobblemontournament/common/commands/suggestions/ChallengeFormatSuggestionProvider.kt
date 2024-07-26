package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandSourceStack
import java.util.concurrent.CompletableFuture

object ChallengeFormatSuggestionProvider : SuggestionProvider<CommandSourceStack> {

    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        for (constant in ChallengeFormat.entries) {
            builder.suggest(constant.name)
        }
        return builder.buildFuture()
    }

}
