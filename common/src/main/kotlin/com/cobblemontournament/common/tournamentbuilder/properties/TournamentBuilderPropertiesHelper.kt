package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.loadPlayersFromNbt
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.savePlayersToNbt
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.util.*
import com.someguy.storage.PropertiesHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object TournamentBuilderPropertiesHelper : PropertiesHelper<TournamentBuilderProperties> {

    override fun setFromNbtHelper(
        mutable: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): TournamentBuilderProperties {
        mutable.name = nbt.getString(TOURNAMENT_BUILDER_NAME_KEY)
        mutable.tournamentBuilderID = nbt.getUUID(TOURNAMENT_BUILDER_ID_KEY)
        mutable.tournamentType = enumValueOf<TournamentType>(nbt.getString(TOURNAMENT_TYPE_KEY))
        mutable.challengeFormat = enumValueOf<ChallengeFormat>(nbt.getString(CHALLENGE_FORMAT_KEY))
        mutable.maxParticipants = nbt.getInt(MAX_PARTICIPANTS_KEY)
        mutable.teamSize = nbt.getInt(TEAM_SIZE_KEY)
        mutable.groupSize = nbt.getInt(GROUP_SIZE_KEY)
        mutable.minLevel = nbt.getInt(MIN_LEVEL_KEY)
        mutable.maxLevel = nbt.getInt(MAX_LEVEL_KEY)
        mutable.showPreview = nbt.getBoolean(SHOW_PREVIEW_KEY)
        for (playerProps in loadPlayersFromNbt(nbt = nbt.getCompound(PLAYER_SET_KEY))) {
            mutable.addPlayer(playerProps = playerProps)
        }
        return mutable
    }

    override fun deepCopyHelper(
        properties: TournamentBuilderProperties,
    ): TournamentBuilderProperties {
        return TournamentBuilderProperties(
            name = properties.name,
            tournamentBuilderID = properties.tournamentBuilderID,
            tournamentProperties = TournamentProperties(
                tournamentType = properties.tournamentType,
                challengeFormat = properties.challengeFormat,
                maxParticipants = properties.maxParticipants,
                teamSize = properties.teamSize,
                groupSize = properties.groupSize,
                minLevel = properties.minLevel,
                maxLevel = properties.maxLevel,
                showPreview = properties.showPreview,
            ),
            players = properties.getPlayersDeepCopy(),
        )
    }

    override fun saveToNbtHelper(
        properties: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): CompoundTag {
        nbt.putString(TOURNAMENT_BUILDER_NAME_KEY, properties.name)
        nbt.putUUID(TOURNAMENT_BUILDER_ID_KEY, properties.tournamentBuilderID)
        nbt.putString(TOURNAMENT_TYPE_KEY, properties.tournamentType.name)
        nbt.putString(CHALLENGE_FORMAT_KEY, properties.challengeFormat.name)
        nbt.putInt(MAX_PARTICIPANTS_KEY, properties.maxParticipants)
        nbt.putInt(TEAM_SIZE_KEY, properties.teamSize)
        nbt.putInt(GROUP_SIZE_KEY, properties.groupSize)
        nbt.putInt(MIN_LEVEL_KEY, properties.minLevel)
        nbt.putInt(MAX_LEVEL_KEY, properties.maxLevel)
        nbt.putBoolean(SHOW_PREVIEW_KEY, properties.showPreview)
        nbt.put(
            PLAYER_SET_KEY,
            savePlayersToNbt(players = properties.getPlayersIterator(), nbt = CompoundTag())
        )
        return nbt
    }

    override fun loadFromNbtHelper(nbt: CompoundTag): TournamentBuilderProperties {
        return TournamentBuilderProperties(
            name = nbt.getString(TOURNAMENT_BUILDER_NAME_KEY),
            tournamentBuilderID = nbt.getUUID(TOURNAMENT_BUILDER_ID_KEY),
            tournamentProperties = TournamentProperties(
                tournamentType = enumValueOf<TournamentType>(nbt.getString(TOURNAMENT_TYPE_KEY)),
                challengeFormat = enumValueOf<ChallengeFormat>(nbt.getString(CHALLENGE_FORMAT_KEY)),
                maxParticipants = nbt.getInt(MAX_PARTICIPANTS_KEY),
                teamSize = nbt.getInt(TEAM_SIZE_KEY),
                groupSize = nbt.getInt(GROUP_SIZE_KEY),
                minLevel = nbt.getInt(MIN_LEVEL_KEY),
                maxLevel = nbt.getInt(MAX_LEVEL_KEY),
                showPreview = nbt.getBoolean(SHOW_PREVIEW_KEY),
            ),
            players = loadPlayersFromNbt(nbt = nbt.getCompound(PLAYER_SET_KEY)),
        )
    }

    override fun logDebugHelper(properties: TournamentBuilderProperties) {
        Util.report("Tournament Builder \"${properties.name}\" " +
                "[${properties.tournamentBuilderID.shortUUID()}]")
        properties.printTournamentProperties()
        Util.report("  Players:")
        for (player in properties.getPlayersSortedBy { it.seed }) {
            player.logDebug()
        }
    }

    override fun displayInChatHelper(
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
            textColor = ChatUtil.PURPLE_FORMAT,
            padding = 0 to 1,
            bold = true,
        )
        titleComponent.appendWithBracketed(
            text = properties.tournamentBuilderID.shortUUID(),
            textColor = ChatUtil.PURPLE_FORMAT,
        )

        player.displayClientMessage(titleComponent, false)

        properties.displayTournamentPropertiesInChat(player = player)

        val playerComponent = getComponent(text = "  Player Count ")
        playerComponent.appendWithBracketed(
            text = properties.getPlayersSize().toString(),
            textColor = ChatUtil.YELLOW_FORMAT,
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
