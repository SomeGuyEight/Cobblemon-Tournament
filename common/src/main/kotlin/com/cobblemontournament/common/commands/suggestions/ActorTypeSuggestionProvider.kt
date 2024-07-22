package com.cobblemontournament.common.commands.suggestions

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack

class ActorTypeSuggestionProvider : SuggestionProvider <CommandSourceStack> {

    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        for (instance in ActorType.entries) {
            builder.suggest(instance.name)
        }
        return builder.buildFuture()
    }

}
