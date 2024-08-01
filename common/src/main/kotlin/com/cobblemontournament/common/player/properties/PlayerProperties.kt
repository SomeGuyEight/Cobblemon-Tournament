package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.*
import com.sg8.properties.DefaultProperties
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class PlayerProperties(
    name: String = DEFAULT_PLAYER_NAME,
    actorType: ActorType = DEFAULT_ACTOR_TYPE,
    uuid: UUID = UUID.randomUUID(),
    tournamentID: UUID = UUID.randomUUID(),
    seed: Int = DEFAULT_SEED,
    originalSeed: Int = DEFAULT_SEED,
    pokemonTeamID: UUID? = DEFAULT_POKEMON_TEAM_ID,
    currentMatchID: UUID? = DEFAULT_CURRENT_MATCH_ID,
    finalPlacement: Int = DEFAULT_FINAL_PLACEMENT,
    pokemonFinal: Boolean = DEFAULT_POKEMON_FINAL,
    lockPokemonOnSet: Boolean = DEFAULT_LOCK_POKEMON_ON_SET,
) : DefaultProperties<PlayerProperties>,
    Comparable<PlayerProperties> {
    //Observable<PlayerProperties> by SimpleObservable() { // maybe??

    override val instance: PlayerProperties = this
    override val helper = PlayerPropertiesHelper
    override val observable = SimpleObservable<PlayerProperties>()
    private val subscriptionsMap: MutableMap<Observable<*>, ObservableSubscription<*>> = mutableMapOf()

    private val _name = SettableObservable(name).subscribe()
    private val _uuid = SettableObservable(uuid).subscribe()
    private val _actorType = SettableObservable(actorType).subscribe()
    private val _tournamentID = SettableObservable(tournamentID).subscribe()
    private val _seed = SettableObservable(seed).subscribe()
    private val _originalSeed = SettableObservable(originalSeed).subscribe()
    private val _pokemonTeamID = SettableObservable(pokemonTeamID).subscribe()
    private val _currentMatchID = SettableObservable(currentMatchID).subscribe()
    private val _finalPlacement = SettableObservable(finalPlacement).subscribe()
    private val _pokemonFinal = SettableObservable(pokemonFinal).subscribe()
    private val _lockPokemonOnSet = SettableObservable(lockPokemonOnSet).subscribe()

    var name: String
        get() = _name.get()
        set(value) { _name.set(value) }
    var uuid: UUID
        get() = _uuid.get()
        set(value) { _uuid.set(value) }
    var actorType: ActorType
        get() = _actorType.get()
        set(value) { _actorType.set(value) }
    var tournamentID: UUID
        get() = _tournamentID.get()
        set(value) { _tournamentID.set(value) }
    var seed: Int
        get() = _seed.get()
        set(value) { _seed.set(value) }
    var originalSeed: Int
        get() = _originalSeed.get()
        set(value) { _originalSeed.set(value) }
    var pokemonTeamID: UUID?
        get() = _pokemonTeamID.get()
        set(value) { _pokemonTeamID.set(value) }
    var currentMatchID: UUID?
        get() = _currentMatchID.get()
        set(value) { _currentMatchID.set(value) }
    var finalPlacement: Int
        get() = _finalPlacement.get()
        set(value) { _finalPlacement.set(value) }
    var pokemonFinal: Boolean
        get() = _pokemonFinal.get()
        set(value) { _pokemonFinal.set(value) }
    var lockPokemonOnSet: Boolean
        get() = _lockPokemonOnSet.get()
        set(value) { _lockPokemonOnSet.set(value) }


    private fun <T, O : Observable<T>> replaceSubscription(old: O, new: O): O {
        old.unsubscribe()
        return new.subscribe()
    }

    private fun <T, O : Observable<T>> O.subscribe(): O {
        subscriptionsMap[this] = this.subscribe { emitChange() }
        return this
    }

    private fun <T, O : Observable<T>> O.unsubscribe(): O {
        subscriptionsMap[this]?.unsubscribe()
        return this
    }

    override fun emitChange() = observable.emit(this)

    override fun compareTo(other: PlayerProperties): Int {
        return compareValuesBy(
            a = this,
            b = other,
            { it.name },
            { it.uuid },
            { it.actorType },
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
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbt(nbt = nbt)
    }

}
