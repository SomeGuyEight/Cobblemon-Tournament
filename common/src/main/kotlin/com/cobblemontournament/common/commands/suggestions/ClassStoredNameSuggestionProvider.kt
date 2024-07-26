package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.commands.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.someguy.storage.ClassStored
import com.someguy.storage.StorePosition
import com.someguy.storage.Store
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack

open class ClassStoredNameSuggestionProvider <P : StorePosition, C : ClassStored, ST : Store<P, C>> (
    private val storeClass: Class<out ST>,
    private val getActive: Boolean = true,
    private val predicate: (CommandContext) -> (C) -> Boolean = { { true } },
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
            predicate = predicate.invoke(context),
        ).forEach { name ->
            builder.suggest(name)
        }

        return builder.buildFuture()
    }

}
