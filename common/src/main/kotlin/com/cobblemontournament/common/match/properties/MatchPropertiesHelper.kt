package com.cobblemontournament.common.match.properties

import com.cobblemontournament.common.api.storage.TournamentDataKeys.MATCH_CONNECTIONS_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MATCH_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MATCH_STATUS_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.PLAYER_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.PLAYER_ID_TO_TEAM_INDEX_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_INDEX_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ROUND_MATCH_INDEX_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TEAM_INDEX_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_MATCH_INDEX_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.VICTOR_ID_KEY
import com.cobblemontournament.common.match.MatchStatus
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

object MatchPropertiesHelper : PropertiesHelper<MatchProperties> {

    const val DEFAULT_TOURNAMENT_MATCH_INDEX = -1
    const val DEFAULT_ROUND_MATCH_INDEX = -1
    val DEFAULT_MATCH_STATUS = MatchStatus.UNKNOWN
    val DEFAULT_VICTOR_ID = null

    override fun deepCopyHelper(properties: MatchProperties): MatchProperties {
        return MatchProperties(
            matchID = properties.matchID,
            tournamentID = properties.tournamentID,
            roundID = properties.roundID,
            roundIndex = properties.roundIndex,
            tournamentMatchIndex = properties.tournamentMatchIndex,
            roundMatchIndex = properties.roundMatchIndex,
            connections = properties.connections.deepCopy(),
            matchStatus = properties.matchStatus,
            victorID = properties.victorID,
            playerMap = TournamentUtil.shallowCopy(properties.playerMap),
        )
    }

    @Suppress("DuplicatedCode")
    override fun setFromPropertiesHelper(
        mutable: MatchProperties,
        from: MatchProperties,
    ): MatchProperties {
        mutable.matchID = from.matchID
        mutable.tournamentID = from.tournamentID
        mutable.roundID = from.roundID
        mutable.roundIndex = from.roundIndex
        mutable.tournamentMatchIndex = from.tournamentMatchIndex
        mutable.roundMatchIndex = from.roundMatchIndex
        mutable.matchStatus = from.matchStatus
        mutable.victorID = from.victorID
        mutable.playerMap = TournamentUtil.shallowCopy(from.playerMap)
        mutable.connections.setFromConnections(from.connections)
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: MatchProperties,
        nbt: CompoundTag,
    ): MatchProperties {
        mutable.matchID = nbt.getUUID(MATCH_ID_KEY)
        mutable.tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.roundID = nbt.getUUID(ROUND_ID_KEY)
        mutable.roundIndex = nbt.getInt(ROUND_INDEX_KEY)
        mutable.tournamentMatchIndex = nbt.getInt(TOURNAMENT_MATCH_INDEX_KEY)
        mutable.roundMatchIndex = nbt.getInt(ROUND_MATCH_INDEX_KEY)
        mutable.victorID = nbt.getNullableUUID(VICTOR_ID_KEY)
        mutable.matchStatus = enumValueOf<MatchStatus>(nbt.getString(MATCH_STATUS_KEY))
        mutable.playerMap = loadPlayerMapFromNBT(nbt.getCompound(PLAYER_ID_TO_TEAM_INDEX_KEY))
        mutable.connections.setFromNBT(nbt.getCompound(MATCH_CONNECTIONS_KEY))
        return mutable
    }

    override fun saveToNBTHelper(
        properties: MatchProperties,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putUUID(MATCH_ID_KEY, properties.matchID)
        nbt.putUUID(TOURNAMENT_ID_KEY, properties.tournamentID)
        nbt.putUUID(ROUND_ID_KEY, properties.roundID)
        nbt.putInt(ROUND_MATCH_INDEX_KEY, properties.roundIndex)
        nbt.putInt(TOURNAMENT_MATCH_INDEX_KEY, properties.tournamentMatchIndex)
        nbt.putInt(ROUND_MATCH_INDEX_KEY, properties.roundMatchIndex)
        nbt.put(MATCH_CONNECTIONS_KEY, properties.connections.saveToNBT( CompoundTag() ))
        nbt.putString(MATCH_STATUS_KEY, properties.matchStatus.name)
        nbt.putIfNotNull(VICTOR_ID_KEY, properties.victorID)
        if (properties.playerMap.isNotEmpty()) {
            nbt.put(PLAYER_ID_TO_TEAM_INDEX_KEY, savePlayerMapToNBT(properties.playerMap, CompoundTag()))
        }
        return nbt
    }

    private fun savePlayerMapToNBT(
        playerMap: Map<UUID,Int>,
        nbt: CompoundTag
    ): CompoundTag {
        var index = 0
        for ((key, value) in playerMap) {
            nbt.putUUID((PLAYER_ID_KEY + index), key)
            nbt.putInt((TEAM_INDEX_KEY + index++), value)
        }
        nbt.putInt(StoreDataKeys.SIZE, index)
        return nbt
    }

