package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.someguy.storage.properties.Properties
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class PlayerProperties(
    name: String,
    actorType: ActorType,
    playerID: UUID,
    tournamentID: UUID,
    seed: Int = PlayerPropertiesHelper.DEFAULT_SEED,
    originalSeed: Int = PlayerPropertiesHelper.DEFAULT_SEED,
    pokemonTeamID: UUID? = PlayerPropertiesHelper.DEFAULT_POKEMON_TEAM_ID,
    currentMatchID: UUID? = PlayerPropertiesHelper.DEFAULT_CURRENT_MATCH_ID,
    finalPlacement: Int = PlayerPropertiesHelper.DEFAULT_FINAL_PLACEMENT,
    pokemonFinal: Boolean = PlayerPropertiesHelper.DEFAULT_POKEMON_FINAL,
    lockPokemonOnSet: Boolean = PlayerPropertiesHelper.DEFAULT_LOCK_POKEMON_ON_SET,
) : Properties<PlayerProperties>,
    Comparable<PlayerProperties> {

    override val instance = this
    var name: String = name
        set(value) {
            field = value
            emitChange()
        }
    var actorType: ActorType = actorType
        set(value) {
            field = value
            emitChange()
        }
    var playerID: UUID = playerID
        set(value) {
            field = value
            emitChange()
        }
    var tournamentID: UUID = tournamentID
        set(value) {
            field = value
            emitChange()
        }
    var seed: Int = seed
        set(value) {
            field = value
            emitChange()
        }
    var originalSeed: Int = originalSeed
        set(value) {
            field = value
            emitChange()
        }
    var pokemonTeamID: UUID? = pokemonTeamID
        set(value) {
            field = value
            emitChange()
        }
    var currentMatchID: UUID? = currentMatchID
        set(value) {
            field = value
            emitChange()
        }
    var finalPlacement: Int = finalPlacement
         set(value) {
             field = value
             emitChange()
         }
    var pokemonFinal: Boolean = pokemonFinal
        set(value) {
            field = value
            emitChange()
        }
    var lockPokemonOnSet: Boolean = lockPokemonOnSet
        set(value) {
            field = value
            emitChange()
        }

    override val helper = PlayerPropertiesHelper
    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<PlayerProperties>()

    constructor(uuid: UUID = UUID.randomUUID()) : this(
        name = PlayerPropertiesHelper.DEFAULT_PLAYER_NAME,
        actorType = PlayerPropertiesHelper.DEFAULT_ACTOR_TYPE,
        playerID = uuid,
        tournamentID = UUID.randomUUID(),
    )

    private fun emitChange() = anyChangeObservable.emit((this))

    override fun getAllObservables() = observables.asIterable()

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

    companion object {
        private val HELPER = PlayerPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNBTHelper(nbt = nbt)
    }

}
