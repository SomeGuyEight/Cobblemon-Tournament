package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.sg8.collections.reactive.set.loadObservableSetOf
import com.sg8.collections.reactive.set.observableSetOf
import com.sg8.collections.reactive.set.saveToNbt
import com.sg8.properties.PropertiesHelper
import com.sg8.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object TournamentBuilderPropertiesHelper : PropertiesHelper<TournamentBuilderProperties> {

    override fun saveToNbt(
        properties: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): CompoundTag {
        nbt.putString(TOURNAMENT_BUILDER_NAME_KEY, properties.name)
        nbt.putUUID(TOURNAMENT_BUILDER_ID_KEY, properties.uuid)
        nbt.put(TOURNAMENT_PROPERTIES_KEY, properties.tournamentProperties.saveToNbt(nbt))
        nbt.putPlayersSet(properties)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): TournamentBuilderProperties {
        return TournamentBuilderProperties(
            name = nbt.getString(TOURNAMENT_BUILDER_NAME_KEY),
            uuid = nbt.getUUID(TOURNAMENT_BUILDER_ID_KEY),
            tournamentProperties = TournamentProperties.loadFromNbt(
                nbt.getCompound(TOURNAMENT_PROPERTIES_KEY)
            ),
            playerSet = nbt.getPlayersSet(),
        )
    }

    override fun setFromNbt(
        mutable: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): TournamentBuilderProperties {
        mutable.name = nbt.getString(TOURNAMENT_BUILDER_NAME_KEY)
        mutable.uuid = nbt.getUUID(TOURNAMENT_BUILDER_ID_KEY)
        mutable.tournamentProperties = TournamentProperties.loadFromNbt(
            nbt.getCompound(TOURNAMENT_PROPERTIES_KEY)
        )
        val playersSet = nbt.getPlayersSet()
        mutable.playerSet.retainAll(playersSet)
        mutable.playerSet.addAll(playersSet)
        return mutable
    }

    private fun CompoundTag.putPlayersSet(properties: TournamentBuilderProperties): Tag? {
        val elementHandler = { player: PlayerProperties -> player.saveToNbt(CompoundTag()) }
        val playersSetNbt =  properties.playerSet.saveToNbt(elementHandler)
        return this.put(PLAYER_SET_KEY, playersSetNbt)
    }

    private fun CompoundTag.getPlayersSet(): MutablePlayersSet {
        val elementHandler = { nbt: CompoundTag -> PlayerProperties.loadFromNbt(nbt) }
        val playersSetNbt = this.getCompound(PLAYER_SET_KEY)
        return playersSetNbt.loadObservableSetOf(elementHandler)
    }

    override fun deepCopy(properties: TournamentBuilderProperties): TournamentBuilderProperties {
        val playersCopy = observableSetOf<PlayerProperties, MutablePlayersSet>()
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
        val titleComponent = getComponent(text = "Tournament Builder ")
        titleComponent.appendWithQuoted(
            text = properties.name,
            textColor = PURPLE_FORMAT,
            padding = 0 to 1,
            bold = true,
        )
        titleComponent.appendWithBracketed(
            text = properties.uuid.short(),
            textColor = PURPLE_FORMAT,
        )

        player.displayClientMessage(titleComponent, false)

        properties.displayTournamentPropertiesInChat(player = player)

        val playerComponent = getComponent(text = "  Player Count ")
        playerComponent.appendWithBracketed(
            text = properties.playerSet.size.toString(),
            textColor = YELLOW_FORMAT,
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
