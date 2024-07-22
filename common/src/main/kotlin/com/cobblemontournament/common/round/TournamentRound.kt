package com.cobblemontournament.common.round

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.round.properties.RoundProperties
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_PROPERTIES_KEY
import com.someguy.storage.classstored.ClassStored
import com.google.gson.JsonObject
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import java.util.UUID

/** &#9888; (UUID) constructor is needed for serialization method */
open class TournamentRound(protected val properties: RoundProperties) : ClassStored {

    override val name get() = "Round $roundIndex [${roundType.name} type]"
    override var uuid get() = properties.roundID
        protected set(value) { properties.roundID = value }
    val tournamentID        get()   = properties.tournamentID
    val roundIndex          get()   = properties.roundIndex
    private val roundType   get()   = properties.roundType
    val matchMapSize        get()   = properties.indexedMatchMap.size

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> = SettableObservable(value = null)
    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<TournamentRound>()

    constructor(uuid: UUID = UUID.randomUUID()) : this(RoundProperties(uuid = uuid))

    override fun initialize(): TournamentRound {
        registerObservable(observable = properties.anyChangeObservable)
        return this
    }

    fun getMatchID(roundMatchIndex: Int) = properties.indexedMatchMap[roundMatchIndex]

    private fun registerObservable(observable: Observable<*>): Observable<*> {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit((this)) }
        return observable
    }

    fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    override fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.put(ROUND_PROPERTIES_KEY, properties.saveToNBT(nbt = CompoundTag()))
        return nbt
    }
    override fun loadFromNBT(nbt: CompoundTag): TournamentRound {
        properties.setFromNBT(nbt = nbt.getCompound(ROUND_PROPERTIES_KEY))
        return this
    }
    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not yet implemented") }
    override fun loadFromJSON(json: JsonObject): ClassStored { TODO("Not yet implemented") }

    override fun printProperties() = properties.logDebug()

    companion object {
        /** &#9888; Observables will be broken if [initialize] is not called after construction */
        fun loadFromNBT(nbt: CompoundTag): TournamentRound {
            return TournamentRound(
                RoundProperties.loadFromNBT(nbt = nbt.getCompound(ROUND_PROPERTIES_KEY))
            )
        }
    }

}
