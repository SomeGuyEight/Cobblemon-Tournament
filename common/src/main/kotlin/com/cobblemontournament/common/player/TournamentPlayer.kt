package com.cobblemontournament.common.player

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.*
import com.cobblemontournament.common.api.storage.PLAYER_PROPERTIES_KEY
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.google.gson.JsonObject
import com.sg8.storage.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

open class TournamentPlayer(protected val properties: PlayerProperties) : TypeStored {

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable(value = null)

    private val anyChangeObservable = SimpleObservable<TournamentPlayer>()

    val playerID: UUID get() = properties.uuid
    override val name: String get() = properties.name
    override val uuid: UUID get() = properties.uuid
    val tournamentID: UUID get() = properties.tournamentID
    val actorType: ActorType get() = properties.actorType
    val seed: Int get() = properties.seed
    val originalSeed: Int get() = properties.originalSeed
    val lockPokemonOnSet: Boolean get() = properties.lockPokemonOnSet
    var currentMatchID: UUID?
        get() = properties.currentMatchID
        set(value) { properties.currentMatchID = value }
    var finalPlacement: Int
        get() = properties.finalPlacement
        set(value) { properties.finalPlacement = value }
    var pokemonTeamID: UUID?
        get() = properties.pokemonTeamID
        private set(value) {
            if (!(properties.pokemonTeamID != null && properties.pokemonFinal)) {
                properties.pokemonTeamID = value
                if (properties.pokemonTeamID != null && properties.lockPokemonOnSet) {
                    properties.pokemonFinal = true
                }
            }
        }
    var pokemonFinal: Boolean
        get() = properties.pokemonFinal
        protected set(value) { properties.pokemonFinal = value }

    init {
        properties.observable.subscribe { emitChange() }
    }

    /** &#9888; (UUID) constructor is needed for serialization method */
    constructor(playerUuid: UUID = UUID.randomUUID()) :
           this(PlayerProperties(uuid = playerUuid))

    override fun initialize() = this

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getObservable(): Observable<TournamentPlayer> = anyChangeObservable

    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        nbt.put(PLAYER_PROPERTIES_KEY, properties.saveToNbt(nbt = CompoundTag()))
        return nbt
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO() }

    override fun loadFromNBT(nbt: CompoundTag): TournamentPlayer {
        properties.setFromNbt(nbt = nbt.getCompound(PLAYER_PROPERTIES_KEY))
        return this
    }

    override fun loadFromJSON(json: JsonObject): TypeStored { TODO() }

    fun deepCopy() = TournamentPlayer(properties.deepCopy())

    fun copy() = TournamentPlayer(properties.copy())

    fun displayInChat(
        player: ServerPlayer,
        padStart: Int = 0,
        displaySeed: Boolean = false,
        displayPokemon: Boolean = false,
        displayCurrentMatch: Boolean = false,
        displayPlacement: Boolean = false,
    ) {
        properties.displayInChat(
            properties = properties,
            player = player,
            padStart = padStart,
            displaySeed = displaySeed,
            displayPokemon = displayPokemon,
            displayCurrentMatch = displayCurrentMatch,
            displayPlacement = displayPlacement,
        )
    }

    override fun printProperties() = properties.printDebug()

    companion object {
        fun loadFromNbt(nbt: CompoundTag): TournamentPlayer {
            return TournamentPlayer(
                PlayerProperties.loadFromNbt(nbt = nbt.getCompound(PLAYER_PROPERTIES_KEY))
            )
        }
    }

}
