package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.sg8.collections.reactive.set.loadMutableObservableSetOf
import com.sg8.collections.reactive.set.mutableObservableSetOf
import com.sg8.collections.reactive.set.saveToNbt
import com.sg8.properties.PropertiesHelper
import com.sg8.util.appendWithBracketed
import com.sg8.util.appendWithQuoted
import com.sg8.util.ComponentUtil
import com.sg8.util.short
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util


object TournamentBuilderPropertiesHelper : PropertiesHelper<TournamentBuilderProperties> {

    override fun saveToNbt(
        properties: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): CompoundTag {
        nbt.putString(DataKeys.TOURNAMENT_BUILDER_NAME, properties.name)
        nbt.putUUID(DataKeys.TOURNAMENT_BUILDER_ID, properties.uuid)
        nbt.put(DataKeys.TOURNAMENT_PROPERTIES, properties.tournamentProperties.saveToNbt(nbt))
        nbt.putPlayersSet(properties)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): TournamentBuilderProperties {
        return TournamentBuilderProperties(
            name = nbt.getString(DataKeys.TOURNAMENT_BUILDER_NAME),
            uuid = nbt.getUUID(DataKeys.TOURNAMENT_BUILDER_ID),
            tournamentProperties = TournamentProperties.loadFromNbt(
                nbt.getCompound(DataKeys.TOURNAMENT_PROPERTIES)
            ),
            playerSet = nbt.getPlayersSet(),
        )
    }

    override fun setFromNbt(
        mutable: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): TournamentBuilderProperties {
        mutable.name = nbt.getString(DataKeys.TOURNAMENT_BUILDER_NAME)
        mutable.uuid = nbt.getUUID(DataKeys.TOURNAMENT_BUILDER_ID)
        mutable.tournamentProperties = TournamentProperties.loadFromNbt(
            nbt.getCompound(DataKeys.TOURNAMENT_PROPERTIES)
        )
        val playersSet = nbt.getPlayersSet()
        mutable.playerSet.retainAll(playersSet)
        mutable.playerSet.addAll(playersSet)
        return mutable
    }

    private fun CompoundTag.putPlayersSet(properties: TournamentBuilderProperties): Tag? {
        val elementHandler = { player: PlayerProperties -> player.saveToNbt(CompoundTag()) }
        val playersSetNbt =  properties.playerSet.saveToNbt(elementHandler)
        return this.put(DataKeys.PLAYER_SET, playersSetNbt)
    }

    private fun CompoundTag.getPlayersSet(): MutablePlayersSet {
        val elementHandler = { nbt: CompoundTag -> PlayerProperties.loadFromNbt(nbt) }
        val playersSetNbt = this.getCompound(DataKeys.PLAYER_SET)
        return playersSetNbt.loadMutableObservableSetOf(elementHandler)
    }

    override fun deepCopy(properties: TournamentBuilderProperties): TournamentBuilderProperties {
        val playersCopy = mutableObservableSetOf<PlayerProperties>()
        properties.playerSet.forEach { playersCopy.add(it.deepCopy()) }
        properties.playerSet.clear()
        properties.playerSet.addAll(playersCopy)
        return copy(properties)
    }

    override fun copy(properties: TournamentBuilderProperties): TournamentBuilderProperties {
        return TournamentBuilderProperties(
            name = properties.name,
            uuid = properties.uuid,
            tournamentProperties = properties.tournamentProperties,
            playerSet = properties.playerSet,
        )
    }

    override fun printDebug(properties: TournamentBuilderProperties) {
        Util.report("Tournament Builder \"${properties.name}\" " +
                "[${properties.uuid.short()}]")
        properties.printTournamentProperties()
        Util.report("  Players:")
        for (player in properties.getPlayersSortedBy { it.seed }) {
            player.printDebug()
        }
    }

    override fun displayInChat(
        properties: TournamentBuilderProperties,
        player: ServerPlayer,
    ) {
        displayShortenedInChatHelper(properties = properties, player = player)
        displayPlayersInChatHelper(properties = properties, player = player)
    }

    fun displayShortenedInChatHelper(
        properties: TournamentBuilderProperties,
        player: ServerPlayer,
    ) {
        val titleComponent = ComponentUtil.getComponent(text = "Tournament Builder ")
        titleComponent.appendWithQuoted(
            text = properties.name,
            textColor = ChatFormatting.LIGHT_PURPLE,
            padding = 0 to 1,
            bold = true,
        )
        titleComponent.appendWithBracketed(
            text = properties.uuid.short(),
            textColor = ChatFormatting.LIGHT_PURPLE,
        )

        player.displayClientMessage(titleComponent, false)

        properties.displayTournamentPropertiesInChat(player = player)

        val playerComponent = ComponentUtil.getComponent(text = "  Player Count ")
        playerComponent.appendWithBracketed(
            text = properties.playerSet.size.toString(),
            textColor = ChatFormatting.YELLOW,
        )

        player.displayClientMessage(playerComponent, false)
    }

    private fun displayPlayersInChatHelper(
        properties: TournamentBuilderProperties,
        player: ServerPlayer
    ) {
        val display: (PlayerProperties) -> Unit = { props ->
            PlayerPropertiesHelper.displayInChatHelper(
                properties = props,
                player = player,
                padStart = 2,
                displaySeed = true,
            )
        }

        properties.getSeededPlayers().forEach { display(it) }
        properties.getUnseededPlayers().forEach { display(it) }
    }
}
