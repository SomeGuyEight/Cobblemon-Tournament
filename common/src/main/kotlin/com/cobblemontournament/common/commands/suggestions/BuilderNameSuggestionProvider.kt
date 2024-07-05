package com.cobblemontournament.common.commands.suggestions

import com.cobblemontournament.common.TournamentManager
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.position.StorePosition
import com.someguy.storage.store.Store
import java.util.concurrent.CompletableFuture
import net.minecraft.commands.CommandSourceStack
import java.util.*

class BuilderNameSuggestionProvider <P: StorePosition,C: ClassStored,St: Store<P,C>> (
    val storeClass: Class<out St>,
    val key: UUID
) : SuggestionProvider<CommandSourceStack>
{
    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions>
    {
        val store = TournamentManager.getTournamentBuilderStore()?: return builder.buildFuture()
        for (instance in store.iterator()) {
            //val formatedName = instance.name.filterNot { it == ' ' }
            builder.suggest(instance.name)
        }
        return builder.buildFuture()
    }
}