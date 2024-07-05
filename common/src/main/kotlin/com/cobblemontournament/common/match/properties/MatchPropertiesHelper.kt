package com.cobblemontournament.common.match.properties

import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.util.TournamentDataKeys
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import com.someguy.storage.util.StoreUtil.getNullableUUID
import com.someguy.storage.util.StoreUtil.putIfNotNull
import net.minecraft.nbt.CompoundTag
import java.util.UUID

object MatchPropertiesHelper
    : PropertiesHelper <MatchPropertyFields, MatchProperties, MutableMatchProperties>
{
    const val DEFAULT_TOURNAMENT_MATCH_INDEX    = -1
    const val DEFAULT_ROUND_MATCH_INDEX         = -1
    val DEFAULT_MATCH_STATUS                    = MatchStatus.UNKNOWN
    val DEFAULT_VICTOR_ID                       = null

    override fun deepCopyHelper(
        properties: MatchPropertyFields,
    ): MatchProperties
    {
        return MatchProperties(
            matchID                  = properties.matchID,
            tournamentID             = properties.tournamentID,
            roundID                  = properties.roundID,
            tournamentMatchIndex     = properties.tournamentMatchIndex,
            roundMatchIndex          = properties.roundMatchIndex,
            matchStatus              = properties.matchStatus,
            victorID                 = properties.victorID,
            playerMap                = deepCopy(properties.playerMap))
    }

    override fun deepMutableCopyHelper(
        properties: MatchPropertyFields,
    ): MutableMatchProperties
    {
        return MutableMatchProperties(
            matchID                  = properties.matchID,
            tournamentID             = properties.tournamentID,
            roundID                  = properties.roundID,
            tournamentMatchIndex     = properties.tournamentMatchIndex,
            roundMatchIndex          = properties.roundMatchIndex,
            matchStatus              = properties.matchStatus,
            victorID                 = properties.victorID,
            playerMap                = deepCopy(properties.playerMap))
    }

    @Suppress("DuplicatedCode")
    override fun setFromPropertiesHelper(
        mutable: MutableMatchProperties,
        from: MatchPropertyFields
    ): MutableMatchProperties
    {
        mutable.matchID                  = from.matchID
        mutable.tournamentID             = from.tournamentID
        mutable.roundID                  = from.roundID
        mutable.tournamentMatchIndex     = from.tournamentMatchIndex
        mutable.roundMatchIndex          = from.roundMatchIndex
        mutable.matchStatus              = from.matchStatus
        mutable.victorID                 = from.victorID
        mutable.playerMap                = deepCopy(from.playerMap)
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: MutableMatchProperties,
        nbt: CompoundTag
    ): MutableMatchProperties
    {
        mutable.matchID                 = nbt.getUUID(TournamentDataKeys.MATCH_ID)
        mutable.tournamentID            = nbt.getUUID(TournamentDataKeys.TOURNAMENT_ID)
        mutable.roundID                 = nbt.getUUID(TournamentDataKeys.ROUND_ID)
        mutable.tournamentMatchIndex    = nbt.getInt(TournamentDataKeys.TOURNAMENT_MATCH_INDEX)
        mutable.roundMatchIndex         = nbt.getInt(TournamentDataKeys.ROUND_MATCH_INDEX)
        mutable.victorID                = nbt.getNullableUUID(TournamentDataKeys.VICTOR_ID)
        mutable.matchStatus = enumValueOf<MatchStatus> (nbt.getString(TournamentDataKeys.MATCH_STATUS))
        mutable.playerMap = loadPlayerMapFromNBT(nbt.getCompound(TournamentDataKeys.PLAYER_ID_TO_TEAM_INDEX))
        return mutable
    }

    override fun saveToNBTHelper(
        properties: MatchPropertyFields,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putUUID(        TournamentDataKeys.MATCH_ID                 , properties.matchID)
        nbt.putUUID(        TournamentDataKeys.TOURNAMENT_ID            , properties.tournamentID)
        nbt.putUUID(        TournamentDataKeys.ROUND_ID                 , properties.roundID)
        nbt.putInt(         TournamentDataKeys.TOURNAMENT_MATCH_INDEX   , properties.tournamentMatchIndex)
        nbt.putInt(         TournamentDataKeys.ROUND_MATCH_INDEX        , properties.roundMatchIndex)
        nbt.putString(      TournamentDataKeys.MATCH_STATUS             , properties.matchStatus.name)
        nbt.putIfNotNull(   TournamentDataKeys.VICTOR_ID                , properties.victorID)
        if (properties.playerMap.isNotEmpty()) {
            nbt.put(TournamentDataKeys.PLAYER_ID_TO_TEAM_INDEX, savePlayerMapToNBT(properties.playerMap, CompoundTag()))
        }
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ): MatchProperties
    {
        return  MatchProperties(
            matchID                 = nbt.getUUID(TournamentDataKeys.MATCH_ID),
            tournamentID            = nbt.getUUID(TournamentDataKeys.TOURNAMENT_ID),
            roundID                 = nbt.getUUID(TournamentDataKeys.ROUND_ID),
            tournamentMatchIndex    = nbt.getInt(TournamentDataKeys.TOURNAMENT_MATCH_INDEX),
            roundMatchIndex         = nbt.getInt(TournamentDataKeys.ROUND_MATCH_INDEX),
            victorID                = nbt.getNullableUUID(TournamentDataKeys.VICTOR_ID),
            matchStatus = enumValueOf<MatchStatus> (nbt.getString(TournamentDataKeys.MATCH_STATUS)),
            playerMap = loadPlayerMapFromNBT(nbt.getCompound(TournamentDataKeys.PLAYER_ID_TO_TEAM_INDEX))
        )
    }

    override fun loadMutableFromNBT(
        nbt: CompoundTag
    ): MutableMatchProperties
    {
        return  MutableMatchProperties(
            matchID                 = nbt.getUUID(TournamentDataKeys.MATCH_ID),
            tournamentID            = nbt.getUUID(TournamentDataKeys.TOURNAMENT_ID),
            roundID                 = nbt.getUUID(TournamentDataKeys.ROUND_ID),
            tournamentMatchIndex    = nbt.getInt(TournamentDataKeys.TOURNAMENT_MATCH_INDEX),
            roundMatchIndex         = nbt.getInt(TournamentDataKeys.ROUND_MATCH_INDEX),
            victorID                = nbt.getNullableUUID(TournamentDataKeys.VICTOR_ID),
            matchStatus = enumValueOf<MatchStatus> (nbt.getString(TournamentDataKeys.MATCH_STATUS)),
            playerMap = loadPlayerMapFromNBT(nbt.getCompound(TournamentDataKeys.PLAYER_ID_TO_TEAM_INDEX))
        )
    }


    // Below are extra inner functions needed for this helper

    private fun deepCopy(
        map: Map<UUID,Int>,
    ): MutableMap<UUID,Int>
    {
        val copy = mutableMapOf<UUID,Int>()
        map.forEach { (key, value) -> copy[key] = value }
        return copy
    }

    private fun savePlayerMapToNBT(
        playerMap: Map<UUID,Int>,
        nbt :CompoundTag
    ): CompoundTag
    {
        var index = 0
        for ((key, value) in playerMap) {
            nbt.putUUID(TournamentDataKeys.PLAYER_ID + index, key)
            nbt.putInt(TournamentDataKeys.TEAM_INDEX + index++, value)
        }
        nbt.putInt(StoreDataKeys.SIZE, index)
        return nbt
    }

    private fun loadPlayerMapFromNBT(
        nbt: CompoundTag
    ): MutableMap<UUID,Int>
    {
        val playerMap = mutableMapOf<UUID,Int>()
        if (nbt.contains(StoreDataKeys.SIZE) && nbt.getInt(StoreDataKeys.SIZE) != 0) {
            val size = nbt.getInt(StoreDataKeys.SIZE)
            for (i in 0 until size) {
                val playerID = nbt.getUUID(TournamentDataKeys.PLAYER_ID + i)
                val team = nbt.getInt(TournamentDataKeys.TEAM_INDEX + i)
                playerMap[playerID] = team
            }
        }
        return playerMap
    }

}