    override fun loadFromNBTHelper(
        nbt: CompoundTag
    ): MatchProperties
    {
        return  MatchProperties(
            matchID = nbt.getUUID(MATCH_ID_KEY),
            tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY),
            roundID = nbt.getUUID(ROUND_ID_KEY),
            roundIndex = nbt.getInt(ROUND_INDEX_KEY),
            tournamentMatchIndex = nbt.getInt(TOURNAMENT_MATCH_INDEX_KEY),
            roundMatchIndex = nbt.getInt(ROUND_MATCH_INDEX_KEY),
            connections = MatchConnections().setFromNBT(nbt.getCompound( MATCH_CONNECTIONS_KEY )),
            victorID = nbt.getNullableUUID(VICTOR_ID_KEY),
            matchStatus = enumValueOf<MatchStatus>(nbt.getString(MATCH_STATUS_KEY)),
            playerMap = loadPlayerMapFromNBT(nbt.getCompound(PLAYER_ID_TO_TEAM_INDEX_KEY))
        )
    }

    private fun loadPlayerMapFromNBT(nbt: CompoundTag): MutableMap<UUID, Int> {
        val playerMap = mutableMapOf<UUID, Int>()
        if (nbt.contains(StoreDataKeys.SIZE) && (nbt.getInt(StoreDataKeys.SIZE) != 0)) {
            val size = nbt.getInt(StoreDataKeys.SIZE)
            for (i in 0 until size) {
                val playerID = nbt.getUUID((PLAYER_ID_KEY + i))
                val team = nbt.getInt((TEAM_INDEX_KEY + i))
                playerMap[playerID] = team
            }
        }
        return playerMap
    }

    override fun logDebugHelper(properties: MatchProperties) {
        Util.report(("Match \"${properties.name}\" [${ChatUtil.shortUUID( properties.matchID )}]"))
        Util.report(("  Tournament ID [${ChatUtil.shortUUID( properties.tournamentID )}]"))
        Util.report(("  Round Index (${properties.roundIndex}) [${ChatUtil.shortUUID( properties.roundID )}]"))
        Util.report(("  Status: ${properties.matchStatus}"))
        Util.report(("  Victor: ${properties.victorID}"))
        if (properties.playerMap.isNotEmpty()) {
            Util.report(("  Players"))
            val sorted = properties.playerMap.toSortedMap { _, teamIndex -> teamIndex.compareTo(teamIndex) }
            sorted.forEach { map ->
                Util.report(("    Team: ${map.value} - Player [${ChatUtil.shortUUID( map.key )}]"))
            }
        }
    }

    override fun displayInChatHelper(properties: MatchProperties, player: ServerPlayer) {
        val propertiesText = ChatUtil.formatText(
            text = "${properties.name} ",
            color = ChatUtil.green,
            bold = true,
        )
        propertiesText.append(ChatUtil.formatTextBracketed(
            text = ChatUtil.shortUUID(properties.matchID),
            color = ChatUtil.green,
        ))
        val statusText =ChatUtil.formatText(text = "  Status ")
        statusText.append(ChatUtil.formatTextBracketed(
            text = properties.matchStatus.name,
            color = ChatUtil.yellow,
        ))
        val shortVictorID = if (properties.victorID != null) {
            ChatUtil.shortUUID( properties.victorID )
        } else {
            "null"
        }
        val victorText = ChatUtil.formatText(text = "  Victor ")
        victorText.append(ChatUtil.formatTextBracketed(
            text = shortVictorID,
            color = ChatUtil.yellow,
        ))

        player.displayClientMessage(propertiesText, (false))
        player.displayClientMessage(statusText, (false))
        player.displayClientMessage(victorText, (false))

        if (properties.playerMap.isNotEmpty()) {
            val title = ChatUtil.formatText(
                text    = "  Players ",
                color   = ChatUtil.aqua,
                bold    = true,
            )
            title.append(ChatUtil.formatTextBracketed(
                text = "${properties.playerMap.size}",
                color = ChatUtil.aqua,
            ))
            player.displayClientMessage(title, (false))
            val sorted = properties.playerMap.toList().sortedBy { (_, team) -> team }
            sorted.forEach { (playerID, team) ->
                val playerText = ChatUtil.formatText(text = "    Player ")
                playerText.append(ChatUtil.formatTextBracketed(
                    text = ChatUtil.shortUUID(playerID),
                    color = ChatUtil.aqua,
                ))
                playerText.append(ChatUtil.formatText(
                    text = " Team ",
                ))
                playerText.append(ChatUtil.formatTextBracketed(
                    text = "$team",
                    color = ChatUtil.aqua,
                ))
                player.displayClientMessage(playerText, (false))
            }
        }
    }

}