package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.TournamentStoreManager
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
import java.util.*

/**
 * This returns suggestions for players based on the name give in the
 * keys of either [BUILDER_NAME] or [TOURNAMENT_NAME] in [CommandContext].
 *
 * All names will be suggested if a valid [TournamentBuilder] or [Tournament] is found.
 */
class PlayerNameSuggestionProvider(
    val instanceID: UUID? = null
): SuggestionProvider<CommandSourceStack>
{
    override fun getSuggestions(
        ctx: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions>
    {

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input)
        var entry = nodeEntries.firstOrNull { it.key == BUILDER_NAME }
        if (entry == null) {
           entry = nodeEntries.firstOrNull { it.key == TOURNAMENT_NAME }?: return builder.buildFuture()
        }
        if (entry.key == BUILDER_NAME) {
            val (tournamentBuilder,_) = TournamentStoreManager.getTournamentBuilderByName(entry.value)
            tournamentBuilder?.getPlayerNames()?.forEach { builder.suggest( it ) }
        } else if (entry.key == TOURNAMENT_NAME) {
            val (tournament,_) = TournamentStoreManager.getTournamentByName(entry.value)
            tournament?.getPlayerNames()?.forEach { builder.suggest( it.name ) }
        }
        return builder.buildFuture()
    }
}
