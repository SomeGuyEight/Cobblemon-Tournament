package com.cobblemontournament.common.commands.suggestions

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.*
import net.minecraft.commands.CommandSourceStack
import java.util.concurrent.CompletableFuture

object ActorTypeSuggestionProvider : SuggestionProvider<CommandSourceStack> {

    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        for (constant in ActorType.entries) {
            builder.suggest(constant.name)
        }
        return builder.buildFuture()
    }

}
