package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.api.storage.store.TournamentBuilderStore
import com.cobblemontournament.common.api.storage.store.TournamentStore
import com.sg8.api.command.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.sg8.api.command.getNodeInputRange
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack

/**
 * This returns suggestions for players based on the name give in the
 * keys of either [BUILDER_NAME] or [TOURNAMENT_NAME] in [CommandContext].
 *
 * All names will be suggested if a valid [TournamentBuilder] or [Tournament] is found.
 */
class PlayerNameSuggestionProvider : SuggestionProvider<CommandSourceStack> {

    override fun getSuggestions(
        ctx: CommandContext,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {

        ctx.getNodeInputRange(BUILDER_NAME)
            ?.let { builderName ->
                TournamentStoreManager.getInstanceByName(
                    storeClass = TournamentBuilderStore::class.java,
                    name = builderName,
                    storeID = TournamentStoreManager.ACTIVE_STORE_ID,
                )?.let { tournamentBuilder ->
                    tournamentBuilder.getPlayersNames().forEach { name ->
                        builder.suggest(name)
                    }
                } ?: return builder.buildFuture()

            }

        ctx.getNodeInputRange(TOURNAMENT_NAME)
            ?.let { tournamentName ->
                TournamentStoreManager.getInstanceByName(
                    storeClass = TournamentStore::class.java,
                    name = tournamentName,
                    storeID = TournamentStoreManager.ACTIVE_STORE_ID,
                )?.let { tournament ->
                    tournament.getPlayerSet().forEach { builder.suggest(it.name) }
                    return builder.buildFuture()
                }
            }

        return builder.buildFuture()
    }

}
