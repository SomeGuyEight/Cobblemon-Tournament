package com.cobblemontournament.common.round

import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.round.properties.RoundProperties
import com.cobblemontournament.common.util.*
import com.someguy.storage.ClassStored
import com.google.gson.JsonObject
import com.someguy.storage.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import java.util.UUID

open class TournamentRound(protected val properties: RoundProperties) : ClassStored {

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable(value = null)

    private val anyChangeObservable = SimpleObservable<TournamentRound>()

    override val name: String get() = "Round $roundIndex [${roundType.name} type]"
    override var uuid: RoundID
        get() = properties.roundID
        protected set(value) { properties.roundID = value }
    val tournamentID: TournamentID get() = properties.tournamentID
    val roundIndex: Int get() = properties.roundIndex
    val roundType: RoundType get() = properties.roundType
    val matchMapSize: Int get() = properties.indexedMatchMap.size

    init {
        properties.getChangeObservable().subscribe { emitChange() }
    }

    /** &#9888; (UUID) constructor is needed for serialization method */
    constructor(roundID: RoundID = UUID.randomUUID()) : this(RoundProperties(roundID = roundID))

    override fun initialize() = this

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getChangeObservable() = anyChangeObservable

    fun getMatchID(roundMatchIndex: Int) = properties.indexedMatchMap[roundMatchIndex]

    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        nbt.put(ROUND_PROPERTIES_KEY, properties.saveToNbt(nbt = CompoundTag()))
        return nbt
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO() }

    override fun loadFromNBT(nbt: CompoundTag): TournamentRound {
        properties.setFromNbt(nbt = nbt.getCompound(ROUND_PROPERTIES_KEY))
        return this
    }

    override fun loadFromJSON(json: JsonObject): ClassStored { TODO() }

    override fun printProperties() = properties.logDebug()

    companion object {
        fun loadFromNbt(nbt: CompoundTag): TournamentRound {
            return TournamentRound(
                RoundProperties.loadFromNbt(nbt = nbt.getCompound(ROUND_PROPERTIES_KEY))
            )
        }
    }

}
