package com.cobblemontournament.common.tournamentbuilder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.generator.TournamentGenerator
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.tournamentbuilder.properties.TournamentBuilderProperties
import com.cobblemontournament.common.api.tournament.TournamentData
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.cobblemontournament.common.util.ChatUtil
import com.google.gson.JsonObject
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

/** &#9888; (UUID) constructor is needed for serialization method */
open class TournamentBuilder : ClassStored
{
    companion object {
        /** &#9888; Observables will be broken if [initialize] is not called after construction */
        fun loadFromNBT( nbt: CompoundTag ): TournamentBuilder {
            return TournamentBuilder( TournamentBuilderProperties.loadFromNBT( nbt.getCompound( DataKeys.TOURNAMENT_BUILDER_PROPERTIES ) ) )
        }
    }

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    constructor( uuid: UUID = UUID.randomUUID() ) : this ( TournamentBuilderProperties( uuid ) )
    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    constructor( properties: TournamentBuilderProperties ) {
        this.properties = properties
    }

    protected val properties: TournamentBuilderProperties

    override var name
        get() = properties.name
        set( value ) { properties.name = value }

    override var uuid
        get() = properties.tournamentBuilderID
        protected set ( value ) { properties.tournamentBuilderID = value }

    override var storeCoordinates: SettableObservable <StoreCoordinates <*,*>? > = SettableObservable( value = null )

    var tournamentType
        get() = properties.tournamentProperties.tournamentType
        set( value ) { properties.tournamentProperties.tournamentType = value }

    var challengeFormat
        get() = properties.tournamentProperties.challengeFormat
        set( value ) { properties.tournamentProperties.challengeFormat = value }

    var maxParticipants
        get() = properties.tournamentProperties.maxParticipants
        set( value ) { properties.tournamentProperties.maxParticipants = value }

    var teamSize
        get() = properties.tournamentProperties.teamSize
        set( value ) { properties.tournamentProperties.teamSize = value }

    var groupSize
        get() = properties.tournamentProperties.groupSize
        set( value ) { properties.tournamentProperties.groupSize = value }

    var minLevel
        get() = properties.tournamentProperties.minLevel
        set( value ) { properties.tournamentProperties.minLevel = value }

    var maxLevel
        get() = properties.tournamentProperties.maxLevel
        set( value ) { properties.tournamentProperties.maxLevel = value }

    var showPreview
        get() = properties.tournamentProperties.showPreview
        set( value ) { properties.tournamentProperties.showPreview = value }

    fun getTournamentProperties(
        name            : String,
        tournamentID    : UUID = UUID.randomUUID()
    ): TournamentProperties
    {
        val copy = properties.tournamentProperties.deepCopy()
        copy.name = name
        copy.tournamentID = tournamentID
        return copy
    }

    override fun printProperties() = properties.logDebug()
    fun displayPropertiesInChat(player: ServerPlayer ) = properties.displayInChat( player )
    fun displayPropertiesInChatSlim( player: ServerPlayer ) = properties.displayInChatSlim( player )

    fun printPlayerInfo() {
        for ( player in properties.getPlayersSortedBy { it.seed } ) {
            player.logDebug()
        }
    }

    fun displayPlayerInfoInChat(
        player              : ServerPlayer,
        spacing             : String = "",
        displaySeed         : Boolean = false,
        displayPokemon      : Boolean = false,
        displayCurrentMatch : Boolean = false,
        displayPlacement    : Boolean = false )
    {
        if ( properties.getPlayersSize() != 0 ) {
            ChatUtil.displayInPlayerChat(
                player  = player,
                text    = "Players for Tournament Builder \"$name\":", ChatUtil.yellow,
                bold    = true )
            for ( playerProps in properties.getPlayersSortedBy { it.seed } ) {
                PlayerPropertiesHelper.displayInChatOptionalHelper(
                    properties          = playerProps,
                    player              = player,
                    spacing             = spacing,
                    displaySeed         = displaySeed,
                    displayPokemon      = displayPokemon,
                    displayCurrentMatch = displayCurrentMatch,
                    displayPlacement    = displayPlacement )
            }
        } else {
            ChatUtil.displayInPlayerChat( player, "No players registered for Tournament Builder \"$name\"." )
        }
    }

    fun containsPlayerID( playerID: UUID ) = properties.containsPlayerID( playerID )

    fun getSeededPlayers()          = properties.getSeededPlayers()
    fun getUnseededPlayers()        = properties.getUnseededPlayers()
    fun playersSize()               = properties.getPlayersSize()

    /**
     *  Initializes & returns a reference to itself
     *
     * &#9888; Observables will be broken if [initialize] is not called after construction
     */
    override fun initialize(): TournamentBuilder {
        registerObservable( properties.getChangeObservable() )
        return this
    }

    fun addPlayer(
        playerID    : UUID,
        playerName  : String,
        actorType   : ActorType? = null,
        seed        : Int? = null
    ): Boolean
    {
        return if ( properties.containsPlayerID( playerID ) ) {
             false
        } else {
            properties.addPlayer(
                playerProps = PlayerProperties(
                    name            = playerName,
                    actorType       = actorType ?: ActorType.PLAYER,
                    playerID        = playerID,
                    tournamentID    = uuid,
                    seed            = seed ?: -1 ) )
        }
    }

    protected fun addPlayer( playerProps: PlayerProperties ) = properties.addPlayer( playerProps )

    fun getPlayer( playerID: UUID ) = properties.getPlayer( playerID )

    fun getPlayer( name: String ) = properties.getPlayer( name )

    fun getPlayerNames(): Set <String>
    {
        val names = mutableSetOf <String>()
        for ( playerProps in properties.getPlayersIterator() ) {
            names.add( playerProps.name )
        }
        return names
    }

    fun updatePlayer(
        playerID    : UUID,
        actorType   : ActorType?,
        seed        : Int?
    ): Boolean
    {
        val playerProps = getPlayer( playerID ) ?: return false

        val updated = if ( actorType != null ) {
            playerProps.actorType = actorType
            true
        } else false

        return if ( seed != null && seed != playerProps.seed ) {
            playerProps.seed = seed
            playerProps.originalSeed = seed
            true
        } else updated
    }

    fun removePlayer( playerID: UUID ) = properties.removePlayer( playerID )

    fun removePlayerByName( name: String ) = properties.removePlayer( name )

    fun toTournament(
        name: String
    ): TournamentData?
    {
        val tournamentData = TournamentGenerator.toTournament( name = name, builder = this )
        tournamentData?.sendAllToManager()
        return tournamentData
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        nbt.put( DataKeys.TOURNAMENT_BUILDER_PROPERTIES, properties.saveToNBT( CompoundTag() ) )
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ): TournamentBuilder {
        properties.setFromNBT( nbt.getCompound( DataKeys.TOURNAMENT_BUILDER_PROPERTIES ) )
        return this
    }

    override fun saveToJSON( json: JsonObject ): JsonObject { TODO("Not implemented") }

    override fun loadFromJSON( json: JsonObject ): TournamentBuilder { TODO("Not implemented") }

    private val observables = mutableListOf <Observable <*> >()
    val anyChangeObservable = SimpleObservable <TournamentBuilder>()

    fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    private fun registerObservable(
        observable: Observable <*>
    ) : Observable <*>
    {
        observables.add( observable )
        observable.subscribe { anyChangeObservable.emit( values = arrayOf( this ) ) }
        return observable
    }

}
