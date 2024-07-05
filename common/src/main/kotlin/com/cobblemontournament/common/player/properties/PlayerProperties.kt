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
import com.someguy.storage.properties.PropertiesCompanion
import net.minecraft.nbt.CompoundTag
import java.util.UUID

data class PlayerProperties(
    override val name               : String,
    override val actorType          : ActorType,
    override val playerID           : UUID,
    override val tournamentID       : UUID,
    override val seed               : Int       = DEFAULT_SEED,
    override val originalSeed       : Int       = DEFAULT_SEED,
    override val finalPlacement     : Int       = DEFAULT_FINAL_PLACEMENT,
    override val pokemonTeamID      : UUID?     = DEFAULT_POKEMON_TEAM_ID,
    override val currentMatchID     : UUID?     = DEFAULT_CURRENT_MATCH_ID,
    override var pokemonFinal       : Boolean   = DEFAULT_POKEMON_FINAL,
    override var lockPokemonOnSet   : Boolean   = DEFAULT_LOCK_POKEMON_ON_SET,
) : PlayerPropertyFields, Properties <PlayerPropertyFields,PlayerProperties,MutablePlayerProperties>
{
    companion object
        : PropertiesCompanion<PlayerPropertyFields, PlayerProperties, MutablePlayerProperties>
    {
        override val helper = PlayerPropertiesHelper
    }

    constructor() : this(
        name            = DEFAULT_PLAYER_NAME,
        actorType       = DEFAULT_ACTOR_TYPE,
        playerID        = UUID.randomUUID(),
        tournamentID    = UUID.randomUUID()
    )

    override fun getHelper() = PlayerPropertiesHelper

    override fun deepCopy() = helper.deepCopyHelper( properties = this)

    override fun deepMutableCopy() = helper.deepMutableCopyHelper( properties = this)

    override fun saveToNBT( nbt: CompoundTag) = helper.saveToNBTHelper( properties = this, nbt = nbt)

    // player properties are immutable so empty & a placeholder is fine
    override fun getAllObservables(): Iterable<Observable<*>> = emptyList()
    override fun getChangeObservable(): Observable<PlayerProperties> = SimpleObservable()
}
