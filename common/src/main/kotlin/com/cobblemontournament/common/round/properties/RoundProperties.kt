package com.cobblemontournament.common.round.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.round.RoundType
import com.sg8.collections.reactive.map.MutableObservableMap
import com.sg8.collections.reactive.map.mutableObservableMapOf
import com.sg8.properties.DefaultProperties
import net.minecraft.nbt.CompoundTag
import java.util.UUID


typealias IndexedMatchMap = MutableObservableMap<Int, UUID>


class RoundProperties(
    uuid: UUID = UUID.randomUUID(),
    tournamentID: UUID = UUID.randomUUID(),
    roundIndex: Int = DEFAULT_ROUND_INDEX,
    roundType: RoundType = DEFAULT_ROUND_TYPE,
    indexedMatchMap: IndexedMatchMap? = null,
) : DefaultProperties<RoundProperties> {

    override val instance: RoundProperties = this
    override val helper = RoundPropertiesHelper
    override val observable = SimpleObservable<RoundProperties>()
    private val subscriptionsMap: MutableMap<Observable<*>, ObservableSubscription<*>> = mutableMapOf()

    private val _uuid = SettableObservable(uuid).subscribe()
    private val _tournamentID = SettableObservable(tournamentID).subscribe()
    private val _roundIndex = SettableObservable(roundIndex).subscribe()
    private val _roundType = SettableObservable(roundType).subscribe()
    private var _indexedMatchMap = indexedMatchMap?.mutableCopy() ?: mutableObservableMapOf()

    val name: String get() = "Round $roundIndex [${roundType.name} type]"
    var uuid: UUID
        get() = _uuid.get()
        set(value) { _uuid.set(value) }
    var tournamentID: UUID
        get() = _tournamentID.get()
        set(value) { _tournamentID.set(value) }
    var roundIndex: Int
        get() = _roundIndex.get()
        set(value) { _roundIndex.set(value) }
    var roundType: RoundType
        get() = _roundType.get()
        set(value) { _roundType.set(value) }
    var indexedMatchMap: IndexedMatchMap
        get() = _indexedMatchMap
        set(value) { _indexedMatchMap = replaceSubscription(old = _indexedMatchMap, new = value) }


    init {
        _indexedMatchMap.subscribe()
    }

    private fun <T, O : Observable<T>> replaceSubscription(old: O, new: O): O {
        old.unsubscribe()
        return new.subscribe()
    }

    private fun <T, O : Observable<T>> O.subscribe(): O {
        subscriptionsMap[this] = this.subscribe { emitChange() }
        return this
    }

    private fun <T, O : Observable<T>> O.unsubscribe(): O {
        subscriptionsMap[this]?.unsubscribe()
        return this
    }

    override fun emitChange() = observable.emit(this)

    fun getMatchID(index: Int?): UUID? = index?.let { indexedMatchMap[it] }

    companion object {
        private val HELPER = RoundPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbt(nbt = nbt)
    }

}
