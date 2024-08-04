package com.cobblemontournament.common.round

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.round.properties.RoundProperties
import com.google.gson.JsonObject
import com.sg8.storage.StoreCoordinates
import com.sg8.storage.TypeStored
import net.minecraft.nbt.CompoundTag
import java.util.UUID


open class TournamentRound(protected val properties: RoundProperties) : TypeStored {

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable(value = null)

    private val anyChangeObservable = SimpleObservable<TournamentRound>()

    override val name: String get() = properties.name
    override val uuid: UUID get() = properties.uuid
    val tournamentID: UUID get() = properties.tournamentID
    val roundIndex: Int get() = properties.roundIndex
    val roundType: RoundType get() = properties.roundType
    val matchMapSize: Int get() = properties.indexedMatchMap.size

    init {
        properties.observable.subscribe { emitChange() }
    }

    /** &#9888; (UUID) constructor is necessary for serialization method */
    constructor(roundUuid: UUID = UUID.randomUUID()) :
            this(RoundProperties(uuid = roundUuid))

    override fun initialize() = this

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getObservable(): Observable<TournamentRound> = anyChangeObservable

    fun getMatchID(roundMatchIndex: Int) = properties.indexedMatchMap[roundMatchIndex]

    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        nbt.put(DataKeys.ROUND_PROPERTIES, properties.saveToNbt(nbt = CompoundTag()))
        return nbt
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO() }

    override fun loadFromNBT(nbt: CompoundTag): TournamentRound {
        properties.setFromNbt(nbt = nbt.getCompound(DataKeys.ROUND_PROPERTIES))
        return this
    }

    override fun loadFromJSON(json: JsonObject): TypeStored { TODO() }

    fun deepCopy() = TournamentRound(properties.deepCopy())

    fun copy() = TournamentRound(properties.copy())

    override fun printProperties() = properties.printDebug()

    companion object {
        fun loadFromNbt(nbt: CompoundTag): TournamentRound {
            return TournamentRound(
                RoundProperties.loadFromNbt(nbt = nbt.getCompound(DataKeys.ROUND_PROPERTIES))
            )
        }
    }
}
