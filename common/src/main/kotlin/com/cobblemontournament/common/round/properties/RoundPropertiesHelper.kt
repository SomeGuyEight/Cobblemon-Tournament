package com.cobblemontournament.common.round.properties

import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.round.RoundType
import com.sg8.collections.reactive.map.loadMutableObservableMapOf
import com.sg8.collections.reactive.map.saveToNbt
import com.sg8.properties.PropertiesHelper
import com.sg8.util.appendWith
import com.sg8.util.appendWithBracketed
import com.sg8.util.ComponentUtil
import com.sg8.util.getConstant
import com.sg8.util.short
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID
import kotlin.collections.Map.Entry


object RoundPropertiesHelper : PropertiesHelper<RoundProperties> {

    override fun saveToNbt(properties: RoundProperties, nbt: CompoundTag): CompoundTag {
        nbt.putUUID(DataKeys.ROUND_ID, properties.uuid)
        nbt.putUUID(DataKeys.TOURNAMENT_ID, properties.tournamentID)
        nbt.putInt(DataKeys.ROUND_INDEX, properties.roundIndex)
        nbt.putString(DataKeys.ROUND_TYPE, properties.roundType.name)
        nbt.putIndexedMatchMap(properties)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): RoundProperties {
        return RoundProperties(
            uuid = nbt.getUUID(DataKeys.ROUND_ID),
            tournamentID = nbt.getUUID(DataKeys.TOURNAMENT_ID),
            roundIndex = nbt.getInt(DataKeys.ROUND_INDEX),
            roundType = nbt.getConstant<RoundType>(DataKeys.ROUND_TYPE),
            indexedMatchMap = nbt.getIndexedMatchMap(),
        )
    }

    override fun setFromNbt(mutable: RoundProperties, nbt: CompoundTag): RoundProperties {
        mutable.uuid = nbt.getUUID(DataKeys.ROUND_ID)
        mutable.tournamentID = nbt.getUUID(DataKeys.TOURNAMENT_ID)
        mutable.roundIndex = nbt.getInt(DataKeys.ROUND_INDEX)
        mutable.roundType = nbt.getConstant<RoundType>(DataKeys.ROUND_TYPE)
        mutable.indexedMatchMap = nbt.getIndexedMatchMap()
        return mutable
    }

    private fun CompoundTag.putIndexedMatchMap(properties: RoundProperties) {
        val entryHandler = { (index, matchID): Entry<Int, UUID> ->
            CompoundTag().also { pairNbt ->
                pairNbt.putInt(DataKeys.ROUND_MATCH_INDEX, index)
                pairNbt.putUUID(DataKeys.MATCH_ID, matchID)
            }
        }
        val matchMapNbt = properties.indexedMatchMap.saveToNbt(entryHandler)
        this.put(DataKeys.ROUND_MATCH_INDEX_TO_ID, matchMapNbt)
    }

    private fun CompoundTag.getIndexedMatchMap(): IndexedMatchMap {
        val entryHandler = { entryNbt: CompoundTag ->
            entryNbt.getInt(DataKeys.ROUND_MATCH_INDEX) to entryNbt.getUUID(DataKeys.MATCH_ID)
        }
        val mapNbt = this.getCompound(DataKeys.ROUND_MATCH_INDEX_TO_ID)
        return mapNbt.loadMutableObservableMapOf(entryHandler)
    }

    override fun deepCopy(properties: RoundProperties) = copy(properties)

    override fun copy(properties: RoundProperties): RoundProperties {
        return RoundProperties(
            uuid = properties.uuid,
            tournamentID = properties.tournamentID,
            roundIndex = properties.roundIndex,
            roundType = properties.roundType,
            indexedMatchMap = properties.indexedMatchMap,
        )
    }

    override fun printDebug(properties: RoundProperties) {
        Util.report("${properties.roundType} Round ${properties.roundIndex} " +
                "(${properties.indexedMatchMap.size} matches) " +
                "[${properties.uuid.short()}]")
        Util.report("- Tournament ID [${properties.tournamentID.short()}]")
    }

    override fun displayInChat(properties: RoundProperties, player: ServerPlayer) {
        val propertiesComponent = ComponentUtil.getBracketedComponent(
            text = "${properties.roundType}",
            textColor = ChatFormatting.GREEN,
        )
        propertiesComponent.appendWith(text = " \"")
        propertiesComponent.appendWith(text = "Round ", color = ChatFormatting.GREEN)
        propertiesComponent.appendWithBracketed(
            text = "${properties.roundIndex}",
            textColor = ChatFormatting.GREEN,
        )
        propertiesComponent.appendWith(text = "\" ")
        propertiesComponent.appendWithBracketed(
            text = properties.uuid.short(),
            textColor = ChatFormatting.GREEN,
        )

        player.displayClientMessage(propertiesComponent, (false))

        val matchesComponent = ComponentUtil.getComponent(text = "  Total Matches ")
        matchesComponent.appendWithBracketed(
            text = "${properties.indexedMatchMap.size}",
            textColor = ChatFormatting.YELLOW,
        )

        player.displayClientMessage(matchesComponent, false)
    }

}
