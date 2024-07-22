package com.cobblemontournament.common.match

import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.storage.TournamentDataKeys.DEFEATED_NEXT_MATCH_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MATCH_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TEAM_INDEX_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.VICTOR_NEXT_MATCH_KEY
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.util.StoreDataKeys
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MatchConnections(
    victorNextMatch: UUID? = null,
    defeatedNextMatch: UUID? = null,
    previousMatches: MutableMap<Int, UUID> = mutableMapOf()
) {

    var victorNextMatch: UUID? = null
        set(value) {
            field = value
            emitChange()
        }
    var defeatedNextMatch: UUID? = null
        set(value) {
            field = value
            emitChange()
        }
    private val previousMatchesMap: MutableMap<Int,UUID> = mutableMapOf()

    init {
        this.victorNextMatch = victorNextMatch
        this.defeatedNextMatch = defeatedNextMatch
        this.previousMatchesMap.putAll(previousMatches)
    }

    fun deepCopy(): MatchConnections {
        return MatchConnections(
            victorNextMatch = victorNextMatch,
            defeatedNextMatch = defeatedNextMatch,
            previousMatches = TournamentUtil.shallowCopy(map = previousMatchesMap)
        )
    }

    fun setFromConnections(connections: MatchConnections): MatchConnections {
        this.victorNextMatch = connections.victorNextMatch
        this.defeatedNextMatch = connections.defeatedNextMatch
        this.previousMatchesMap.putAll(connections.previousMatchesMap)
        return this
    }

    fun setFromNBT(nbt: CompoundTag): MatchConnections {
        previousMatchesMap.clear()
        if (nbt.contains(VICTOR_NEXT_MATCH_KEY)) {
            victorNextMatch = nbt.getUUID(VICTOR_NEXT_MATCH_KEY)
        }
        if (nbt.contains(DEFEATED_NEXT_MATCH_KEY)) {
            defeatedNextMatch = nbt.getUUID(DEFEATED_NEXT_MATCH_KEY)
        }
        if (nbt.contains(StoreDataKeys.SIZE) && (nbt.getInt(StoreDataKeys.SIZE) != 0)) {
            val size = nbt.getInt(StoreDataKeys.SIZE)
            for (i in 0 until size) {
                val teamIndex = nbt.getInt((TEAM_INDEX_KEY + i))
                val matchID = nbt.getUUID((MATCH_ID_KEY + i))
                previousMatchesMap[teamIndex] = matchID
            }
        }
        return this
    }

    fun saveToNBT(
        nbt : CompoundTag
    ): CompoundTag
    {
        if (victorNextMatch != null) {
            nbt.putUUID( VICTOR_NEXT_MATCH_KEY, victorNextMatch!! )
        }
        if (defeatedNextMatch != null) {
            nbt.putUUID( DEFEATED_NEXT_MATCH_KEY, defeatedNextMatch!! )
        }
        var size = 0
        for ((teamIndex, matchID) in previousMatchesMap) {
            nbt.putInt(TEAM_INDEX_KEY + size, teamIndex)
            nbt.putUUID(MATCH_ID_KEY + size++, matchID)
        }
        nbt.putInt(StoreDataKeys.SIZE, size)
        return nbt
    }

    fun addPrevious(matchIndex: Int, matchID: UUID) {
        previousMatchesMap[matchIndex] = matchID
        emitChange()
    }

    private fun emitChange() = anyChangeObservable.emit((this))
    private val anyChangeObservable = SimpleObservable<MatchConnections>()
    fun getChangeObservable() = anyChangeObservable

}
