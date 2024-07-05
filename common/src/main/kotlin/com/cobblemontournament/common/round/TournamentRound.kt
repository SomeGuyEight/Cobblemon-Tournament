package com.cobblemontournament.common.round

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.round.properties.MutableRoundProperties
import com.cobblemontournament.common.round.properties.RoundProperties
import com.cobblemontournament.common.util.TournamentDataKeys
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.classstored.extension.ClassStoredExtension.defaultStoreCoords
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.util.UUID

// Important: (UUID) constructor is needed for serialization method
open class TournamentRound(uuid: UUID) : ClassStored
{
    constructor() : this (UUID.randomUUID())

    constructor (
        properties: RoundProperties,
    ) : this (properties.roundID)
    {
        this.properties.setFromProperties(properties)
    }

    constructor (
        properties: MutableRoundProperties,
    ) : this (properties.roundID)
    {
        this.properties = properties
    }

    protected var properties = MutableRoundProperties()
    override val name
        get() = "Round $roundIndex [${roundType.name} type]"
    override var uuid
        get() = properties.roundID
        protected set(value) { properties.roundID = value }
    override var storeCoordinates = defaultStoreCoords()

    val tournamentID    get()   = properties.tournamentID
    val roundIndex      get()   = properties.roundIndex
    val roundType       get()   = properties.roundType
    protected val indexedMatchMap get() = properties.indexedMatchMap

    override fun printProperties() = properties.printProperties()
    fun getProperties()         = properties.deepCopy()
    fun getMatchMapEntries()    = indexedMatchMap.entries.toSet()
    fun getMatchMapSize()       = indexedMatchMap.size

    override fun initialize(): TournamentRound {
        registerObservable(properties.anyChangeObservable)
        return this
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ) : CompoundTag
    {
        nbt.put(TournamentDataKeys.ROUND_PROPERTIES,properties.saveToNBT(CompoundTag()))
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ) : TournamentRound
    {
        properties.saveToNBT( nbt.getCompound(TournamentDataKeys.ROUND_PROPERTIES))
        return this
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not yet implemented") }

    override fun loadFromJSON(json: JsonObject): ClassStored { TODO("Not yet implemented") }

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<TournamentRound>()

    fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable(): Observable<TournamentRound> = anyChangeObservable

    protected fun <T> registerObservable(
        observable: SimpleObservable<T>
    ) : SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

}
