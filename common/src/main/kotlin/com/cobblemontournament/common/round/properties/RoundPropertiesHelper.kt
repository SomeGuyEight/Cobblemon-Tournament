package com.cobblemontournament.common.round.properties

import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.util.ChatUtil
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object RoundPropertiesHelper: PropertiesHelper<RoundProperties>
{
    const val DEFAULT_ROUND_INDEX   = -1
    val DEFAULT_ROUND_TYPE          = RoundType.NONE

    override fun deepCopyHelper(
        properties: RoundProperties
    ): RoundProperties {
        return RoundProperties(
            roundID         = properties.roundID,
            tournamentID    = properties.tournamentID,
            roundIndex      = properties.roundIndex,
            roundType       = properties.roundType,
            indexedMatchMap = TournamentUtil.copy( properties.indexedMatchMap )
        )
    }

    override fun setFromPropertiesHelper(
        mutable: RoundProperties,
        from: RoundProperties,
    ): RoundProperties {
        mutable.roundID         = from.roundID
        mutable.tournamentID    = from.tournamentID
        mutable.roundIndex      = from.roundIndex
        mutable.roundType       = from.roundType
        mutable.indexedMatchMap = TournamentUtil.copy( from.indexedMatchMap )
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: RoundProperties,
        nbt: CompoundTag
    ): RoundProperties
    {
        mutable.roundID         = nbt.getUUID(DataKeys.ROUND_ID)
        mutable.tournamentID    = nbt.getUUID(DataKeys.TOURNAMENT_ID)
        mutable.roundIndex      = nbt.getInt(DataKeys.ROUND_INDEX)
        mutable.roundType       = enumValueOf<RoundType>(nbt.getString(DataKeys.ROUND_TYPE))
        if (nbt.contains(DataKeys.ROUND_MATCH_INDEX_TO_ID)) {
            mutable.indexedMatchMap.putAll(loadIndexedMatchMapFromNBT(nbt))
        }
        return mutable
    }

    override fun saveToNBTHelper(
        properties: RoundProperties,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putUUID(    DataKeys.ROUND_ID         , properties.roundID)
        nbt.putUUID(    DataKeys.TOURNAMENT_ID    , properties.tournamentID)
        nbt.putInt(     DataKeys.ROUND_INDEX      , properties.roundIndex)
        nbt.putString(  DataKeys.ROUND_TYPE       , properties.roundType.toString())
        if (properties.indexedMatchMap.isNotEmpty()) {
            nbt.put(
                DataKeys.ROUND_MATCH_INDEX_TO_ID,
                saveIndexedMatchMapToNBT( properties.indexedMatchMap, CompoundTag())
            )
        }
        return nbt
    }

    private fun saveIndexedMatchMapToNBT(
        indexedMatchMap: Map<Int, UUID>,
        nbt: CompoundTag
    ): CompoundTag
    {
        if (indexedMatchMap.isEmpty()) {
            return nbt
        }
        var index = 0
        for ((roundMatchIndex,matchID) in indexedMatchMap) {
            nbt.putInt(DataKeys.ROUND_MATCH_INDEX + index     , roundMatchIndex)
            nbt.putUUID(DataKeys.MATCH_ID         + index++   , matchID)
        }
        nbt.putInt(StoreDataKeys.SIZE, indexedMatchMap.size)
        return nbt
    }

    override fun loadFromNBTHelper(
        nbt: CompoundTag
    ): RoundProperties
    {
        val map = if (nbt.contains(DataKeys.ROUND_MATCH_INDEX_TO_ID)) {
            loadIndexedMatchMapFromNBT(nbt.getCompound(DataKeys.ROUND_MATCH_INDEX_TO_ID))
        } else {
            mutableMapOf()
        }
        return RoundProperties(
            roundID         = nbt.getUUID(DataKeys.ROUND_ID),
            tournamentID    = nbt.getUUID(DataKeys.TOURNAMENT_ID),
            roundIndex      = nbt.getInt(DataKeys.ROUND_INDEX),
            roundType       = enumValueOf<RoundType>(nbt.getString(DataKeys.ROUND_TYPE)),
            indexedMatchMap = map
        )
    }

    private fun loadIndexedMatchMapFromNBT(
        nbt: CompoundTag
    ): MutableMap<Int, UUID>
    {
        val indexedMatchMap = mutableMapOf<Int, UUID>()
        if (!nbt.contains(StoreDataKeys.SIZE) || nbt.getInt(StoreDataKeys.SIZE) == 0) {
            return indexedMatchMap
        }
        val size = nbt.getInt(StoreDataKeys.SIZE)
        for (i in 0 until size) {
            val roundMatchIndex     = nbt.getInt(   DataKeys.ROUND_MATCH_INDEX + i)
            val matchID             = nbt.getUUID(  DataKeys.MATCH_ID + i)
            indexedMatchMap[roundMatchIndex] = matchID
        }
        return indexedMatchMap
    }

    override fun logDebugHelper( properties: RoundProperties )
    {
        Util.report("${properties.roundType} Round ${properties.roundIndex} " +
                "(${properties.indexedMatchMap.size} matches) [${ChatUtil.shortUUID( properties.roundID )}]")
        Util.report("- Tournament ID [${ChatUtil.shortUUID( properties.tournamentID )}]")
    }

    override fun displayInChatHelper(
        properties: RoundProperties,
        player: ServerPlayer)
    {
        val text0 = ChatUtil.formatTextBracketed(
            text    = "${properties.roundType}",
            color   = ChatUtil.green )
        text0.append(ChatUtil.formatText( text = " \"" ) )
        text0.append(ChatUtil.formatText(
            text    = "Round ",
            color   = ChatUtil.green ) )
        text0.append( ChatUtil.formatTextBracketed(
            text    = "${properties.roundIndex}",
            color   = ChatUtil.green ) )
        text0.append(ChatUtil.formatText( text = "\" " ) )
        text0.append( ChatUtil.formatTextBracketed(
            text    = ChatUtil.shortUUID( properties.roundID ),
            color   = ChatUtil.green ) )
        val text1 = ChatUtil.formatText(
            text    = "  Total Matches " )
        text1.append( ChatUtil.formatTextBracketed(
            text    = "${properties.indexedMatchMap.size}",
            color   = ChatUtil.yellow ) )
        player.displayClientMessage( text0,false )
        player.displayClientMessage( text1,false )
    }


}
