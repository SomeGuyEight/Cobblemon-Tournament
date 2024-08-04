package com.cobblemontournament.common.match.connections

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.sg8.collections.reactive.map.mutableObservableMapOf
import com.sg8.properties.DefaultProperties
import net.minecraft.nbt.CompoundTag
import java.util.UUID


class MatchConnectionsProperties(
    victorNextMatch: UUID? = null,
    defeatedNextMatch: UUID? = null,
    previousMatchMap: PreviousMatchesMap? = null,
) : DefaultProperties<MatchConnectionsProperties> {

    override val instance = this
    override val helper = MatchConnectionsPropertiesHelper
    override val observable = SimpleObservable<MatchConnectionsProperties>()

    private val _victorNextMatch = SettableObservable(victorNextMatch).subscribe()
    private val _defeatedNextMatch = SettableObservable(defeatedNextMatch).subscribe()
    private val _previousMatchMap = previousMatchMap?.mutableCopy() ?: mutableObservableMapOf()

    var victorNextMatch: UUID?
        get() = _victorNextMatch.get()
        set(value) { _victorNextMatch.set(value) }
    var defeatedNextMatch: UUID?
        get() = _defeatedNextMatch.get()
        set(value) { _defeatedNextMatch.set(value) }
    val previousMatchMap get() = _previousMatchMap

    init {
        _previousMatchMap.subscribe()
    }

    private fun <T, O : Observable<T>> O.subscribe(): O {
        this.subscribe { emitChange() }
        return this
    }

    override fun emitChange() = observable.emit(this)

    companion object {
        private val HELPER = MatchConnectionsPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbt(nbt)
    }
}
