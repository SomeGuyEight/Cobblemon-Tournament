package com.cobblemontournament.common.match.properties

import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.match.MatchConnections
import com.cobblemontournament.common.util.ChatUtil
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import com.someguy.storage.store.StoreUtil.getNullableUUID
import com.someguy.storage.store.StoreUtil.putIfNotNull
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object MatchPropertiesHelper : PropertiesHelper <MatchProperties>
{
    const val DEFAULT_TOURNAMENT_MATCH_INDEX    = -1
    const val DEFAULT_ROUND_MATCH_INDEX         = -1
    val DEFAULT_MATCH_STATUS                    = MatchStatus.UNKNOWN
    val DEFAULT_VICTOR_ID                       = null

    override fun deepCopyHelper(
        properties: MatchProperties,
    ): MatchProperties
    {
        return MatchProperties(
            matchID                 = properties.matchID,
            tournamentID            = properties.tournamentID,
            roundID                 = properties.roundID,
            roundIndex              = properties.roundIndex,
            tournamentMatchIndex    = properties.tournamentMatchIndex,
            roundMatchIndex         = properties.roundMatchIndex,
            connections             = properties.connections.deepCopy(),
            matchStatus             = properties.matchStatus,
            victorID                = properties.victorID,
            playerMap               = TournamentUtil.shallowCopy( properties.playerMap ))
    }

    @Suppress("DuplicatedCode")
    override fun setFromPropertiesHelper(
        mutable: MatchProperties,
        from: MatchProperties
    ): MatchProperties
    {
        mutable.matchID                 = from.matchID
        mutable.tournamentID            = from.tournamentID
        mutable.roundID                 = from.roundID
        mutable.roundIndex              = from.roundIndex
        mutable.tournamentMatchIndex    = from.tournamentMatchIndex
        mutable.roundMatchIndex         = from.roundMatchIndex
        mutable.matchStatus             = from.matchStatus
        mutable.victorID                = from.victorID
        mutable.playerMap               = TournamentUtil.shallowCopy( from.playerMap )
        mutable.connections.setFromConnections( from.connections )
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: MatchProperties,
        nbt: CompoundTag
    ): MatchProperties
    {
        mutable.matchID                 = nbt.getUUID(  DataKeys.MATCH_ID)
        mutable.tournamentID            = nbt.getUUID(  DataKeys.TOURNAMENT_ID)
        mutable.roundID                 = nbt.getUUID(  DataKeys.ROUND_ID)
        mutable.roundIndex              = nbt.getInt(   DataKeys.ROUND_INDEX)
        mutable.tournamentMatchIndex    = nbt.getInt(   DataKeys.TOURNAMENT_MATCH_INDEX)
        mutable.roundMatchIndex         = nbt.getInt(   DataKeys.ROUND_MATCH_INDEX)
        mutable.victorID                = nbt.getNullableUUID(DataKeys.VICTOR_ID)
        mutable.matchStatus = enumValueOf<MatchStatus> (nbt.getString(DataKeys.MATCH_STATUS))
        mutable.playerMap = loadPlayerMapFromNBT(nbt.getCompound(DataKeys.PLAYER_ID_TO_TEAM_INDEX))
        mutable.connections.setFromNBT( nbt.getCompound( DataKeys.MATCH_CONNECTIONS ))
        return mutable
    }

    override fun saveToNBTHelper(
        properties: MatchProperties,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putUUID(        DataKeys.MATCH_ID                 , properties.matchID)
        nbt.putUUID(        DataKeys.TOURNAMENT_ID            , properties.tournamentID)
        nbt.putUUID(        DataKeys.ROUND_ID                 , properties.roundID)
        nbt.putInt(         DataKeys.ROUND_MATCH_INDEX        , properties.roundIndex)
        nbt.putInt(         DataKeys.TOURNAMENT_MATCH_INDEX   , properties.tournamentMatchIndex)
        nbt.putInt(         DataKeys.ROUND_MATCH_INDEX        , properties.roundMatchIndex)
        nbt.put(            DataKeys.MATCH_CONNECTIONS        , properties.connections.saveToNBT( CompoundTag() ))
        nbt.putString(      DataKeys.MATCH_STATUS             , properties.matchStatus.name)
        nbt.putIfNotNull(   DataKeys.VICTOR_ID                , properties.victorID)
        if (properties.playerMap.isNotEmpty()) {
            nbt.put( DataKeys.PLAYER_ID_TO_TEAM_INDEX, savePlayerMapToNBT( properties.playerMap, CompoundTag() ))
        }
        return nbt
    }

    private fun savePlayerMapToNBT(
        playerMap: Map<UUID,Int>,
        nbt :CompoundTag
    ): CompoundTag
    {
        var index = 0
        for ((key, value) in playerMap) {
            nbt.putUUID(DataKeys.PLAYER_ID + index, key)
            nbt.putInt(DataKeys.TEAM_INDEX + index++, value)
        }
        nbt.putInt(StoreDataKeys.SIZE, index)
        return nbt
    }

    override fun loadFromNBTHelper(
        nbt: CompoundTag
    ): MatchProperties
    {
        return  MatchProperties(
            matchID                 = nbt.getUUID( DataKeys.MATCH_ID),
            tournamentID            = nbt.getUUID( DataKeys.TOURNAMENT_ID),
            roundID                 = nbt.getUUID( DataKeys.ROUND_ID),
            roundIndex              = nbt.getInt(  DataKeys.ROUND_INDEX),
            tournamentMatchIndex    = nbt.getInt(  DataKeys.TOURNAMENT_MATCH_INDEX),
            roundMatchIndex         = nbt.getInt(  DataKeys.ROUND_MATCH_INDEX),
            connections             = MatchConnections().setFromNBT( nbt.getCompound( DataKeys.MATCH_CONNECTIONS )),
            victorID                = nbt.getNullableUUID(DataKeys.VICTOR_ID),
            matchStatus = enumValueOf<MatchStatus> (nbt.getString(DataKeys.MATCH_STATUS)),
            playerMap = loadPlayerMapFromNBT( nbt.getCompound(DataKeys.PLAYER_ID_TO_TEAM_INDEX))
        )
    }

    private fun loadPlayerMapFromNBT(
        nbt: CompoundTag
    ): MutableMap<UUID,Int>
    {
        val playerMap = mutableMapOf<UUID,Int>()
        if (nbt.contains(StoreDataKeys.SIZE) && nbt.getInt(StoreDataKeys.SIZE) != 0) {
            val size = nbt.getInt(StoreDataKeys.SIZE)
            for (i in 0 until size) {
                val playerID = nbt.getUUID(DataKeys.PLAYER_ID + i)
                val team = nbt.getInt(DataKeys.TEAM_INDEX + i)
                playerMap[playerID] = team
            }
        }
        return playerMap
    }

    override fun logDebugHelper( properties: MatchProperties )
    {
        Util.report("Match \"${properties.name}\" [${ChatUtil.shortUUID( properties.matchID )}]")
        Util.report("  Tournament ID [${ChatUtil.shortUUID( properties.tournamentID )}]")
        Util.report("  Round Index (${properties.roundIndex}) [${ChatUtil.shortUUID( properties.roundID )}]")
        Util.report("  Status: ${properties.matchStatus}")
        Util.report("  Victor: ${properties.victorID}")
        if (properties.playerMap.isNotEmpty()) {
            Util.report("  Players")
            val sorted = properties.playerMap.toSortedMap { _, teamIndex -> teamIndex.compareTo(teamIndex) }
            sorted.forEach {
                Util.report("    Team: ${it.value} - Player [${ChatUtil.shortUUID( it.key )}]")
            }
        }
    }

    override fun displayInChatHelper(
        properties: MatchProperties,
        player: ServerPlayer )
    {
        val text0 = ChatUtil.formatText(
            text    = "${properties.name} ",
            color   = ChatUtil.green,
            bold    = true )
        text0.append( ChatUtil.formatTextBracketed(
            text    = ChatUtil.shortUUID( properties.matchID ),
            color   = ChatUtil.green ) )
        val text1 =ChatUtil.formatText(
            text    = "  Status " )
        text1.append( ChatUtil.formatTextBracketed(
            text    = properties.matchStatus.name,
            color   = ChatUtil.yellow ) )
        val victorShortID = if ( properties.victorID != null ) ChatUtil.shortUUID( properties.victorID ) else "null"
        val text2 = ChatUtil.formatText(
            text    = "  Victor ")
        text2.append( ChatUtil.formatTextBracketed(
            text    = victorShortID,
            color   = ChatUtil.yellow ) )

        player.displayClientMessage( text0, false )
        player.displayClientMessage( text1, false )
        player.displayClientMessage( text2, false )

        if (properties.playerMap.isNotEmpty()) {
            val title = ChatUtil.formatText(
                text    = "  Players ",
                color   = ChatUtil.aqua,
                bold    = true)
            title.append( ChatUtil.formatTextBracketed(
                text    = "${properties.playerMap.size}",
                color   = ChatUtil.aqua ) )
            player.displayClientMessage( title, false )
            val sorted = properties.playerMap.toList().sortedBy { it.second }
            sorted.forEach {
                val playerText = ChatUtil.formatText(
                    text    = "    Player " )
                playerText.append( ChatUtil.formatTextBracketed(
                    text    = ChatUtil.shortUUID( it.first ),
                    color   = ChatUtil.aqua ) )
                playerText.append( ChatUtil.formatText(
                    text    = " Team " ) )
                playerText.append( ChatUtil.formatTextBracketed(
                    text    = "${it.second}",
                    color   = ChatUtil.aqua ) )
                player.displayClientMessage( playerText, false )
            }
        }
    }

}