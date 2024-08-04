package com.cobblemontournament.common.commands.suggestions

import com.sg8.api.command.CommandContext
import com.cobblemontournament.common.tournament.TournamentType
import com.mojang.brigadier.suggestion.*
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
