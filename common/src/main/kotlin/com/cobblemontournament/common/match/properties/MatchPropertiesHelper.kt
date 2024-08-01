package com.cobblemontournament.common.match.properties

import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.match.*
import com.sg8.properties.PropertiesHelper
import com.sg8.collections.reactive.map.loadObservableMapOf
import com.sg8.collections.reactive.map.saveToNbt
import com.sg8.collections.reactive.set.saveToNbt
import com.sg8.util.*
import kotlin.collections.Map.Entry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object MatchPropertiesHelper : PropertiesHelper<MatchProperties> {

    override fun saveToNbt(properties: MatchProperties, nbt: CompoundTag): CompoundTag {
        nbt.putUUID(MATCH_ID_KEY, properties.uuid)
        nbt.putUUID(TOURNAMENT_ID_KEY, properties.tournamentID)
        nbt.putUUID(ROUND_ID_KEY, properties.roundID)
        nbt.putInt(ROUND_MATCH_INDEX_KEY, properties.roundIndex)
        nbt.putInt(TOURNAMENT_MATCH_INDEX_KEY, properties.tournamentMatchIndex)
        nbt.putInt(ROUND_MATCH_INDEX_KEY, properties.roundMatchIndex)
        nbt.put(MATCH_CONNECTIONS_KEY, properties.matchConnections.saveToNbt( CompoundTag() ))
        nbt.putString(MATCH_STATUS_KEY, properties.matchStatus.name)
        nbt.putIfNotNull(VICTOR_ID_KEY, properties.victorID)
        nbt.savePlayersMap(properties)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): MatchProperties {
        return  MatchProperties(
            uuid = nbt.getUUID(MATCH_ID_KEY),
            tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY),
            roundID = nbt.getUUID(ROUND_ID_KEY),
            roundIndex = nbt.getInt(ROUND_INDEX_KEY),
            tournamentMatchIndex = nbt.getInt(TOURNAMENT_MATCH_INDEX_KEY),
            roundMatchIndex = nbt.getInt(ROUND_MATCH_INDEX_KEY),
            victorID = nbt.getUuidOrNull(VICTOR_ID_KEY),
            matchStatus = nbt.getConstantStrict<MatchStatus>(MATCH_STATUS_KEY),
            matchConnections = MatchConnections.loadFromNbt(
                nbt.getCompound(MATCH_CONNECTIONS_KEY)
            ),
            playerMap = nbt.loadPlayersMap(),
        )
    }

    override fun setFromNbt(mutable: MatchProperties, nbt: CompoundTag): MatchProperties {
        mutable.uuid = nbt.getUUID(MATCH_ID_KEY)
        mutable.tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.roundID = nbt.getUUID(ROUND_ID_KEY)
        mutable.roundIndex = nbt.getInt(ROUND_INDEX_KEY)
        mutable.tournamentMatchIndex = nbt.getInt(TOURNAMENT_MATCH_INDEX_KEY)
        mutable.roundMatchIndex = nbt.getInt(ROUND_MATCH_INDEX_KEY)
        mutable.victorID = nbt.getUuidOrNull(VICTOR_ID_KEY)
        mutable.matchStatus = nbt.getConstantStrict<MatchStatus>(MATCH_STATUS_KEY)
        mutable.matchConnections = MatchConnections.loadFromNbt(
            nbt.getCompound(MATCH_CONNECTIONS_KEY)
        )
        mutable.playerMap = nbt.loadPlayersMap()
        return mutable
    }

    private fun CompoundTag.savePlayersMap(properties: MatchProperties): Tag? {
        val entryHandler = { entry: Entry<UUID, Int> ->
            CompoundTag().also { entryNbt ->
                entryNbt.putUUID(PLAYER_ID_KEY, entry.key)
                entryNbt.putInt(TEAM_INDEX_KEY, entry.value)
            }
        }
        val mapNbt = properties.playerMap.saveToNbt(entryHandler)
        return this.put(PLAYER_TEAM_MAP_KEY, mapNbt)
    }

    private fun CompoundTag.loadPlayersMap(): MutablePlayerTeamMap {
        val entryHandler = { nbt: CompoundTag ->
            nbt.getUUID(PLAYER_ID_KEY) to nbt.getInt(TEAM_INDEX_KEY)
        }
        val mapNbt = this.getCompound(PLAYER_TEAM_MAP_KEY)
        return mapNbt.loadObservableMapOf(entryHandler)
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
        val propertiesComponent = getComponent(
            text = "${properties.name} ",
            color = GREEN_FORMAT,
            bold = true,
        )
        propertiesComponent.appendWithBracketed(properties.uuid.short(), textColor = GREEN_FORMAT)

        val statusComponent = getComponent(text = "  Status ")
        statusComponent.appendWithBracketed(properties.matchStatus.name, textColor = YELLOW_FORMAT)

        val victorComponent = getComponent(text = "  Victor ")
        victorComponent.appendWithBracketed(properties.victorID.short(), textColor = YELLOW_FORMAT)

        player.displayClientMessage(propertiesComponent, false)
        player.displayClientMessage(statusComponent, false)
        player.displayClientMessage(victorComponent, false)

        if (properties.playerMap.isNotEmpty()) {
            val titleComponent = getComponent("  Players ", color = AQUA_FORMAT, bold = true)
            titleComponent.appendWithBracketed(
                text = "${properties.playerMap.size}",
                textColor = AQUA_FORMAT,
            )

            player.displayClientMessage(titleComponent, (false))

            val sorted = properties.playerMap.toList().sortedBy { (_, team) -> team }
            sorted.forEach { (playerID, team) ->
                val playerComponent = getComponent(text = "    Player ")
                playerComponent.appendWithBracketed(playerID.short(), textColor = AQUA_FORMAT)
                playerComponent.appendWith(text = " Team ")
                playerComponent.appendWithBracketed("$team", textColor = AQUA_FORMAT)
                player.displayClientMessage(playerComponent, false)
            }
        }
    }

}
