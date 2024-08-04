package com.cobblemontournament.common.match.properties

import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.connections.MatchConnections
import com.sg8.collections.reactive.map.loadMutableObservableMapOf
import com.sg8.collections.reactive.map.saveToNbt
import com.sg8.properties.PropertiesHelper
import com.sg8.util.appendWith
import com.sg8.util.appendWithBracketed
import com.sg8.util.ComponentUtil
import com.sg8.util.getConstantStrict
import com.sg8.util.getUuidOrNull
import com.sg8.util.putIfNotNull
import com.sg8.util.short
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID
import kotlin.collections.Map.Entry


object MatchPropertiesHelper : PropertiesHelper<MatchProperties> {

    override fun saveToNbt(properties: MatchProperties, nbt: CompoundTag): CompoundTag {
        nbt.putUUID(DataKeys.MATCH_ID, properties.uuid)
        nbt.putUUID(DataKeys.TOURNAMENT_ID, properties.tournamentID)
        nbt.putUUID(DataKeys.ROUND_ID, properties.roundID)
        nbt.putInt(DataKeys.ROUND_MATCH_INDEX, properties.roundIndex)
        nbt.putInt(DataKeys.TOURNAMENT_MATCH_INDEX, properties.tournamentMatchIndex)
        nbt.putInt(DataKeys.ROUND_MATCH_INDEX, properties.roundMatchIndex)
        nbt.put(DataKeys.MATCH_CONNECTIONS, properties.matchConnections.saveToNbt( CompoundTag() ))
        nbt.putString(DataKeys.MATCH_STATUS, properties.matchStatus.name)
        nbt.putIfNotNull(DataKeys.VICTOR_ID, properties.victorID)
        nbt.savePlayersMap(properties)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): MatchProperties {
        return  MatchProperties(
            uuid = nbt.getUUID(DataKeys.MATCH_ID),
            tournamentID = nbt.getUUID(DataKeys.TOURNAMENT_ID),
            roundID = nbt.getUUID(DataKeys.ROUND_ID),
            roundIndex = nbt.getInt(DataKeys.ROUND_INDEX),
            tournamentMatchIndex = nbt.getInt(DataKeys.TOURNAMENT_MATCH_INDEX),
            roundMatchIndex = nbt.getInt(DataKeys.ROUND_MATCH_INDEX),
            victorID = nbt.getUuidOrNull(DataKeys.VICTOR_ID),
            matchStatus = nbt.getConstantStrict<MatchStatus>(DataKeys.MATCH_STATUS),
            matchConnections = MatchConnections.loadFromNbt(
                nbt.getCompound(DataKeys.MATCH_CONNECTIONS)
            ),
            playerMap = nbt.loadPlayersMap(),
        )
    }

    override fun setFromNbt(mutable: MatchProperties, nbt: CompoundTag): MatchProperties {
        mutable.uuid = nbt.getUUID(DataKeys.MATCH_ID)
        mutable.tournamentID = nbt.getUUID(DataKeys.TOURNAMENT_ID)
        mutable.roundID = nbt.getUUID(DataKeys.ROUND_ID)
        mutable.roundIndex = nbt.getInt(DataKeys.ROUND_INDEX)
        mutable.tournamentMatchIndex = nbt.getInt(DataKeys.TOURNAMENT_MATCH_INDEX)
        mutable.roundMatchIndex = nbt.getInt(DataKeys.ROUND_MATCH_INDEX)
        mutable.victorID = nbt.getUuidOrNull(DataKeys.VICTOR_ID)
        mutable.matchStatus = nbt.getConstantStrict<MatchStatus>(DataKeys.MATCH_STATUS)
        mutable.matchConnections = MatchConnections.loadFromNbt(
            nbt.getCompound(DataKeys.MATCH_CONNECTIONS)
        )
        mutable.playerMap = nbt.loadPlayersMap()
        return mutable
    }

    private fun CompoundTag.savePlayersMap(properties: MatchProperties): Tag? {
        val entryHandler = { entry: Entry<UUID, Int> ->
            CompoundTag().also { entryNbt ->
                entryNbt.putUUID(DataKeys.PLAYER_ID, entry.key)
                entryNbt.putInt(DataKeys.TEAM_INDEX, entry.value)
            }
        }
        val mapNbt = properties.playerMap.saveToNbt(entryHandler)
        return this.put(DataKeys.PLAYER_TEAM_MAP, mapNbt)
    }

