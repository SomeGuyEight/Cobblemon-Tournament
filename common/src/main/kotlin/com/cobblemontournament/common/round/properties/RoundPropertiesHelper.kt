package com.cobblemontournament.common.round.properties

import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.util.TournamentDataKeys
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import net.minecraft.nbt.CompoundTag
import java.util.UUID

object RoundPropertiesHelper: PropertiesHelper<RoundPropertyFields, RoundProperties, MutableRoundProperties>
{
    const val DEFAULT_ROUND_INDEX   = -1
    val DEFAULT_ROUND_TYPE          = RoundType.NONE

    override fun deepCopyHelper(
        properties: RoundPropertyFields
    ): RoundProperties
    {
        return RoundProperties(
            roundID         = properties.roundID,
            tournamentID    = properties.tournamentID,
            roundIndex      = properties.roundIndex,
            roundType       = properties.roundType,
            indexedMatchMap = deepCopy(properties.indexedMatchMap)
        )
    }

    override fun deepMutableCopyHelper(
        properties: RoundPropertyFields
    ): MutableRoundProperties
    {
        return MutableRoundProperties(
            roundID         = properties.roundID,
            tournamentID    = properties.tournamentID,
            roundIndex      = properties.roundIndex,
            roundType       = properties.roundType,
            indexedMatchMap = deepCopy(properties.indexedMatchMap)
        )
    }

    override fun setFromPropertiesHelper(
        mutable: MutableRoundProperties,
        from: RoundPropertyFields,
    ): MutableRoundProperties
    {
        mutable.roundID         = from.roundID
        mutable.tournamentID    = from.tournamentID
        mutable.roundIndex      = from.roundIndex
        mutable.roundType       = from.roundType
        mutable.indexedMatchMap = deepCopy(from.indexedMatchMap)
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: MutableRoundProperties,
        nbt: CompoundTag
    ): MutableRoundProperties
    {
        mutable.roundID         = nbt.getUUID(TournamentDataKeys.ROUND_ID)
        mutable.tournamentID    = nbt.getUUID(TournamentDataKeys.TOURNAMENT_ID)
        mutable.roundIndex      = nbt.getInt(TournamentDataKeys.ROUND_INDEX)
        mutable.roundType       = enumValueOf<RoundType>(nbt.getString(TournamentDataKeys.ROUND_TYPE))
        if (nbt.contains(TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID)) {
            mutable.indexedMatchMap.putAll(loadIndexedMatchMapFromNBT(nbt))
        }
        return mutable
    }

    override fun saveToNBTHelper(
        properties: RoundPropertyFields,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putUUID(    TournamentDataKeys.ROUND_ID         , properties.roundID)
        nbt.putUUID(    TournamentDataKeys.TOURNAMENT_ID    , properties.tournamentID)
        nbt.putInt(     TournamentDataKeys.ROUND_INDEX      , properties.roundIndex)
        nbt.putString(  TournamentDataKeys.ROUND_TYPE       , properties.roundType.toString())
        if (properties.indexedMatchMap.isNotEmpty()) {
            nbt.put(
                TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID,
                saveIndexedMatchMapToNBT( properties.indexedMatchMap, CompoundTag())
            )
        }
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ): RoundProperties
    {
        val map = if (nbt.contains(TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID)) {
            loadIndexedMatchMapFromNBT(nbt.getCompound(TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID))
        } else {
            mutableMapOf()
        }
        return RoundProperties(
            roundID         = nbt.getUUID(TournamentDataKeys.ROUND_ID),
            tournamentID    = nbt.getUUID(TournamentDataKeys.TOURNAMENT_ID),
            roundIndex      = nbt.getInt(TournamentDataKeys.ROUND_INDEX),
            roundType       = enumValueOf<RoundType>(nbt.getString(TournamentDataKeys.ROUND_TYPE)),
            indexedMatchMap = map
        )
    }

    override fun loadMutableFromNBT(
        nbt: CompoundTag)
    : MutableRoundProperties
    {
        val map = if (nbt.contains(TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID)) {
            loadIndexedMatchMapFromNBT(nbt.getCompound(TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID))
        } else {
            mutableMapOf()
        }
        return MutableRoundProperties(
            roundID         = nbt.getUUID(TournamentDataKeys.ROUND_ID),
            tournamentID    = nbt.getUUID(TournamentDataKeys.TOURNAMENT_ID),
            roundIndex      = nbt.getInt(TournamentDataKeys.ROUND_INDEX),
            roundType       = enumValueOf<RoundType>(nbt.getString(TournamentDataKeys.ROUND_TYPE)),
            indexedMatchMap = map
        )
    }


    // Below are extra inner functions needed for this helper

    private fun deepCopy(
        indexedMatchMap: Map<Int, UUID>
    ): MutableMap<Int, UUID>
    {
        val map = mutableMapOf<Int, UUID>()
        if (indexedMatchMap.isNotEmpty()) {
            indexedMatchMap.forEach { (i, uuid) -> map[i] = uuid }
        }
        return map
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
            nbt.putInt(TournamentDataKeys.ROUND_MATCH_INDEX + index     , roundMatchIndex)
            nbt.putUUID(TournamentDataKeys.MATCH_ID         + index++   , matchID)
        }
        nbt.putInt(StoreDataKeys.SIZE, indexedMatchMap.size)
        return nbt
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
            val roundMatchIndex     = nbt.getInt(   TournamentDataKeys.ROUND_MATCH_INDEX + i)
            val matchID             = nbt.getUUID(  TournamentDataKeys.MATCH_ID + i)
            indexedMatchMap[roundMatchIndex] = matchID
        }
        return indexedMatchMap
    }

}
