package com.cobblemontournament.common.match

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.util.*
import com.someguy.storage.util.SIZE_KEY
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MatchConnections(
    victorNextMatch: UUID? = null,
    defeatedNextMatch: UUID? = null,
    previousMatches: MutableMap<Int, UUID> = mutableMapOf()
) {

    private val previousMatchesMap: MutableMap<Int,UUID> = previousMatches.toMutableMap()

    private val anyChangeObservable = SimpleObservable<MatchConnections>()

    private val victorNextMatchObservable =
        registerObservable(SettableObservable(victorNextMatch))
    private val defeatedNextMatchObservable =
        registerObservable(SettableObservable(defeatedNextMatch))

    var victorNextMatch: UUID?
        get() = victorNextMatchObservable.get()
        set(value) { victorNextMatchObservable.set(value) }
    var defeatedNextMatch: UUID?
        get() = defeatedNextMatchObservable.get()
        set(value) { defeatedNextMatchObservable.set(value) }

    private fun <T, O : Observable<T>> registerObservable(observable: O): O {
        observable.subscribe { emitChange() }
        return observable
    }

    private fun emitChange() = anyChangeObservable.emit(this)

    fun getChangeObservable() = anyChangeObservable

    fun setFromNbt(nbt: CompoundTag): MatchConnections {
        previousMatchesMap.clear()
        if (nbt.contains(VICTOR_NEXT_MATCH_KEY)) {
            victorNextMatch = nbt.getUUID(VICTOR_NEXT_MATCH_KEY)
        }
        if (nbt.contains(DEFEATED_NEXT_MATCH_KEY)) {
            defeatedNextMatch = nbt.getUUID(DEFEATED_NEXT_MATCH_KEY)
        }
        if (nbt.contains(SIZE_KEY) && (nbt.getInt(SIZE_KEY) != 0)) {
            val size = nbt.getInt(SIZE_KEY)
            for (i in 0 until size) {
                val teamIndex = nbt.getInt((TEAM_INDEX_KEY + i))
                previousMatchesMap[teamIndex] = nbt.getUUID((MATCH_ID_KEY + i))
            }
        }
        return this
    }

    fun deepCopy(): MatchConnections {
        return MatchConnections(
            victorNextMatch = victorNextMatch,
            defeatedNextMatch = defeatedNextMatch,
            previousMatches = previousMatchesMap,
        )
    }

    fun saveToNbt(nbt: CompoundTag): CompoundTag {
        if (victorNextMatch != null) {
            nbt.putUUID(VICTOR_NEXT_MATCH_KEY, victorNextMatch!!)
        }
        if (defeatedNextMatch != null) {
            nbt.putUUID(DEFEATED_NEXT_MATCH_KEY, defeatedNextMatch!!)
        }
        var size = 0
        for ((teamIndex, matchID) in previousMatchesMap) {
            nbt.putInt(TEAM_INDEX_KEY + size, teamIndex)
            nbt.putUUID(MATCH_ID_KEY + (size++), matchID)
        }
        nbt.putInt(SIZE_KEY, size)
        return nbt
    }

    fun addPrevious(matchIndex: Int, matchID: UUID) {
        previousMatchesMap[matchIndex] = matchID
        emitChange()
    }

}