    private fun CompoundTag.loadPlayersMap(): MutablePlayerTeamMap {
        val entryHandler = { nbt: CompoundTag ->
            nbt.getUUID(DataKeys.PLAYER_ID) to nbt.getInt(DataKeys.TEAM_INDEX)
        }
        val mapNbt = this.getCompound(DataKeys.PLAYER_TEAM_MAP)
        return mapNbt.loadMutableObservableMapOf(entryHandler)
    }

    override fun deepCopy(properties: MatchProperties): MatchProperties {
        properties.matchConnections = properties.matchConnections.deepCopy()
        return copy(properties)
    }

    override fun copy(properties: MatchProperties): MatchProperties {
        return MatchProperties(
            uuid = properties.uuid,
            tournamentID = properties.tournamentID,
            roundID = properties.roundID,
            roundIndex = properties.roundIndex,
            tournamentMatchIndex = properties.tournamentMatchIndex,
            roundMatchIndex = properties.roundMatchIndex,
            matchStatus = properties.matchStatus,
            victorID = properties.victorID,
            playerMap = properties.playerMap,
            matchConnections = properties.matchConnections,
        )
    }

    override fun printDebug(properties: MatchProperties) {
        Util.report("Match \"${properties.name}\" [${properties.uuid.short()}]")
        Util.report("  Tournament ID [${properties.tournamentID.short()}]")
        Util.report(
            "  Round Index (${properties.roundIndex}) [${properties.roundID.short()}]"
        )
        Util.report("  Status: ${properties.matchStatus}")
        Util.report("  Victor: ${properties.victorID}")
        if (properties.playerMap.isNotEmpty()) {
            Util.report("  Players")
            val sorted = properties.playerMap.toSortedMap { _, teamIndex ->
                teamIndex.compareTo(teamIndex)
            }
            sorted.forEach { map ->
                Util.report("    Team: ${map.value} - Player [${map.key.short()}]")
            }
        }
    }

    override fun displayInChat(properties: MatchProperties, player: ServerPlayer) {
        val propertiesComponent = ComponentUtil.getComponent(
            text = "${properties.name} ",
            color = ChatFormatting.GREEN,
            bold = true,
        )
        propertiesComponent.appendWithBracketed(
            text = properties.uuid.short(),
            textColor = ChatFormatting.GREEN,
        )

        val statusComponent = ComponentUtil.getComponent(text = "  Status ")
        statusComponent.appendWithBracketed(
            text = properties.matchStatus.name,
            textColor = ChatFormatting.YELLOW,
        )

        val victorComponent = ComponentUtil.getComponent(text = "  Victor ")
        victorComponent.appendWithBracketed(
            text = properties.victorID.short(),
            textColor = ChatFormatting.YELLOW,
        )

        player.displayClientMessage(propertiesComponent, false)
        player.displayClientMessage(statusComponent, false)
        player.displayClientMessage(victorComponent, false)

        if (properties.playerMap.isNotEmpty()) {
            val titleComponent = ComponentUtil.getComponent(
                text = "  Players ",
                color = ChatFormatting.AQUA,
                bold = true,
            )
            titleComponent.appendWithBracketed(
                text = "${properties.playerMap.size}",
                textColor = ChatFormatting.AQUA,
            )

            player.displayClientMessage(titleComponent, (false))

            val sorted = properties.playerMap.toList().sortedBy { (_, team) -> team }
            sorted.forEach { (playerID, team) ->
                val playerComponent = ComponentUtil.getComponent(text = "    Player ")
                playerComponent.appendWithBracketed(
                    text = playerID.short(),
                    textColor = ChatFormatting.AQUA,
                )
                playerComponent.appendWith(text = " Team ")
                playerComponent.appendWithBracketed(
                    text = "$team",
                    textColor = ChatFormatting.AQUA,
                )
                player.displayClientMessage(playerComponent, false)
            }
        }
    }

}
