package com.cobblemontournament.common.match.connections

import com.cobblemontournament.common.api.storage.DataKeys
import com.sg8.collections.reactive.map.loadMutableObservableMapOf
import com.sg8.collections.reactive.map.saveToNbt
import com.sg8.properties.PropertiesHelper
import com.sg8.util.getUuidOrNull
import com.sg8.util.putIfNotNull
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID
import kotlin.collections.Map.Entry


object MatchConnectionsPropertiesHelper : PropertiesHelper<MatchConnectionsProperties> {

    override fun saveToNbt(
        properties: MatchConnectionsProperties,
        nbt: CompoundTag,
    ): CompoundTag {
        nbt.putIfNotNull(DataKeys.VICTOR_NEXT_MATCH, properties.victorNextMatch)
        nbt.putIfNotNull(DataKeys.DEFEATED_NEXT_MATCH, properties.defeatedNextMatch)
        nbt.putPreviousMatchMap(properties)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): MatchConnectionsProperties {
        return MatchConnectionsProperties(
            victorNextMatch = nbt.getUuidOrNull(DataKeys.VICTOR_NEXT_MATCH),
            defeatedNextMatch = nbt.getUuidOrNull(DataKeys.DEFEATED_NEXT_MATCH),
            previousMatchMap = nbt.getPreviousMatchMap(),
        )
    }

    override fun setFromNbt(
        mutable: MatchConnectionsProperties,
        nbt: CompoundTag,
    ): MatchConnectionsProperties {
        mutable.victorNextMatch = nbt.getUuidOrNull(DataKeys.VICTOR_NEXT_MATCH)
        mutable.defeatedNextMatch = nbt.getUuidOrNull(DataKeys.DEFEATED_NEXT_MATCH)
        mutable.previousMatchMap.clear()
        mutable.previousMatchMap.putAll(nbt.getPreviousMatchMap())
        return mutable
    }

    private fun CompoundTag.putPreviousMatchMap(properties: MatchConnectionsProperties) {
        val entryHandler = { (index, matchID): Entry<Int, UUID> ->
            CompoundTag().also { nbt ->
                nbt.putInt(DataKeys.TEAM_INDEX, index)
                nbt.putUUID(DataKeys.MATCH_ID, matchID)
            }
        }
        val mapNbt = properties.previousMatchMap.saveToNbt(entryHandler)
        this.put(DataKeys.PREVIOUS_MATCH_MAP, mapNbt)
    }

    private fun CompoundTag.getPreviousMatchMap(): MutablePreviousMatchesMap {
        val mapNbt = this.getCompound(DataKeys.PREVIOUS_MATCH_MAP)
        val entryHandler = { nbt: CompoundTag ->
            nbt.getInt(DataKeys.TEAM_INDEX) to nbt.getUUID(DataKeys.MATCH_ID)
        }
        return mapNbt.loadMutableObservableMapOf(entryHandler)
    }

    override fun deepCopy(properties: MatchConnectionsProperties) = copy(properties)

    override fun copy(properties: MatchConnectionsProperties): MatchConnectionsProperties {
        return MatchConnectionsProperties(
            victorNextMatch = properties.victorNextMatch,
            defeatedNextMatch = properties.defeatedNextMatch,
            previousMatchMap = properties.previousMatchMap,
        )
    }

    override fun printDebug(properties: MatchConnectionsProperties) {
        TODO("Not yet implemented")
    }

    override fun displayInChat(properties: MatchConnectionsProperties, player: ServerPlayer) {
        TODO("Not yet implemented")
    }
}
