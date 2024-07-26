package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.util.*
import com.someguy.storage.Properties
import com.someguy.storage.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class PlayerProperties(
    name: String = DEFAULT_PLAYER_NAME,
    actorType: ActorType = DEFAULT_ACTOR_TYPE,
    playerID: PlayerID = UUID.randomUUID(),
    tournamentID: TournamentID = UUID.randomUUID(),
    seed: Int = DEFAULT_SEED,
    originalSeed: Int = DEFAULT_SEED,
    pokemonTeamID: UUID? = DEFAULT_POKEMON_TEAM_ID,
    currentMatchID: MatchID? = DEFAULT_CURRENT_MATCH_ID,
    finalPlacement: Int = DEFAULT_FINAL_PLACEMENT,
    pokemonFinal: Boolean = DEFAULT_POKEMON_FINAL,
    lockPokemonOnSet: Boolean = DEFAULT_LOCK_POKEMON_ON_SET,
) : Properties<PlayerProperties>, Comparable<PlayerProperties> {

    override val instance: PlayerProperties = this
    override val helper = PlayerPropertiesHelper

    private val anyChangeObservable = SimpleObservable<PlayerProperties>()
    private val subscriptionsMap: SubscriptionMap = mutableMapOf()

    private val nameObservable = registerObservable(SettableObservable(name))
    private val playerIDObservable = registerObservable(SettableObservable(playerID))
    private val actorTypeObservable = registerObservable(SettableObservable(actorType))
    private val tournamentIDObservable = registerObservable(SettableObservable(tournamentID))
    private val seedObservable = registerObservable(SettableObservable(seed))
    private val originalSeedObservable = registerObservable(SettableObservable(originalSeed))
    private val pokemonTeamIDObservable = registerObservable(SettableObservable(pokemonTeamID))
    private val currentMatchIDObservable = registerObservable(SettableObservable(currentMatchID))
    private val finalPlacementObservable = registerObservable(SettableObservable(finalPlacement))
    private val pokemonFinalObservable = registerObservable(SettableObservable(pokemonFinal))
    private val lockPokemonOnSetObservable =
        registerObservable(SettableObservable(lockPokemonOnSet))

    var name: String
        get() = nameObservable.get()
        set(value) { nameObservable.set(value) }
    var playerID: PlayerID
        get() = playerIDObservable.get()
        set(value) { playerIDObservable.set(value) }
    var actorType: ActorType
        get() = actorTypeObservable.get()
        set(value) { actorTypeObservable.set(value) }
    var tournamentID: TournamentID
        get() = tournamentIDObservable.get()
        set(value) { tournamentIDObservable.set(value) }
    var seed: Int
        get() = seedObservable.get()
        set(value) { seedObservable.set(value) }
    var originalSeed: Int
        get() = originalSeedObservable.get()
        set(value) { originalSeedObservable.set(value) }
    var pokemonTeamID: UUID?
        get() = pokemonTeamIDObservable.get()
        set(value) { pokemonTeamIDObservable.set(value) }
    var currentMatchID: MatchID?
        get() = currentMatchIDObservable.get()
        set(value) { currentMatchIDObservable.set(value) }
    var finalPlacement: Int
        get() = finalPlacementObservable.get()
        set(value) { finalPlacementObservable.set(value) }
    var pokemonFinal: Boolean
        get() = pokemonFinalObservable.get()
        set(value) { pokemonFinalObservable.set(value) }
    var lockPokemonOnSet: Boolean
        get() = lockPokemonOnSetObservable.get()
        set(value) { lockPokemonOnSetObservable.set(value) }

    private fun <T, O : Observable<T>> registerObservable(observable: O): O {
        return observable.registerObservable(subscriptionsMap) { emitChange() }
    }

    private fun emitChange() = anyChangeObservable.emit((this))

    override fun getChangeObservable() = anyChangeObservable

    override fun compareTo(other: PlayerProperties): Int {
        return compareValuesBy(
            a = this,
            b = other,
            { it.name },
            { it.actorType },
            { it.playerID },
            { it.tournamentID },
            { it.seed },
            { it.originalSeed },
            { it.pokemonTeamID },
            { it.pokemonFinal },
            { it.lockPokemonOnSet },
            { it.currentMatchID },
            { it.finalPlacement },
        )
    }

    fun displayInChat(
        properties: PlayerProperties,
        player: ServerPlayer,
        padStart: Int = 0,
        displaySeed: Boolean = false,
        displayPokemon: Boolean = false,
        displayCurrentMatch: Boolean = false,
        displayPlacement: Boolean = false,
    ) {
        HELPER.displayInChatHelper(
            properties = properties,
            player = player,
            padStart = padStart,
            displaySeed = displaySeed,
            displayPokemon = displayPokemon,
            displayCurrentMatch = displayCurrentMatch,
            displayPlacement = displayPlacement,
        )
    }

    companion object {
        private val HELPER = PlayerPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbtHelper(nbt = nbt)
    }

}
