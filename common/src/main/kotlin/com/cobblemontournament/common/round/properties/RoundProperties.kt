package com.cobblemontournament.common.round.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.round.properties.RoundPropertiesHelper.DEFAULT_ROUND_INDEX
import com.cobblemontournament.common.round.properties.RoundPropertiesHelper.DEFAULT_ROUND_TYPE
import com.cobblemontournament.common.round.RoundType
import com.someguy.storage.properties.Properties
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class RoundProperties(
    roundID: UUID,
    tournamentID: UUID,
    roundIndex: Int,
    roundType: RoundType = DEFAULT_ROUND_TYPE,
    indexedMatchMap: MutableMap<Int, UUID> = mutableMapOf()
) : Properties<RoundProperties> {

    override val instance = this
    var roundID: UUID = UUID.randomUUID()
        set(value) {
            field = value
            emitChange()
        }
    var tournamentID: UUID = UUID.randomUUID()
        set(value) {
            field = value
            emitChange()
        }
    var roundIndex: Int = -1
        set(value) {
            field = value
            emitChange()
        }
    var roundType: RoundType = DEFAULT_ROUND_TYPE
        set(value) {
            field = value
            emitChange()
        }
    var indexedMatchMap: MutableMap<Int, UUID> = mutableMapOf()
        set(value) {
            field = value
            emitChange()
        }

    override val helper = RoundPropertiesHelper
    private val observables = mutableListOf <Observable <*>>()
    val anyChangeObservable = SimpleObservable <RoundProperties>()

    init {
        this.roundID = roundID
        this.tournamentID = tournamentID
        this.roundIndex = roundIndex
        this.roundType = roundType
        this.indexedMatchMap.putAll(indexedMatchMap)
    }

    constructor(uuid: UUID = UUID.randomUUID()) : this(
        roundID = uuid,
        tournamentID = UUID.randomUUID(),
        roundIndex = DEFAULT_ROUND_INDEX,
    )

    private fun emitChange() = anyChangeObservable.emit((this))

    override fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable() = anyChangeObservable

    companion object {
        private val HELPER = RoundPropertiesHelper
        /** Returns a new RoundProperties instance loaded from the CompoundTag */
        fun loadFromNBT(nbt: CompoundTag) = HELPER.loadFromNBTHelper(nbt = nbt)
    }

}
