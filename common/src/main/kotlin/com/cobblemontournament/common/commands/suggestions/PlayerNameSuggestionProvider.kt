package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.commands.nodes.NodeEntry
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.CommandUtil
import com.mojang.brigadier.context.CommandContext
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
class PlayerNameSuggestionProvider : SuggestionProvider <CommandSourceStack> {

    override fun getSuggestions(
        ctx: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {

        val entry: NodeEntry = CommandUtil.getNodeEntries(ctx).run {
            this.firstOrNull { it.key == BUILDER_NAME }
                ?: this.firstOrNull { it.key == TOURNAMENT_NAME }
                ?: return builder.buildFuture()
        }

        if (entry.key == BUILDER_NAME) {
            val tournamentBuilder = TournamentStoreManager.getInstanceByName(
                storeClass = TournamentBuilderStore::class.java,
                name = entry.value,
                storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            ).first ?: return builder.buildFuture()
            tournamentBuilder.getPlayersNames().forEach { builder.suggest( it ) }
        } else if (entry.key == TOURNAMENT_NAME) {
            val tournament = TournamentStoreManager.getInstanceByName(
                storeClass = TournamentStore::class.java,
                name = entry.value,
                storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            ).first ?: return builder.buildFuture()
            tournament.getPlayerSet().forEach { builder.suggest(it.name) }
        }
        return builder.buildFuture()
    }

}
