package com.cobblemontournament.common.player

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.player.properties.MutablePlayerProperties
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.someguy.storage.classstored.ClassStored
import com.cobblemontournament.common.util.TournamentDataKeys
import com.someguy.storage.classstored.extension.ClassStoredExtension.defaultStoreCoords
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.util.UUID

// Important: (UUID) constructor is needed for serialization method
open class TournamentPlayer(uuid: UUID) : ClassStored
{
    constructor() : this (UUID.randomUUID())

    constructor (
        properties: PlayerProperties
    ) : this (properties.playerID) {
        this.properties.setFromProperties(properties)
    }

    constructor (
        properties: MutablePlayerProperties
    ) : this (properties.playerID) {
        this.properties = properties
    }

    protected var properties = MutablePlayerProperties()

    override val name
        get() = properties.name

    override var uuid
        get() = properties.playerID
        protected set(value) { properties.playerID = value }

    override var storeCoordinates = defaultStoreCoords()

    val tournamentID        get() = properties.tournamentID
    val actorType           get() = properties.actorType
    val seed                get() = properties.seed
    val originalSeed        get() = properties.originalSeed
    val currentMatchID      get() = properties.currentMatchID
    val finalPlacement      get() = properties.finalPlacement
    val pokemonTeamID       get() = properties.pokemonTeamID
    val pokemonFinal        get() = properties.pokemonFinal
    val lockPokemonOnSet    get() = properties.lockPokemonOnSet

    override fun printProperties() = properties.printProperties()

    fun getProperties() = properties.deepCopy()

    /* TODO implement tournament properties to dictate set-ability of pokemon
        -> ex "Lock Pokemon Teams On Set" */
    protected fun setPokemonTeamID(
        pokemonTeamID: UUID?
    )
    {
        if (!properties.pokemonFinal) {
            return
        }
        properties.pokemonTeamID = pokemonTeamID
        if (pokemonTeamID != null && properties.lockPokemonOnSet) {
            properties.pokemonFinal = true
        }
    }

    override fun initialize(): TournamentPlayer {
        registerObservable(properties.anyChangeObservable)
        return this
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        nbt.put(TournamentDataKeys.PLAYER_PROPERTIES,properties.saveToNBT(CompoundTag()))
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ): TournamentPlayer {
        properties.setFromNBT( nbt.getCompound(TournamentDataKeys.PLAYER_PROPERTIES))
        return this
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not yet implemented") }

    override fun loadFromJSON(json: JsonObject): ClassStored { TODO("Not yet implemented") }

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<TournamentPlayer>()

    fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable(): Observable<TournamentPlayer> = anyChangeObservable

    protected fun <T> registerObservable(
        observable: SimpleObservable<T>
    ) : SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

}
