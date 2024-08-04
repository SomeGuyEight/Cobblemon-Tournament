package com.cobblemontournament.common.match.connections

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.storage.DataKeys
import com.sg8.collections.reactive.map.MutableObservableMap
import com.sg8.collections.reactive.map.ObservableMap
import net.minecraft.nbt.CompoundTag
import java.util.UUID


typealias PreviousMatchesMap = ObservableMap<Int, UUID>
typealias MutablePreviousMatchesMap = MutableObservableMap<Int, UUID>


class MatchConnections(properties: MatchConnectionsProperties? = null) {

    val observable = SimpleObservable<MatchConnections>()

    private val properties = properties?.deepCopy() ?: MatchConnectionsProperties()

    var victorNextMatch: UUID?
        get() = properties.victorNextMatch
        set(value) { properties.victorNextMatch = value }
    var defeatedNextMatch: UUID?
        get() = properties.defeatedNextMatch
        set(value) { properties.defeatedNextMatch = value }
    val previousMatchesMap: PreviousMatchesMap get() = properties.previousMatchMap

    init {
        registerObservable(this.properties.observable)
    }

    private fun <T, O : Observable<T>> registerObservable(observable: O): O {
        observable.subscribe { emitChange() }
        return observable
    }

    private fun emitChange() = observable.emit(this)

    fun getObservable(): Observable<MatchConnections> = observable

    fun addPreviousMatch(matchIndex: Int?, matchID: UUID?): Boolean {
        if (matchIndex != null && matchID != null) {
            properties.previousMatchMap[matchIndex] = matchID
            return true
        }
        return false
    }

    fun setFromNbt(nbt: CompoundTag): MatchConnections {
        properties.setFromNbt(nbt)
        return this
    }

    fun saveToNbt(nbt: CompoundTag) = properties.saveToNbt(nbt = nbt)

    fun deepCopy() = MatchConnections(properties)

    companion object {
        fun loadFromNbt(nbt: CompoundTag) = MatchConnections(
            MatchConnectionsProperties.loadFromNbt(
                nbt.getCompound(DataKeys.MATCH_CONNECTIONS_PROPERTIES)
            )
        )
    }
}
