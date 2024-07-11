package com.cobblemontournament.common.tournamentbuilder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.generator.TournamentGenerator
import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.tournamentbuilder.properties.TournamentBuilderProperties
import com.cobblemontournament.common.api.tournament.TournamentData
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.util.ChatUtil
import com.google.gson.JsonObject
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID
import java.util.function.Predicate

open class TournamentBuilder( uuid: UUID ): ClassStored
{
    constructor(): this( UUID.randomUUID() )

    protected val properties = TournamentBuilderProperties()

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

    protected val seededPlayers     get() = properties.seededPlayers
    protected val unseededPlayers   get() = properties.unseededPlayers

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
    fun printPropertiesInChat( player: ServerPlayer ) = properties.displayInChat( player )

    fun printPlayerInfo() {
        seededPlayers.forEach { it.logDebug() }
        unseededPlayers.forEach { it.logDebug() }
    }

    fun printPlayerInfoInChat(player: ServerPlayer)
    {
        ChatUtil.displayInPlayerChat(
            player  = player,
            text    = "Players for Tournament Builder \"$name\":", ChatUtil.yellow,
            bold    = true )
        if ( seededPlayers.isNotEmpty() ) {
            ChatUtil.displayInPlayerChat(
                player = player,
                text = "(Seeded Players)",
                color = ChatUtil.yellow )
            seededPlayers.forEach { it.displayInChat( player ) }
        }
        if ( unseededPlayers.isNotEmpty() ) {
            ChatUtil.displayInPlayerChat(
                player  = player,
                text    = "(Unseeded Players)",
                color   = ChatUtil.yellow )
            unseededPlayers.forEach { it.displayInChat( player ) }
        }
    }

    fun getPropertiesCopy() = properties.deepCopy()

    fun containsPlayerID(
        playerID: UUID
    ): Boolean {
        return if ( seededPlayers.firstOrNull { it.playerID == playerID } != null ) {
            true
        } else unseededPlayers.firstOrNull { it.playerID == playerID } != null
    }

    fun getSeededPlayers()          = seededPlayers.toList()
    fun getUnseededPlayers()        = unseededPlayers.toList()

    fun getMutableSeededPlayers()   = seededPlayers.toMutableList()
    fun getMutableUnseededPlayers() = unseededPlayers.toMutableList()

    fun seededPlayersSize()         = seededPlayers.size
    fun unseededPlayersSize()       = unseededPlayers.size
    fun totalPlayersSize()          = seededPlayers.size + unseededPlayers.size

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
        val predicate: Predicate <in PlayerProperties? > =
            Predicate { it!!.playerID === playerID; }
        if ( containsPlayerWith( seededPlayers, predicate ) || containsPlayerWith( unseededPlayers, predicate ) ) {
            return false
        }
        val playerProps =  PlayerProperties(
            name            = playerName,
            actorType       = actorType ?: ActorType.PLAYER,
            playerID        = playerID,
            tournamentID    = uuid,
            seed            = seed ?: -1 )
        val success = if ( seed != null && seed > 0 ) {
            properties.seededPlayers.add( playerProps )
        } else {
            unseededPlayers.add( playerProps )
        }
        return if ( success ) {
            emitChange()
            registerObservable( properties.getChangeObservable() )
            true
        } else false
    }

    fun getPlayer(
        playerID: UUID
    ): PlayerProperties?
    {
        var playerProps = seededPlayers.firstOrNull { it.playerID == playerID }
        if ( playerProps == null ) {
            playerProps = unseededPlayers.firstOrNull { it.playerID == playerID }
        }
        return playerProps
    }

    fun getPlayer(
        name: String
    ): PlayerProperties?
    {
        var playerProps = seededPlayers.firstOrNull { it.name == name }
        if ( playerProps == null ) {
            playerProps = unseededPlayers.firstOrNull { it.name == name }
        }
        return playerProps
    }

    fun getPlayerNames(): Set<String>
    {
        val set = mutableSetOf<String>()
        seededPlayers.forEach { p -> set.add( p.name) }
        unseededPlayers.forEach { p -> set.add( p.name) }
        return set
    }

    fun updatePlayer(
        playerID    : UUID,
        actorType   : ActorType?,
        seed        : Int?
    ): Boolean
    {
        val player = getPlayer( playerID ) ?: return false

        val updatedActorType = if ( actorType != null ) {
            player.actorType = actorType
            true
        } else false

        val updatedSeed = if ( seed != null && seed != player.seed ) {
            player.seed = seed
            player.originalSeed = seed
            true
        } else false

        return if ( updatedSeed || updatedActorType ) {
            emitChange()
        } else false
    }

    fun removePlayer(
        playerID: UUID
    ): Boolean
    {
        var player = seededPlayers.firstOrNull { it.playerID == playerID }
        if ( player != null ) {
            return if ( seededPlayers.remove( player ) ) {
                emitChange()
            } else false
        }
        player = unseededPlayers.firstOrNull { it.playerID == playerID } ?: return false

        return if ( unseededPlayers.remove( player ) ) {
            emitChange()
        } else false
    }

    fun removePlayerByName(
        name: String
    ): Boolean
    {
        var id: UUID? = null
        run loop@ { seededPlayers.forEach {
            if ( it.name == name ) {
                id = it.playerID
                return@loop // solution to exit loop early b/c 'break' is not like c# break
            }
        } }
        if (id != null && removePlayer( id!! )) {
            return emitChange()
        }
        run loop@ { unseededPlayers.forEach {
            if ( it.name == name ) {
                id = it.playerID
                return@loop
            }
        } }
        return if ( id != null && removePlayer( id!! ) ) {
            emitChange()
        } else false
    }

    private fun containsPlayerWith(
        collection  : MutableSet <PlayerProperties>,
        predicate   : Predicate <in PlayerProperties ? >
    ): Boolean {
        return collection.stream().anyMatch( predicate )
    }

    fun toTournament(
        name: String
    ): TournamentData?
    {
        val tournamentData = TournamentGenerator.toTournament( name = name, builder = this )
        if (tournamentData != null) {
                TournamentStoreManager.addTournamentData( tournamentData )
        }
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

    private fun emitChange(): Boolean = anyChangeObservable.emit( values = arrayOf( this ) ) == Unit
    fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    protected fun registerObservable(
        observable: SimpleObservable <*>
    ) : SimpleObservable <*>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit( values = arrayOf( this ) ) }
        return observable
    }

}
