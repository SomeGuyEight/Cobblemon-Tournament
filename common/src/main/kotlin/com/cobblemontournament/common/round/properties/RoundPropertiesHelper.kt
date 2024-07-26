package com.cobblemontournament.common.round.properties

import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.util.*
import com.someguy.storage.util.*
import com.someguy.storage.PropertiesHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object RoundPropertiesHelper : PropertiesHelper<RoundProperties> {

    override fun setFromNbtHelper(mutable: RoundProperties, nbt: CompoundTag): RoundProperties {
        mutable.roundID = nbt.getUUID(ROUND_ID_KEY)
        mutable.tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.roundIndex = nbt.getInt(ROUND_INDEX_KEY)
        mutable.roundType = enumValueOf<RoundType>(nbt.getString(ROUND_TYPE_KEY))
        if (nbt.contains(ROUND_MATCH_INDEX_TO_ID_KEY)) {
            mutable.indexedMatchMap.putAll(loadIndexedMatchMapFromNBT(nbt = nbt))
        }
        return mutable
    }

    override fun deepCopyHelper(properties: RoundProperties): RoundProperties {
        return RoundProperties(
            roundID = properties.roundID,
            tournamentID = properties.tournamentID,
            roundIndex = properties.roundIndex,
            roundType = properties.roundType,
            indexedMatchMap = properties.indexedMatchMap.toMutableMap(),
        )
    }

    override fun saveToNbtHelper(properties: RoundProperties, nbt: CompoundTag): CompoundTag {
        nbt.putUUID(ROUND_ID_KEY, properties.roundID)
        nbt.putUUID(TOURNAMENT_ID_KEY, properties.tournamentID)
        nbt.putInt(ROUND_INDEX_KEY, properties.roundIndex)
        nbt.putString(ROUND_TYPE_KEY, properties.roundType.name)
        if (properties.indexedMatchMap.isNotEmpty()) {
            nbt.put(
                ROUND_MATCH_INDEX_TO_ID_KEY,
                saveIndexedMatchMapToNbt(
                    indexedMatchMap = properties.indexedMatchMap,
                    nbt = CompoundTag(),
                )
            )
        }
        return nbt
    }

    private fun saveIndexedMatchMapToNbt(
        indexedMatchMap: IndexedMatchMap,
        nbt: CompoundTag,
    ): CompoundTag {
        if (indexedMatchMap.isNotEmpty()) {
            var index = 0
            for ((roundMatchIndex, matchID) in indexedMatchMap) {
                nbt.putInt((ROUND_MATCH_INDEX_KEY + index), roundMatchIndex)
                nbt.putUUID((MATCH_ID_KEY + (index++)), matchID)
            }
            nbt.putInt(SIZE_KEY, indexedMatchMap.size)
        }
        return nbt
    }

    override fun loadFromNbtHelper(nbt: CompoundTag): RoundProperties {
        val map = if (nbt.contains(ROUND_MATCH_INDEX_TO_ID_KEY)) {
            loadIndexedMatchMapFromNBT(nbt = nbt.getCompound(ROUND_MATCH_INDEX_TO_ID_KEY))
        } else {
            mutableMapOf()
        }
        return RoundProperties(
            roundID = nbt.getUUID(ROUND_ID_KEY),
            tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY),
            roundIndex = nbt.getInt(ROUND_INDEX_KEY),
            roundType = enumValueOf<RoundType>(nbt.getString(ROUND_TYPE_KEY)),
            indexedMatchMap = map,
        )
    }

    private fun loadIndexedMatchMapFromNBT(nbt: CompoundTag): IndexedMatchMap {
        val indexedMatchMap = mutableMapOf<Int, UUID>()
        if (!nbt.contains(SIZE_KEY) || (nbt.getInt(SIZE_KEY) == 0)) {
            return indexedMatchMap
        }
        val size = nbt.getInt(SIZE_KEY)
        for (i in 0 until size) {
            val roundMatchIndex = nbt.getInt((ROUND_MATCH_INDEX_KEY + i))
            val matchID = nbt.getUUID((MATCH_ID_KEY + i))
            indexedMatchMap[roundMatchIndex] = matchID
        }
        return indexedMatchMap
    }

    override fun logDebugHelper(properties: RoundProperties) {
        Util.report("${properties.roundType} Round ${properties.roundIndex} " +
                "(${properties.indexedMatchMap.size} matches) " +
                "[${properties.roundID.shortUUID()}]")
        Util.report("- Tournament ID [${properties.tournamentID.shortUUID()}]")
    }

    override fun displayInChatHelper(properties: RoundProperties, player: ServerPlayer) {
        val propertiesComponent = getBracketedComponent(
            text = "${properties.roundType}",
            textColor = ChatUtil.GREEN_FORMAT,
        )
        propertiesComponent.appendWith(text = " \"")
        propertiesComponent.appendWith(text = "Round ", color = ChatUtil.GREEN_FORMAT)
        propertiesComponent.appendWithBracketed(
            text = "${properties.roundIndex}",
            textColor = ChatUtil.GREEN_FORMAT,
        )
        propertiesComponent.appendWith(text = "\" ")
        propertiesComponent.appendWithBracketed(
            text = properties.roundID.shortUUID(),
            textColor = ChatUtil.GREEN_FORMAT,
        )

        player.displayClientMessage(propertiesComponent, (false))

        val matchesComponent = getComponent(text = "  Total Matches ")
        matchesComponent.appendWithBracketed(
            text = "${properties.indexedMatchMap.size}",
            textColor = ChatUtil.YELLOW_FORMAT,
        )

        player.displayClientMessage(matchesComponent, (false))
    }

}
