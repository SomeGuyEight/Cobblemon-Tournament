package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.sg8.api.command.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.sg8.storage.TypeStored
import com.sg8.storage.StorePosition
import com.sg8.storage.Store
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack

open class InstanceNameSuggestionProvider <P : StorePosition, T : TypeStored, S : Store<P, T>> (
    private val storeClass: Class<out S>,
    private val getActive: Boolean = true,
    private val predicate: (CommandContext) -> (T) -> Boolean = { { true } },
) : SuggestionProvider<CommandSourceStack> {

    override fun getSuggestions(
        context: CommandContext,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val storeKey = if (getActive) {
            TournamentStoreManager.ACTIVE_STORE_ID
        } else {
            TournamentStoreManager.INACTIVE_STORE_ID
        }

        TournamentStoreManager.getInstanceNames(
            storeClass = storeClass,
            storeID = storeKey,
            predicate = predicate(context),
        ).forEach { name ->
            builder.suggest(name)
        }

        return builder.buildFuture()
    }

}
