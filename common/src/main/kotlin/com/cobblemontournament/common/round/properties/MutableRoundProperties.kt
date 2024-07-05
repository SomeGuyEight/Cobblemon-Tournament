package com.cobblemontournament.common.round.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.round.properties.RoundPropertiesHelper.DEFAULT_ROUND_INDEX
import com.cobblemontournament.common.round.properties.RoundPropertiesHelper.DEFAULT_ROUND_TYPE
import com.cobblemontournament.common.round.RoundType
import com.someguy.storage.properties.MutableProperties
import com.someguy.storage.properties.PropertiesCompanion
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MutableRoundProperties: RoundPropertyFields, MutableProperties<RoundPropertyFields,RoundProperties,MutableRoundProperties>
{
    companion object: PropertiesCompanion <RoundPropertyFields, RoundProperties, MutableRoundProperties> {
        override val helper = RoundPropertiesHelper
    }

    constructor (
        roundID             : UUID,
        tournamentID        : UUID,
        roundIndex          : Int,
        roundType           : RoundType = DEFAULT_ROUND_TYPE,
        indexedMatchMap     : MutableMap<Int,UUID>  = mutableMapOf()
    ) : super ()
    {
        this.roundID            = roundID
        this.tournamentID       = tournamentID
        this.roundIndex         = roundIndex
        this.roundType          = roundType
        this.indexedMatchMap.putAll( indexedMatchMap)
    }

    constructor() : this (
        roundID         = UUID.randomUUID(),
        tournamentID    = UUID.randomUUID(),
        roundIndex      = DEFAULT_ROUND_INDEX
    )

    override var roundID: UUID = UUID.randomUUID()
        set(value) { field = value; emitChange() }

    override var tournamentID: UUID = UUID.randomUUID()
        set(value) { field = value; emitChange() }

    override var roundIndex: Int = -1
        set(value) { field = value; emitChange() }

    override var roundType           : RoundType = DEFAULT_ROUND_TYPE
        set(value) { field = value; emitChange() }

    override var indexedMatchMap     : MutableMap<Int,UUID>  = mutableMapOf()
        set(value) { field = value; emitChange() }

    override fun getHelper() = RoundPropertiesHelper

    override fun deepCopy() = helper.deepCopyHelper(properties = this)

    override fun deepMutableCopy() = helper.deepMutableCopyHelper(properties = this)

    override fun setFromNBT(
        nbt: CompoundTag
    ): MutableRoundProperties {
        return helper.setFromNBTHelper( mutable = this, nbt = nbt)
    }

    override fun setFromProperties(
        from: RoundPropertyFields
    ): MutableRoundProperties {
        return helper.setFromPropertiesHelper(mutable = this, from = from)
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        return helper.saveToNBTHelper( properties = this, nbt = nbt)
    }

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<MutableRoundProperties>()

    private fun emitChange() = anyChangeObservable.emit(this)
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable(): Observable<MutableRoundProperties> = anyChangeObservable

    private fun <T> registerObservable(
        observable: SimpleObservable<T>
    ): SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

}
