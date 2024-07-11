package com.cobblemontournament.common.round

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.round.properties.RoundProperties
import com.cobblemontournament.common.api.storage.DataKeys
import com.someguy.storage.classstored.ClassStored
import com.google.gson.JsonObject
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import java.util.UUID

// Important: (UUID) constructor is needed for serialization method
open class TournamentRound( uuid: UUID ) : ClassStored
{
    constructor() : this ( UUID.randomUUID() )

    constructor (
        properties: RoundProperties,
    ) : this ( properties.roundID )
    {
        this.properties.setFromProperties( properties )
    }

    protected var properties = RoundProperties()

    override val name get() = "Round $roundIndex [${roundType.name} type]"

    override var uuid
        get() = properties.roundID
        protected set( value ) { properties.roundID = value }

    override var storeCoordinates: SettableObservable <StoreCoordinates <*,*>? > = SettableObservable( value = null )

    val tournamentID    get()   = properties.tournamentID
    val roundIndex      get()   = properties.roundIndex
    val roundType       get()   = properties.roundType
    val matchMapSize    get()   = properties.indexedMatchMap.size

    fun getMatchID( roundMatchIndex: Int ) = properties.indexedMatchMap[roundMatchIndex]

    override fun printProperties() = properties.logDebug()
    // fun copyProperties() = properties.deepCopy()

    override fun initialize(): TournamentRound {
        registerObservable( properties.anyChangeObservable )
        return this
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ) : CompoundTag {
        nbt.put( DataKeys.ROUND_PROPERTIES, properties.saveToNBT( CompoundTag() ) )
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ) : TournamentRound {
        properties.setFromNBT( nbt.getCompound( DataKeys.ROUND_PROPERTIES ) )
        return this
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not yet implemented") }
    override fun loadFromJSON(json: JsonObject): ClassStored { TODO("Not yet implemented") }

    private val observables = mutableListOf <Observable <*>>()
    val anyChangeObservable = SimpleObservable<TournamentRound>()

    fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    protected fun registerObservable(
        observable: Observable <*>
    ) : Observable <*>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

}
