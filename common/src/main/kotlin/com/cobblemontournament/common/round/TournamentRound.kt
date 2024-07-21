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

/** &#9888; (UUID) constructor is needed for serialization method */
open class TournamentRound : ClassStored
{
    companion object {
        /** &#9888; Observables will be broken if [initialize] is not called after construction */
        fun loadFromNBT( nbt: CompoundTag ): TournamentRound {
            return TournamentRound( RoundProperties.loadFromNBT( nbt.getCompound( DataKeys.ROUND_PROPERTIES ) ) )
        }
    }

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    constructor ( uuid: UUID = UUID.randomUUID() ) : this ( RoundProperties( uuid ) )
    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    constructor ( properties: RoundProperties) {
        this.properties = properties
    }

    protected val properties: RoundProperties

    override val name get() = "Round $roundIndex [${roundType.name} type]"

    override var uuid
        get() = properties.roundID
        protected set( value ) { properties.roundID = value }

    override var storeCoordinates: SettableObservable <StoreCoordinates <*,*>? > = SettableObservable( value = null )

    val tournamentID        get()   = properties.tournamentID
    val roundIndex          get()   = properties.roundIndex
    private val roundType   get()   = properties.roundType
    val matchMapSize        get()   = properties.indexedMatchMap.size

    fun getMatchID( roundMatchIndex: Int ) = properties.indexedMatchMap[roundMatchIndex]

    override fun printProperties() = properties.logDebug()
    // fun copyProperties() = properties.deepCopy()

    /**
     *  Initializes & returns a reference to itself
     *
     * &#9888; Observables will be broken if [initialize] is not called after construction
     */
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

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    override fun loadFromNBT(
        nbt: CompoundTag
    ) : TournamentRound {
        properties.setFromNBT( nbt.getCompound( DataKeys.ROUND_PROPERTIES ) )
        return this
    }

    override fun saveToJSON( json: JsonObject ): JsonObject { TODO("Not yet implemented") }

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    override fun loadFromJSON( json: JsonObject ): ClassStored { TODO("Not yet implemented") }

    private val observables = mutableListOf <Observable <*>>()
    val anyChangeObservable = SimpleObservable <TournamentRound>()

    fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    private fun registerObservable(
        observable: Observable <*>
    ): Observable <*>
    {
        observables.add( observable )
        observable.subscribe { anyChangeObservable.emit( values = arrayOf( this ) ) }
        return observable
    }

}
