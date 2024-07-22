package com.cobblemontournament.common.round.properties

import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MATCH_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_INDEX_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_MATCH_INDEX_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_TYPE_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_ID_KEY
import com.cobblemontournament.common.util.ChatUtil
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object RoundPropertiesHelper : PropertiesHelper<RoundProperties> {

    const val DEFAULT_ROUND_INDEX = -1
    val DEFAULT_ROUND_TYPE = RoundType.NONE

    override fun deepCopyHelper(properties: RoundProperties): RoundProperties {
        return RoundProperties(
            roundID = properties.roundID,
            tournamentID = properties.tournamentID,
            roundIndex = properties.roundIndex,
            roundType = properties.roundType,
            indexedMatchMap = TournamentUtil.shallowCopy(map = properties.indexedMatchMap),
        )
    }

    override fun setFromPropertiesHelper(
        mutable: RoundProperties,
        from: RoundProperties,
    ): RoundProperties {
        mutable.roundID = from.roundID
        mutable.tournamentID = from.tournamentID
        mutable.roundIndex = from.roundIndex
        mutable.roundType = from.roundType
        mutable.indexedMatchMap = TournamentUtil.shallowCopy(map = from.indexedMatchMap)
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: RoundProperties,
        nbt: CompoundTag,
    ): RoundProperties {
        mutable.roundID = nbt.getUUID(ROUND_ID_KEY)
        mutable.tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.roundIndex = nbt.getInt(ROUND_INDEX_KEY)
        mutable.roundType = enumValueOf<RoundType>(nbt.getString(ROUND_TYPE_KEY))
        if (nbt.contains(ROUND_MATCH_INDEX_TO_ID_KEY)) {
            mutable.indexedMatchMap.putAll(loadIndexedMatchMapFromNBT(nbt = nbt))
        }
        return mutable
    }

    override fun saveToNBTHelper(
        properties: RoundProperties,
        nbt: CompoundTag,
    ): CompoundTag {
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
        indexedMatchMap: Map<Int, UUID>,
        nbt: CompoundTag,
    ): CompoundTag {
        if (indexedMatchMap.isNotEmpty()) {
            var index = 0
            for ((roundMatchIndex, matchID) in indexedMatchMap) {
                nbt.putInt((ROUND_MATCH_INDEX_KEY + index), roundMatchIndex)
                nbt.putUUID((MATCH_ID_KEY + (index++)), matchID)
            }
            nbt.putInt(StoreDataKeys.SIZE, indexedMatchMap.size)
        }
        return nbt
    }

    override fun loadFromNBTHelper(nbt: CompoundTag): RoundProperties {
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

    private fun loadIndexedMatchMapFromNBT(
        nbt: CompoundTag
    ): MutableMap<Int, UUID> {
        val indexedMatchMap = mutableMapOf<Int, UUID>()
        if (!nbt.contains(StoreDataKeys.SIZE) || (nbt.getInt(StoreDataKeys.SIZE) == 0)) {
            return indexedMatchMap
        }
        val size = nbt.getInt(StoreDataKeys.SIZE)
        for (i in 0 until size) {
            val roundMatchIndex = nbt.getInt((ROUND_MATCH_INDEX_KEY + i))
            val matchID = nbt.getUUID((MATCH_ID_KEY + i))
            indexedMatchMap[roundMatchIndex] = matchID
        }
        return indexedMatchMap
    }

    override fun logDebugHelper(properties: RoundProperties) {
        Util.report(("${properties.roundType} Round ${properties.roundIndex} (${properties.indexedMatchMap.size} matches) [${ChatUtil.shortUUID(uuid = properties.roundID)}]"))
        Util.report(("- Tournament ID [${ChatUtil.shortUUID(uuid = properties.tournamentID)}]"))
    }

    override fun displayInChatHelper(properties: RoundProperties, player: ServerPlayer) {
        val propertiesText = ChatUtil.formatTextBracketed(
            text = "${properties.roundType}",
            color = ChatUtil.green,
        )
        propertiesText.append(ChatUtil.formatText(text = " \""))
        propertiesText.append(ChatUtil.formatText(
            text = "Round ",
            color = ChatUtil.green,
        ))
        propertiesText.append(ChatUtil.formatTextBracketed(
            text = "${properties.roundIndex}",
            color = ChatUtil.green,
        ))
        propertiesText.append(ChatUtil.formatText(text = "\" "))
        propertiesText.append(ChatUtil.formatTextBracketed(
            text = ChatUtil.shortUUID(uuid = properties.roundID),
            color = ChatUtil.green,
        ))

        val matchesText = ChatUtil.formatText(
            text = "  Total Matches ",
        )
        matchesText.append(ChatUtil.formatTextBracketed(
            text = "${properties.indexedMatchMap.size}",
            color = ChatUtil.yellow,
        ))

        player.displayClientMessage(propertiesText, (false))
        player.displayClientMessage(matchesText, (false))
    }

}
