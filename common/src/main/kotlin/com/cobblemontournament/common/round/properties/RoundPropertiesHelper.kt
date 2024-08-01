package com.cobblemontournament.common.round.properties

import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.round.RoundType
import com.sg8.properties.PropertiesHelper
import com.sg8.collections.reactive.map.loadObservableMapOf
import com.sg8.collections.reactive.map.saveToNbt
import com.sg8.collections.reactive.set.saveToNbt
import com.sg8.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID
import kotlin.collections.Map.Entry


object RoundPropertiesHelper : PropertiesHelper<RoundProperties> {

    override fun saveToNbt(properties: RoundProperties, nbt: CompoundTag): CompoundTag {
        nbt.putUUID(ROUND_ID_KEY, properties.uuid)
        nbt.putUUID(TOURNAMENT_ID_KEY, properties.tournamentID)
        nbt.putInt(ROUND_INDEX_KEY, properties.roundIndex)
        nbt.putString(ROUND_TYPE_KEY, properties.roundType.name)
        nbt.putIndexedMatchMap(properties)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): RoundProperties {
        return RoundProperties(
            uuid = nbt.getUUID(ROUND_ID_KEY),
            tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY),
            roundIndex = nbt.getInt(ROUND_INDEX_KEY),
            roundType = nbt.getConstant<RoundType>(ROUND_TYPE_KEY),
            indexedMatchMap = nbt.getIndexedMatchMap(),
        )
    }

    override fun setFromNbt(mutable: RoundProperties, nbt: CompoundTag): RoundProperties {
        mutable.uuid = nbt.getUUID(ROUND_ID_KEY)
        mutable.tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.roundIndex = nbt.getInt(ROUND_INDEX_KEY)
        mutable.roundType = nbt.getConstant<RoundType>(ROUND_TYPE_KEY)
        mutable.indexedMatchMap = nbt.getIndexedMatchMap()
        return mutable
    }

    private fun CompoundTag.putIndexedMatchMap(properties: RoundProperties) {
        val entryHandler = { (index, matchID): Entry<Int, UUID> ->
            CompoundTag().also { pairNbt ->
                pairNbt.putInt(ROUND_MATCH_INDEX_KEY, index)
                pairNbt.putUUID(MATCH_ID_KEY, matchID)
            }
        }
        val matchMapNbt = properties.indexedMatchMap.saveToNbt(entryHandler)
        this.put(ROUND_MATCH_INDEX_TO_ID_KEY, matchMapNbt)
    }

    private fun CompoundTag.getIndexedMatchMap(): IndexedMatchMap {
        val entryHandler = { entryNbt: CompoundTag ->
            entryNbt.getInt(ROUND_MATCH_INDEX_KEY) to entryNbt.getUUID(MATCH_ID_KEY)
        }
        val mapNbt = this.getCompound(ROUND_MATCH_INDEX_TO_ID_KEY)
        return mapNbt.loadObservableMapOf(entryHandler)
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
        val propertiesComponent = getBracketedComponent(
            text = "${properties.roundType}",
            textColor = GREEN_FORMAT,
        )
        propertiesComponent.appendWith(text = " \"")
        propertiesComponent.appendWith(text = "Round ", color = GREEN_FORMAT)
        propertiesComponent.appendWithBracketed(
            text = "${properties.roundIndex}",
            textColor = GREEN_FORMAT,
        )
        propertiesComponent.appendWith(text = "\" ")
        propertiesComponent.appendWithBracketed(
            text = properties.uuid.short(),
            textColor = GREEN_FORMAT,
        )

        player.displayClientMessage(propertiesComponent, (false))

        val matchesComponent = getComponent(text = "  Total Matches ")
        matchesComponent.appendWithBracketed(
            text = "${properties.indexedMatchMap.size}",
            textColor = YELLOW_FORMAT,
        )

        player.displayClientMessage(matchesComponent, false)
    }

}
