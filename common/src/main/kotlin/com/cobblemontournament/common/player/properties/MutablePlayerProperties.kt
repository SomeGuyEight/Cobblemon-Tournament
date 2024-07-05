package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.DEFAULT_ACTOR_TYPE
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.DEFAULT_CURRENT_MATCH_ID
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.DEFAULT_FINAL_PLACEMENT
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.DEFAULT_LOCK_POKEMON_ON_SET
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.DEFAULT_PLAYER_NAME
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.DEFAULT_POKEMON_FINAL
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.DEFAULT_POKEMON_TEAM_ID
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.DEFAULT_SEED
import com.someguy.storage.properties.MutableProperties
import com.someguy.storage.properties.PropertiesCompanion
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MutablePlayerProperties: PlayerPropertyFields, MutableProperties <PlayerPropertyFields,PlayerProperties,MutablePlayerProperties>
{
    companion object: PropertiesCompanion<PlayerPropertyFields, PlayerProperties, MutablePlayerProperties> {
        override val helper = PlayerPropertiesHelper
    }

    constructor(
        name               : String,
        actorType          : ActorType,
        playerID           : UUID,
        tournamentID       : UUID,
        seed               : Int       = DEFAULT_SEED,
        originalSeed       : Int       = DEFAULT_SEED,
        pokemonTeamID      : UUID?     = DEFAULT_POKEMON_TEAM_ID,
        currentMatchID     : UUID?     = DEFAULT_CURRENT_MATCH_ID,
        finalPlacement     : Int       = DEFAULT_FINAL_PLACEMENT,
        pokemonFinal       : Boolean   = DEFAULT_POKEMON_FINAL,
        lockPokemonOnSet   : Boolean   = DEFAULT_LOCK_POKEMON_ON_SET,
    )
    {
        this.name               = name
        this.actorType          = actorType
        this.playerID           = playerID
        this.tournamentID       = tournamentID
        this.seed               = seed
        this.originalSeed       = originalSeed
        this.pokemonTeamID      = pokemonTeamID
        this.currentMatchID     = currentMatchID
        this.finalPlacement     = finalPlacement
        this.pokemonFinal       = pokemonFinal
        this.lockPokemonOnSet   = lockPokemonOnSet
    }

    constructor() : this (
        name            = DEFAULT_PLAYER_NAME,
        actorType       = DEFAULT_ACTOR_TYPE,
        playerID        = UUID.randomUUID(),
        tournamentID    = UUID.randomUUID(),
    )

    override var name = DEFAULT_PLAYER_NAME
        set(value) { field = value; emitChange() }

    override var actorType = DEFAULT_ACTOR_TYPE
        set(value) { field = value; emitChange() }

    override var playerID: UUID = UUID.randomUUID()
        set(value) { field = value; emitChange() }

    override var tournamentID: UUID = UUID.randomUUID()
        set(value) { field = value; emitChange() }

    override var seed = DEFAULT_SEED
        set(value) { field = value; emitChange() }

    override var originalSeed = DEFAULT_SEED
        set(value) { field = value; emitChange() }

    override var pokemonTeamID: UUID?     = DEFAULT_POKEMON_TEAM_ID
        set(value) { field = value; emitChange() }

    override var currentMatchID: UUID?     = DEFAULT_CURRENT_MATCH_ID
        set(value) { field = value; emitChange() }

    override var finalPlacement = DEFAULT_FINAL_PLACEMENT
        set(value) { field = value; emitChange() }

    override var pokemonFinal = DEFAULT_POKEMON_FINAL
        set(value) { field = value; emitChange() }

    override var lockPokemonOnSet = DEFAULT_LOCK_POKEMON_ON_SET
        set(value) { field = value; emitChange() }

    override fun getHelper() = PlayerPropertiesHelper

    override fun deepCopy() = helper.deepCopyHelper( properties = this)

    override fun deepMutableCopy() = helper.deepMutableCopyHelper( properties = this)

    override fun setFromNBT(
        nbt: CompoundTag
    ): MutablePlayerProperties {
        return helper.setFromNBTHelper( mutable = this, nbt = nbt)
    }

    override fun setFromProperties(
        from: PlayerPropertyFields
    ): MutablePlayerProperties {
        return helper.setFromPropertiesHelper(mutable = this, from = from)
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        return helper.saveToNBTHelper( properties = this, nbt = nbt)
    }

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<MutablePlayerProperties>()

    private fun emitChange() = anyChangeObservable.emit(this)
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable(): Observable<MutablePlayerProperties> = anyChangeObservable

    private fun <T> registerObservable(
        observable: SimpleObservable<T>
    ): SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }
}
