package com.cobblemontournament.common.tournament

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.tournament.properties.MutableTournamentProperties
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.someguy.storage.classstored.ClassStored
import com.cobblemontournament.common.util.TournamentDataKeys
import com.someguy.storage.classstored.extension.ClassStoredExtension.defaultStoreCoords
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.util.UUID

// Important: (UUID) constructor is needed for serialization method
open class Tournament(uuid: UUID) : ClassStored
{
    constructor() : this (UUID.randomUUID())

    constructor (
        properties: TournamentProperties,
    ) : this (properties.tournamentID) {
        this.properties.setFromProperties(properties)
    }

    constructor (
        properties: MutableTournamentProperties,
    ) : this (properties.tournamentID) {
        this.properties = properties
    }

    protected var properties = MutableTournamentProperties()

    override val name
        get() = properties.name

    override var uuid
        get() = properties.tournamentID
        protected set(value) { properties.tournamentID = value }

    override var storeCoordinates = defaultStoreCoords()

    val tournamentID        get() = properties.tournamentID
    val tournamentType      get() = properties.tournamentType
    val challengeFormat     get() = properties.challengeFormat
    val maxParticipants     get() = properties.maxParticipants
    val teamSize            get() = properties.teamSize
    val groupSize           get() = properties.groupSize
    val minLevel            get() = properties.minLevel
    val maxLevel            get() = properties.maxLevel
    val showPreview         get() = properties.showPreview
    val totalRounds         get() = properties.totalRounds
    val totalMatches        get() = properties.totalMatches
    val totalPlayers        get() = properties.totalPlayers
    protected val players   get() = properties.players

    override fun printProperties() = properties.printProperties()
    fun getProperties() = properties.deepCopy()

    fun containsPlayerID(playerID: UUID) = players.contains(playerID)
    fun containsPlayerName(name: String) = players.firstNotNullOfOrNull { it.value == name } != null
    fun getPlayerNames()    = players.values.toSet()
    fun getPlayerIDs()      = players.keys.toSet()

    override fun initialize() : Tournament {
        registerObservable(properties.anyChangeObservable)
        return this
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ) : CompoundTag {
        nbt.put(TournamentDataKeys.TOURNAMENT_PROPERTIES,properties.saveToNBT(CompoundTag()))
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ) : Tournament {
        properties.setFromNBT(nbt.getCompound(TournamentDataKeys.TOURNAMENT_PROPERTIES))
        return this
    }

    override fun saveToJSON(json: JsonObject) : JsonObject { TODO("Not yet implemented") }

    override fun loadFromJSON(json: JsonObject) : ClassStored { TODO("Not yet implemented") }

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<Tournament>()

    fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable(): Observable<Tournament> = anyChangeObservable

    protected fun <T> registerObservable(
        observable: SimpleObservable<T>
    ) : SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

}
