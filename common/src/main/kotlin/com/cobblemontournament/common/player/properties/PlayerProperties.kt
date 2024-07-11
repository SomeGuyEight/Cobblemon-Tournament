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
import com.someguy.storage.properties.Properties
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class PlayerProperties : Properties <PlayerProperties>
{
    companion object {
        val HELPER = PlayerPropertiesHelper
        fun loadFromNbt( nbt: CompoundTag ) = HELPER.loadFromNBTHelper( nbt )
    }

    constructor() : this (
        name            = DEFAULT_PLAYER_NAME,
        actorType       = DEFAULT_ACTOR_TYPE,
        playerID        = UUID.randomUUID(),
        tournamentID    = UUID.randomUUID())

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
        lockPokemonOnSet   : Boolean   = DEFAULT_LOCK_POKEMON_ON_SET, )
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

    override val instance = this
    override val helper = PlayerPropertiesHelper

    var name: String = DEFAULT_PLAYER_NAME
        set( value ) { field = value; emitChange() }

    var actorType = DEFAULT_ACTOR_TYPE
        set( value ) { field = value; emitChange() }

    var playerID: UUID = UUID.randomUUID()
        set( value ) { field = value; emitChange() }

    var tournamentID: UUID = UUID.randomUUID()
        set( value ) { field = value; emitChange() }

    var seed = DEFAULT_SEED
        set( value ) { field = value; emitChange() }

    var originalSeed = DEFAULT_SEED
        set( value ) { field = value; emitChange() }

    var pokemonTeamID: UUID? = DEFAULT_POKEMON_TEAM_ID
        set( value ) { field = value; emitChange() }

    var currentMatchID: UUID? = DEFAULT_CURRENT_MATCH_ID
        set( value ) { field = value; emitChange() }

     var finalPlacement = DEFAULT_FINAL_PLACEMENT
        set( value ) { field = value; emitChange() }

    var pokemonFinal = DEFAULT_POKEMON_FINAL
        set( value ) { field = value; emitChange() }

    var lockPokemonOnSet = DEFAULT_LOCK_POKEMON_ON_SET
        set( value ) { field = value; emitChange() }

    private val observables = mutableListOf <Observable <*>>()
    val anyChangeObservable = SimpleObservable <PlayerProperties>()

    private fun emitChange() = anyChangeObservable.emit( values = arrayOf( this ) )
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

}
