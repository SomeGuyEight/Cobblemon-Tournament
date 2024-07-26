package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.getNodeInputRange
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
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
                    tournamentBuilder.getPlayersNames().forEach { name -> builder.suggest(name) }
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
