package com.cobblemontournament.common.tournamentbuilder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.generator.TournamentGenerator
import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.player.properties.MutablePlayerProperties
import com.cobblemontournament.common.tournamentbuilder.properties.MutableTournamentBuilderProperties
import com.cobblemontournament.common.tournament.TournamentData
import com.cobblemontournament.common.util.TournamentDataKeys
import com.google.gson.JsonObject
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.classstored.extension.ClassStoredExtension.defaultStoreCoords
import net.minecraft.nbt.CompoundTag
import java.util.UUID
import java.util.function.Predicate

open class TournamentBuilder(uuid: UUID): ClassStored
{
    companion object {
        val helper = TournamentGenerator
    }

    constructor(): this(UUID.randomUUID())

    protected val builderProperties = MutableTournamentBuilderProperties()

    override var name
        get() = builderProperties.name
        set(value) { builderProperties.name = value }

    override var uuid
        get() = builderProperties.tournamentBuilderID
        protected set (value) { builderProperties.tournamentBuilderID = value }

    override val storeCoordinates = defaultStoreCoords()

    var tournamentProperties
        get() = builderProperties.tournamentProperties
        protected set(value) { builderProperties.tournamentProperties.setFromProperties(value) }

    var tournamentName
        get() = tournamentProperties.name
        set(value) { tournamentProperties.name = value }

    var tournamentType
        get() = tournamentProperties.tournamentType
        set(value) { tournamentProperties.tournamentType = value }

    var challengeFormat
        get() = tournamentProperties.challengeFormat
        set(value) { tournamentProperties.challengeFormat = value }

    var maxParticipants
        get() = tournamentProperties.maxParticipants
        set(value) { tournamentProperties.maxParticipants = value }

    var teamSize
        get() = tournamentProperties.teamSize
        set(value) { tournamentProperties.teamSize = value }

    var groupSize
        get() = tournamentProperties.groupSize
        set(value) { tournamentProperties.groupSize = value }

    var minLevel
        get() = tournamentProperties.minLevel
        set(value) { tournamentProperties.minLevel = value }

    var maxLevel
        get() = tournamentProperties.maxLevel
        set(value) { tournamentProperties.maxLevel = value }

    var showPreview
        get() = tournamentProperties.showPreview
        set(value) { tournamentProperties.showPreview = value }

    protected val seededPlayers     get() = builderProperties.seededPlayers
    protected val unseededPlayers   get() = builderProperties.unseededPlayers

    override fun printProperties() = builderProperties.printProperties()
    fun printPlayerProperties() {
        seededPlayers.forEach { it.printProperties() }
        unseededPlayers.forEach { it.printProperties() }
    }

    fun getProperties()             = builderProperties.deepCopy()

    fun getSeededPlayers()          = seededPlayers.toList()
    fun getUnseededPlayers()        = unseededPlayers.toList()

    fun getMutableSeededPlayers()   = seededPlayers.toMutableList()
    fun getMutableUnseededPlayers() = unseededPlayers.toMutableList()

    fun seededPlayersSize()         = seededPlayers.size
    fun unseededPlayersSize()       = unseededPlayers.size
    fun totalPlayersSize()          = seededPlayers.size + unseededPlayers.size

    override fun initialize(): TournamentBuilder {
        registerObservable(builderProperties.anyChangeObservable)
        return this
    }

    fun addPlayer(
        playerID: UUID,
        playerName: String,
        actorType: ActorType? = null,
        seed: Int? = null
    ): Boolean
    {
        val predicate: Predicate<in MutablePlayerProperties?> =
            Predicate { it!!.playerID === playerID; }
        if (containsPlayerWith(seededPlayers, predicate) || containsPlayerWith(unseededPlayers, predicate)) {
            return false
        }
        val properties =  MutablePlayerProperties(
            name            = playerName,
            actorType       = actorType?: ActorType.PLAYER,
            playerID        = playerID,
            tournamentID    = uuid,
            seed            = seed?: -1
        )
        return if (seed != null && seed > 0) {
            seededPlayers.add(properties)
        } else {
            unseededPlayers.add(properties)
        }
    }

