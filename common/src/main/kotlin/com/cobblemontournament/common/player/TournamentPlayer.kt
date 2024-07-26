package com.cobblemontournament.common.player

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.util.*
import com.someguy.storage.ClassStored
import com.google.gson.JsonObject
import com.someguy.storage.util.PlayerID
import com.someguy.storage.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

open class TournamentPlayer(protected val properties: PlayerProperties) : ClassStored {

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable(value = null)

    private val anyChangeObservable = SimpleObservable<TournamentPlayer>()

    override val name: String get() = properties.name
    override var uuid: PlayerID
        get() = properties.playerID
        protected set(value) { properties.playerID = value }
    val tournamentID: TournamentID get() = properties.tournamentID
    val actorType: ActorType get() = properties.actorType
    val seed: Int get() = properties.seed
    val originalSeed: Int get() = properties.originalSeed
    val lockPokemonOnSet: Boolean get() = properties.lockPokemonOnSet
    var currentMatchID: MatchID?
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
        properties.getChangeObservable().subscribe { emitChange() }
    }

    /** &#9888; (UUID) constructor is needed for serialization method */
    constructor(playerID: PlayerID = UUID.randomUUID()) :
           this(PlayerProperties(playerID = playerID))

    override fun initialize() = this

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getChangeObservable() = anyChangeObservable

    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        nbt.put(PLAYER_PROPERTIES_KEY, properties.saveToNbt(nbt = CompoundTag()))
        return nbt
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO() }

    override fun loadFromNBT(nbt: CompoundTag): TournamentPlayer {
        properties.setFromNbt(nbt = nbt.getCompound(PLAYER_PROPERTIES_KEY))
        return this
    }

    override fun loadFromJSON(json: JsonObject): ClassStored { TODO() }

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

    override fun printProperties() = properties.logDebug()

    companion object {
        fun loadFromNbt(nbt: CompoundTag): TournamentPlayer {
            return TournamentPlayer(
                PlayerProperties.loadFromNbt(nbt = nbt.getCompound(PLAYER_PROPERTIES_KEY))
            )
        }
    }

}
