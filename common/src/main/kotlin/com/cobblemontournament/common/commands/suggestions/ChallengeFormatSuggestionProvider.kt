package com.cobblemontournament.common.commands.suggestions

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat
import net.minecraft.commands.CommandSourceStack
import java.util.concurrent.CompletableFuture

class ChallengeFormatSuggestionProvider: SuggestionProvider<CommandSourceStack>
{
    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions>
    {
        for (instance in ChallengeFormat.entries) {
            builder.suggest(instance.name)
        }
        return builder.buildFuture()
    }
}
