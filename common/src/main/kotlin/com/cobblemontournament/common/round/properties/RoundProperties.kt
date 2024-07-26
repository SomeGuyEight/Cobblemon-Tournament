package com.cobblemontournament.common.round.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.util.*
import com.cobblemontournament.common.round.RoundType
import com.someguy.storage.Properties
import com.someguy.storage.util.*
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class RoundProperties(
    roundID: RoundID = UUID.randomUUID(),
    tournamentID: TournamentID = UUID.randomUUID(),
    roundIndex: Int = DEFAULT_ROUND_INDEX,
    roundType: RoundType = DEFAULT_ROUND_TYPE,
    indexedMatchMap: IndexedMatchMap = mutableMapOf()
) : Properties<RoundProperties> {

    override val instance: RoundProperties = this
    override val helper = RoundPropertiesHelper

    // TODO handle with observable
    var indexedMatchMap: IndexedMatchMap = indexedMatchMap.toMutableMap()

    private val anyChangeObservable = SimpleObservable<RoundProperties>()
    private val subscriptionsMap: SubscriptionMap = mutableMapOf()

    private val roundIDObservable = registerObservable(SettableObservable(roundID))
    private val tournamentIDObservable = registerObservable(SettableObservable(tournamentID))
    private val roundIndexObservable = registerObservable(SettableObservable(roundIndex))
    private val roundTypeObservable = registerObservable(SettableObservable(roundType))

    var roundID: RoundID
        get() = roundIDObservable.get()
        set(value) { roundIDObservable.set(value) }
    var tournamentID: TournamentID
        get() = tournamentIDObservable.get()
        set(value) { tournamentIDObservable.set(value) }
    var roundIndex: Int
        get() = roundIndexObservable.get()
        set(value) { roundIndexObservable.set(value) }
    var roundType: RoundType
        get() = roundTypeObservable.get()
        set(value) { roundTypeObservable.set(value) }

    private fun <T, O : Observable<T>> registerObservable(observable: O): O {
        return observable.registerObservable(subscriptionsMap) { emitChange() }
    }

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getChangeObservable() = anyChangeObservable

    companion object {
        private val HELPER = RoundPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbtHelper(nbt = nbt)
    }

}
