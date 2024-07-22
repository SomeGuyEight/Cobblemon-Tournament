package com.cobblemontournament.common.player

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.someguy.storage.classstored.ClassStored
import com.cobblemontournament.common.api.storage.TournamentDataKeys.PLAYER_PROPERTIES_KEY
import com.google.gson.JsonObject
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

/** &#9888; (UUID) constructor is needed for serialization method */
open class TournamentPlayer(protected val properties: PlayerProperties) : ClassStored {

    override val name get() = properties.name
    override var uuid get() = properties.playerID
        protected set( value ) { properties.playerID = value }

    val tournamentID get() = properties.tournamentID
    val actorType get() = properties.actorType
    val seed get() = properties.seed
    val originalSeed get() = properties.originalSeed
    val lockPokemonOnSet get() = properties.lockPokemonOnSet
    var currentMatchID get() = properties.currentMatchID
        set(value) { properties.currentMatchID = value }
    var finalPlacement get() = properties.finalPlacement
        set(value) { properties.finalPlacement = value }
    var pokemonTeamID get() = properties.pokemonTeamID
        private set(value) {
            if (!(properties.pokemonTeamID != null && properties.pokemonFinal)) {
                properties.pokemonTeamID = value
                if (properties.pokemonTeamID != null && properties.lockPokemonOnSet) {
                    properties.pokemonFinal = true
                }
            }
        }
    var pokemonFinal get() = properties.pokemonFinal
        protected set(value) { properties.pokemonFinal = value }

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> = SettableObservable(value = null)
    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<TournamentPlayer>()

    constructor(uuid: UUID = UUID.randomUUID()) : this(PlayerProperties(uuid = uuid) )

    /**
     * Initializes & returns a reference to itself
     *
     * &#9888; Observables will be broken if [initialize] is not called after construction
     */
    override fun initialize(): TournamentPlayer {
        registerObservable(observable = properties.anyChangeObservable)
        return this
    }

    private fun registerObservable(observable: Observable<*>): Observable<*> {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit((this)) }
        return observable
    }

    fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable() = anyChangeObservable

    override fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.put(PLAYER_PROPERTIES_KEY, properties.saveToNBT(nbt = CompoundTag()))
        return nbt
    }
    override fun loadFromNBT(nbt: CompoundTag): TournamentPlayer {
        properties.setFromNBT(nbt = nbt.getCompound(PLAYER_PROPERTIES_KEY))
        return this
    }
    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not yet implemented") }
    override fun loadFromJSON(json: JsonObject): ClassStored { TODO("Not yet implemented") }

    override fun printProperties() = properties.logDebug()

    fun displayInChat(player: ServerPlayer) = properties.displayInChat(player = player)

    companion object {
        /** &#9888; Observables will be broken if [initialize] is not called after construction */
        fun loadFromNbt(nbt: CompoundTag): TournamentPlayer {
            return TournamentPlayer(
                PlayerProperties.loadFromNbt(nbt = nbt.getCompound(PLAYER_PROPERTIES_KEY))
            )
        }
    }

}