    fun addPlayer(
        properties: MutablePlayerProperties
    ): Boolean
    {
        val predicate: Predicate<in MutablePlayerProperties?> =
            Predicate { it!!.playerID === properties.playerID; }
        if (containsPlayerWith(seededPlayers, predicate) || containsPlayerWith(unseededPlayers, predicate)) {
            return false
        }
        return if (properties.seed > 0) {
            seededPlayers.add(properties)
        } else {
            unseededPlayers.add(properties)
        }
    }

    fun getPlayer(
        playerID: UUID
    ) : MutablePlayerProperties?
    {
        var properties = seededPlayers.firstOrNull { it.playerID == playerID }
        if (properties == null) {
            properties = unseededPlayers.firstOrNull { it.playerID == playerID }
        }
        return properties
    }

    fun getPlayerNames() : Set<String>
    {
        val set = mutableSetOf<String>()
        seededPlayers.forEach { p -> set.add( p.name) }
        unseededPlayers.forEach { p -> set.add( p.name) }
        return set
    }

    fun updatePlayer(
        playerID: UUID,
        playerName: String,
        actorType: ActorType?,
        seed: Int?
    ) : Boolean
    {
        val notNullSeed = if (seed != null && seed > 0) seed else -1
        val removePredicate: Predicate<in MutablePlayerProperties?> =
            Predicate { it!!.playerID === playerID && it!!.seed != notNullSeed }
        removePlayerIf(seededPlayers, removePredicate)
        removePlayerIf(unseededPlayers, removePredicate)
        return addPlayer(
            playerID    = playerID,
            playerName  = playerName,
            actorType   = actorType,
            seed        = seed
        )
    }

    fun removePlayer(
        playerID: UUID
    ): Boolean {
        val removed = removePlayerIf(seededPlayers) { it!!.playerID == playerID }
        return removePlayerIf(unseededPlayers) { it!!.playerID == playerID } || removed
    }

    fun removePlayerByName(
        name: String
    ): Boolean
    {
        var id: UUID? = null
        run loop@ { seededPlayers.forEach {
            if (it.name == name) {
                id = it.playerID
                return@loop // solution to exit loop early b/c 'break' is not like c# break
            }
        } }
        if (id != null) {
            return removePlayer(id!!)
        }
        run loop@ { unseededPlayers.forEach {
            if (it.name == name) {
                id = it.playerID
                return@loop // solution to exit loop early b/c 'break' is not like c# break
            }
        } }
        return if (id == null) {
            false
        } else {
            removePlayer(id!!)
        }
    }

    fun containsPlayerWith(
        collection: MutableSet<MutablePlayerProperties>,
        predicate: Predicate<in MutablePlayerProperties?>
    ) = collection.stream().anyMatch(predicate)

    fun removePlayerIf(
        collection: MutableSet<MutablePlayerProperties>,
        predicate: Predicate<in MutablePlayerProperties?>
    ) = collection.removeIf(predicate)

    fun toTournament(
        name: String?   = null,
        save: Boolean?  = null,
        // storeID: UUID?  = null // when or if multiple builder stores implemented
    ) : TournamentData?
    {
        if (name != null) {
            tournamentProperties.name = name
        }
        val tournamentData = TournamentGenerator.toTournament(builder = this)
        if (save == true && tournamentData != null) {
                TournamentManager.addTournamentData( tournamentData)
        }
        return tournamentData
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ) : CompoundTag {
        nbt.put(TournamentDataKeys.TOURNAMENT_BUILDER_PROPERTIES,builderProperties.saveToNBT(CompoundTag()))
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ) : TournamentBuilder {
        builderProperties.setFromNBT(nbt.getCompound(TournamentDataKeys.TOURNAMENT_BUILDER_PROPERTIES))
        return this
    }

    override fun saveToJSON(json: JsonObject): JsonObject {TODO("Not implemented")}

    override fun loadFromJSON(json: JsonObject): TournamentBuilder {TODO("Not implemented")}

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<TournamentBuilder>()

    fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable(): Observable<TournamentBuilder> = anyChangeObservable

    protected fun <T> registerObservable(
        observable: SimpleObservable<T>
    ) : SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

}
