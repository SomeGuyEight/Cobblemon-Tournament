package com.cobblemontournament.common.match

import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.util.StoreDataKeys
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MatchConnections
{
    constructor(
        victorNextMatch: UUID? = null,
        defeatedNextMatch: UUID? = null,
        previousMatches: MutableMap<Int, UUID> = mutableMapOf())
    {
        this.victorNextMatch = victorNextMatch
        this.defeatedNextMatch = defeatedNextMatch
        this.previousMatchesMap.putAll( previousMatches )
    }

    var victorNextMatch: UUID? = null
        set(value) { field = value; emitChange() }

    var defeatedNextMatch: UUID? = null
        set(value) { field = value; emitChange() }

    private val previousMatchesMap: MutableMap<Int,UUID> = mutableMapOf()

    fun addPrevious(
        matchIndex: Int,
        matchID: UUID,
    ) {
        previousMatchesMap[matchIndex] = matchID
        emitChange()
    }

    fun deepCopy() = MatchConnections( victorNextMatch, defeatedNextMatch, TournamentUtil.shallowCopy( previousMatchesMap ))

    fun setFromConnections(
        connections: MatchConnections
    ): MatchConnections
    {
        this.victorNextMatch = connections.victorNextMatch
        this.defeatedNextMatch = connections.defeatedNextMatch
        this.previousMatchesMap.putAll( connections.previousMatchesMap )
        return this
    }

    fun setFromNBT(
        nbt: CompoundTag
    ): MatchConnections
    {
        previousMatchesMap.clear()
        if (nbt.contains(DataKeys.VICTOR_NEXT_MATCH)) {
            victorNextMatch = nbt.getUUID( DataKeys.VICTOR_NEXT_MATCH)
        }
        if (nbt.contains(DataKeys.DEFEATED_NEXT_MATCH)) {
            defeatedNextMatch = nbt.getUUID( DataKeys.DEFEATED_NEXT_MATCH)
        }
        if (nbt.contains(StoreDataKeys.SIZE) && nbt.getInt(StoreDataKeys.SIZE) != 0) {
            val size = nbt.getInt(StoreDataKeys.SIZE)
            for (i in 0 until size) {
                val teamIndex   = nbt.getInt(DataKeys.TEAM_INDEX + i)
                val matchID     = nbt.getUUID(DataKeys.MATCH_ID + i)
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
            nbt.putUUID( DataKeys.VICTOR_NEXT_MATCH, victorNextMatch!! )
        }
        if (defeatedNextMatch != null) {
            nbt.putUUID( DataKeys.DEFEATED_NEXT_MATCH, defeatedNextMatch!! )
        }
        var size = 0
        for ((teamIndex, matchID) in previousMatchesMap) {
            nbt.putInt(DataKeys.TEAM_INDEX + size, teamIndex)
            nbt.putUUID(DataKeys.MATCH_ID + size++, matchID)
        }
        nbt.putInt(StoreDataKeys.SIZE, size)
        return nbt
    }

    private fun emitChange() = anyChangeObservable.emit( values = arrayOf( this ) )
    private val anyChangeObservable = SimpleObservable<MatchConnections>()
    fun getChangeObservable() = anyChangeObservable

}
