package com.cobblemontournament.common.match.properties

import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.MatchConnections
import com.cobblemontournament.common.util.*
import com.someguy.storage.PropertiesHelper
import com.someguy.storage.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object MatchPropertiesHelper : PropertiesHelper<MatchProperties> {

    override fun setFromNbtHelper(mutable: MatchProperties, nbt: CompoundTag): MatchProperties {
        mutable.matchID = nbt.getUUID(MATCH_ID_KEY)
        mutable.tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.roundID = nbt.getUUID(ROUND_ID_KEY)
        mutable.roundIndex = nbt.getInt(ROUND_INDEX_KEY)
        mutable.tournamentMatchIndex = nbt.getInt(TOURNAMENT_MATCH_INDEX_KEY)
        mutable.roundMatchIndex = nbt.getInt(ROUND_MATCH_INDEX_KEY)
        mutable.victorID = nbt.getNullableUUID(VICTOR_ID_KEY)
        mutable.matchStatus = enumValueOf<MatchStatus>(nbt.getString(MATCH_STATUS_KEY))
        mutable.playerMap.putAll(
            loadPlayerMapFromNBT(nbt.getCompound(PLAYER_ID_TO_TEAM_INDEX_KEY))
        )
        mutable.connections.setFromNbt(nbt.getCompound(MATCH_CONNECTIONS_KEY))
        return mutable
    }

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

    override fun saveToNbtHelper(properties: MatchProperties, nbt: CompoundTag): CompoundTag {
        nbt.putUUID(MATCH_ID_KEY, properties.matchID)
        nbt.putUUID(TOURNAMENT_ID_KEY, properties.tournamentID)
        nbt.putUUID(ROUND_ID_KEY, properties.roundID)
        nbt.putInt(ROUND_MATCH_INDEX_KEY, properties.roundIndex)
        nbt.putInt(TOURNAMENT_MATCH_INDEX_KEY, properties.tournamentMatchIndex)
        nbt.putInt(ROUND_MATCH_INDEX_KEY, properties.roundMatchIndex)
        nbt.put(MATCH_CONNECTIONS_KEY, properties.connections.saveToNbt( CompoundTag() ))
        nbt.putString(MATCH_STATUS_KEY, properties.matchStatus.name)
        nbt.putIfNotNull(VICTOR_ID_KEY, properties.victorID)
        if (properties.playerMap.isNotEmpty()) {
            nbt.put(
                PLAYER_ID_TO_TEAM_INDEX_KEY,
                savePlayerMapToNBT(properties.playerMap, CompoundTag())
            )
        }
        return nbt
    }

    private fun savePlayerMapToNBT(playerMap: Map<UUID,Int>, nbt: CompoundTag): CompoundTag {
        var index = 0
        for ((key, value) in playerMap) {
            nbt.putUUID((PLAYER_ID_KEY + index), key)
            nbt.putInt((TEAM_INDEX_KEY + index++), value)
        }
        nbt.putInt(SIZE_KEY, index)
        return nbt
    }

    override fun loadFromNbtHelper(nbt: CompoundTag): MatchProperties {
        return  MatchProperties(
            matchID = nbt.getUUID(MATCH_ID_KEY),
            tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY),
            roundID = nbt.getUUID(ROUND_ID_KEY),
            roundIndex = nbt.getInt(ROUND_INDEX_KEY),
            tournamentMatchIndex = nbt.getInt(TOURNAMENT_MATCH_INDEX_KEY),
            roundMatchIndex = nbt.getInt(ROUND_MATCH_INDEX_KEY),
            connections = MatchConnections().setFromNbt(nbt.getCompound(MATCH_CONNECTIONS_KEY)),
            victorID = nbt.getNullableUUID(VICTOR_ID_KEY),
            matchStatus = enumValueOf<MatchStatus>(nbt.getString(MATCH_STATUS_KEY)),
            playerMap = loadPlayerMapFromNBT(nbt.getCompound(PLAYER_ID_TO_TEAM_INDEX_KEY))
        )
    }

    private fun loadPlayerMapFromNBT(nbt: CompoundTag): MutableMap<UUID, Int> {
        val playerMap = mutableMapOf<UUID, Int>()
        if (nbt.contains(SIZE_KEY) && (nbt.getInt(SIZE_KEY) != 0)) {
            val size = nbt.getInt(SIZE_KEY)
            for (i in 0 until size) {
                val playerID = nbt.getUUID((PLAYER_ID_KEY + i))
                val team = nbt.getInt((TEAM_INDEX_KEY + i))
                playerMap[playerID] = team
            }
        }
        return playerMap
    }

    override fun logDebugHelper(properties: MatchProperties) {
        Util.report(("Match \"${properties.name}\" [${properties.matchID.shortUUID()}]"))
        Util.report(("  Tournament ID [${properties.tournamentID.shortUUID()}]"))
        Util.report(
            ("  Round Index (${properties.roundIndex}) [${properties.roundID.shortUUID()}]")
        )
        Util.report(("  Status: ${properties.matchStatus}"))
        Util.report(("  Victor: ${properties.victorID}"))
        if (properties.playerMap.isNotEmpty()) {
            Util.report(("  Players"))
            val sorted = properties.playerMap.toSortedMap { _, teamIndex ->
                teamIndex.compareTo(teamIndex)
            }
            sorted.forEach { map ->
                Util.report(("    Team: ${map.value} - Player [${map.key.shortUUID()}]"))
            }
        }
    }

    override fun displayInChatHelper(properties: MatchProperties, player: ServerPlayer) {
        val propertiesComponent = getComponent(
            text = "${properties.name} ",
            color = ChatUtil.GREEN_FORMAT,
            bold = true,
        )
        propertiesComponent.appendWithBracketed(
            text = properties.matchID.shortUUID(),
            textColor = ChatUtil.GREEN_FORMAT,
        )

        val statusComponent = getComponent(text = "  Status ")
        statusComponent.appendWithBracketed(
            text = properties.matchStatus.name,
            textColor = ChatUtil.YELLOW_FORMAT,
        )

        val victorComponent = getComponent(text = "  Victor ")
        victorComponent.appendWithBracketed(
            text = properties.victorID.shortUUID(),
            textColor = ChatUtil.YELLOW_FORMAT,
        )

        player.displayClientMessage(propertiesComponent, false)
        player.displayClientMessage(statusComponent, false)
        player.displayClientMessage(victorComponent, false)

        if (properties.playerMap.isNotEmpty()) {
            val titleComponent = getComponent(
                text = "  Players ",
                color = ChatUtil.AQUA_FORMAT,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.playerMap.size}",
                textColor = ChatUtil.AQUA_FORMAT,
            )

            player.displayClientMessage(titleComponent, (false))

            val sorted = properties.playerMap.toList().sortedBy { (_, team) -> team }
            sorted.forEach { (playerID, team) ->
                val playerComponent = getComponent(text = "    Player ")
                playerComponent.appendWithBracketed(
                    text = playerID.shortUUID(),
                    textColor = ChatUtil.AQUA_FORMAT,
                )
                playerComponent.appendWith(text = " Team ")
                playerComponent.appendWithBracketed(
                    text = "$team",
                    textColor = ChatUtil.AQUA_FORMAT,
                )
                player.displayClientMessage(playerComponent, false)
            }
        }
    }

}
