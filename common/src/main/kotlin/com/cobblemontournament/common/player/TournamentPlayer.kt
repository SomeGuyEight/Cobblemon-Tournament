package com.cobblemontournament.common.player

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.someguy.storage.classstored.ClassStored
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.google.gson.JsonObject
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

// Important: (UUID) constructor is needed for serialization method
@Suppress( names = ["unused"] )
open class TournamentPlayer : ClassStored
{
    companion object {
        fun loadFromNBT( nbt: CompoundTag ): TournamentPlayer {
            return TournamentPlayer( PlayerProperties.loadFromNBT( nbt.getCompound( DataKeys.PLAYER_PROPERTIES ) ) )
        }
    }

    constructor ( uuid: UUID = UUID.randomUUID() ) : this ( PlayerProperties( uuid ) )
    constructor ( properties: PlayerProperties) {
        this.properties = properties
    }

    protected val properties: PlayerProperties

    override val name get() = properties.name

    override var uuid
        get() = properties.playerID
        protected set( value ) { properties.playerID = value }

    override var storeCoordinates: SettableObservable <StoreCoordinates <*,*>? > = SettableObservable( value = null )

    val tournamentID        get() = properties.tournamentID
    val actorType           get() = properties.actorType
    val seed                get() = properties.seed
    val originalSeed        get() = properties.originalSeed
    val lockPokemonOnSet    get() = properties.lockPokemonOnSet

    var currentMatchID
        get() = properties.currentMatchID
        set( value ) { properties.currentMatchID = value }

    var finalPlacement
        get() = properties.finalPlacement
        set( value ) { properties.finalPlacement = value }

    var pokemonTeamID: UUID? = null
        get() = properties.pokemonTeamID
        protected set( value ) {
            if ( field != null && !properties.pokemonFinal ) {
                return
            }
            field = value
            if ( pokemonTeamID != null && properties.lockPokemonOnSet ) {
                properties.pokemonFinal = true
            }
        }

    var pokemonFinal
        get() = properties.pokemonFinal
        protected set( value ) { properties.pokemonFinal = value }

    override fun initialize(): TournamentPlayer {
        registerObservable( properties.anyChangeObservable )
        return this
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        nbt.put( DataKeys.PLAYER_PROPERTIES, properties.saveToNBT( CompoundTag() ) )
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ): TournamentPlayer {
        properties.setFromNBT( nbt.getCompound( DataKeys.PLAYER_PROPERTIES ) )
        return this
    }

    override fun saveToJSON( json: JsonObject ): JsonObject { TODO("Not yet implemented") }

    override fun loadFromJSON( json: JsonObject ): ClassStored { TODO("Not yet implemented") }

    private val observables = mutableListOf <Observable <*>>()
    val anyChangeObservable = SimpleObservable <TournamentPlayer>()

    fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    protected fun registerObservable(
        observable: Observable <*>
    ) : Observable <*>
    {
        observables.add( observable )
        observable.subscribe { anyChangeObservable.emit( values = arrayOf( this ) ) }
        return observable
    }

    override fun printProperties() = properties.logDebug()

    fun displayInChat( player: ServerPlayer ) = properties.displayInChat( player )

    fun displayInChatOptional(
        player              : ServerPlayer,
        spacing             : String = "",
        displaySeed         : Boolean = false,
        displayPokemon      : Boolean = false,
        displayCurrentMatch : Boolean = false,
        displayPlacement    : Boolean = false )
    {
        PlayerPropertiesHelper.displayInChatOptionalHelper(
            properties, player, spacing, displaySeed, displayPokemon, displayCurrentMatch, displayPlacement )
    }

}
