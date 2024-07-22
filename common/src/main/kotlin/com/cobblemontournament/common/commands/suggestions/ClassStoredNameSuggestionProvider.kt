package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.api.TournamentStoreManager
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.position.StorePosition
import com.someguy.storage.store.Store
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack
import java.util.UUID

/**
 * @param storeClass The specific [Store] type to target in the [TournamentStoreManager]
 *
 * @param predicate Will be applied to each instance as [C].
 * The instance as [C] name will be included if [predicate] == `true`.
 * The predicate always returns `true` if it is not specified.
 *
 * @param playerPredicate Takes precedence over [predicate].
 * If [playerPredicate] != null && [CommandContext] source includes a non-null player UUID
 * the nexted predicate inside of [playerPredicate] will be obtained with the player UUID & used.
 */
class ClassStoredNameSuggestionProvider <P : StorePosition, C : ClassStored, ST : Store<P, C>> (
    private val storeClass: Class<out ST>,
    private val getActive: Boolean = true,
    private val playerPredicate: ((UUID) -> (C) -> Boolean)? = null,
    private val predicate: (C) -> Boolean = { true },
): SuggestionProvider<CommandSourceStack> {

    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {

        val finalPredicate = context.source.player?.uuid?.let { playerID ->
            playerPredicate?.invoke(playerID)
        } ?: predicate

        val storeKey = if (getActive) {
            TournamentStoreManager.ACTIVE_STORE_ID
        } else {
            TournamentStoreManager.INACTIVE_STORE_ID
        }

        TournamentStoreManager.getInstanceNames(
            storeClass = storeClass,
            storeID = storeKey,
            predicate = finalPredicate,
        ).forEach { name ->
            builder.suggest(name)
        }

        return builder.buildFuture()
    }

}
