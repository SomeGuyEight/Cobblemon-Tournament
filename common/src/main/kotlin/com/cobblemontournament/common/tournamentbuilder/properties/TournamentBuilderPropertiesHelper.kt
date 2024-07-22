package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.api.storage.TournamentDataKeys.CHALLENGE_FORMAT_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.GROUP_SIZE_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MAX_LEVEL_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MAX_PARTICIPANTS_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.MIN_LEVEL_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.PLAYER_SET_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.SHOW_PREVIEW_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TEAM_SIZE_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_BUILDER_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_BUILDER_NAME_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_TYPE_KEY
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.deepCopyPlayers
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.loadPlayersFromNbt
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.savePlayersToNbt
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.cobblemontournament.common.util.ChatUtil
import com.someguy.storage.properties.PropertiesHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object TournamentBuilderPropertiesHelper : PropertiesHelper<TournamentBuilderProperties> {

    const val DEFAULT_TOURNAMENT_BUILDER_NAME = "Tournament Builder"

    override fun deepCopyHelper(
        properties: TournamentBuilderProperties
    ): TournamentBuilderProperties {
        return TournamentBuilderProperties(
            name = properties.name,
            tournamentBuilderID = properties.tournamentBuilderID,
            tournamentType = properties.tournamentProperties.tournamentType,
            challengeFormat = properties.tournamentProperties.challengeFormat,
            maxParticipants = properties.tournamentProperties.maxParticipants,
            teamSize = properties.tournamentProperties.teamSize,
            groupSize = properties.tournamentProperties.groupSize,
            minLevel = properties.tournamentProperties.minLevel,
            maxLevel = properties.tournamentProperties.maxLevel,
            showPreview = properties.tournamentProperties.showPreview,
            players = properties.getPlayersDeepCopy(),
        )
    }

    override fun setFromPropertiesHelper(
        mutable: TournamentBuilderProperties,
        from: TournamentBuilderProperties,
    ): TournamentBuilderProperties {
        mutable.name = from.name
        mutable.tournamentBuilderID = from.tournamentBuilderID
        mutable.tournamentProperties.tournamentType = from.tournamentProperties.tournamentType
        mutable.tournamentProperties.challengeFormat = from.tournamentProperties.challengeFormat
        mutable.tournamentProperties.maxParticipants = from.tournamentProperties.maxParticipants
        mutable.tournamentProperties.teamSize = from.tournamentProperties.teamSize
        mutable.tournamentProperties.groupSize = from.tournamentProperties.groupSize
        mutable.tournamentProperties.minLevel = from.tournamentProperties.minLevel
        mutable.tournamentProperties.maxLevel = from.tournamentProperties.maxLevel
        mutable.tournamentProperties.showPreview = from.tournamentProperties.showPreview
        for (playerProps in deepCopyPlayers(players = from.getPlayersDeepCopy())) {
            mutable.addPlayer(playerProps = playerProps)
        }
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): TournamentBuilderProperties {
        mutable.name = nbt.getString(TOURNAMENT_BUILDER_NAME_KEY)
        mutable.tournamentBuilderID = nbt.getUUID(TOURNAMENT_BUILDER_ID_KEY)
        mutable.tournamentProperties.tournamentType = enumValueOf<TournamentType>(nbt.getString(TOURNAMENT_TYPE_KEY))
        mutable.tournamentProperties.challengeFormat = enumValueOf<ChallengeFormat>(nbt.getString(CHALLENGE_FORMAT_KEY))
        mutable.tournamentProperties.maxParticipants = nbt.getInt(MAX_PARTICIPANTS_KEY)
        mutable.tournamentProperties.teamSize = nbt.getInt(TEAM_SIZE_KEY)
        mutable.tournamentProperties.groupSize = nbt.getInt(GROUP_SIZE_KEY)
        mutable.tournamentProperties.minLevel = nbt.getInt(MIN_LEVEL_KEY)
        mutable.tournamentProperties.maxLevel = nbt.getInt(MAX_LEVEL_KEY)
        mutable.tournamentProperties.showPreview = nbt.getBoolean(SHOW_PREVIEW_KEY)
        for (playerProps in loadPlayersFromNbt(nbt = nbt.getCompound(PLAYER_SET_KEY))) {
            mutable.addPlayer(playerProps = playerProps)
        }
        return mutable
    }

    override fun saveToNBTHelper(
        properties: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): CompoundTag {
        nbt.putString(TOURNAMENT_BUILDER_NAME_KEY, properties.name)
        nbt.putUUID(TOURNAMENT_BUILDER_ID_KEY, properties.tournamentBuilderID)
        nbt.putString(TOURNAMENT_TYPE_KEY, properties.tournamentProperties.tournamentType.name)
        nbt.putString(CHALLENGE_FORMAT_KEY, properties.tournamentProperties.challengeFormat.name)
        nbt.putInt(MAX_PARTICIPANTS_KEY, properties.tournamentProperties.maxParticipants)
        nbt.putInt(TEAM_SIZE_KEY, properties.tournamentProperties.teamSize)
        nbt.putInt(GROUP_SIZE_KEY, properties.tournamentProperties.groupSize)
        nbt.putInt(MIN_LEVEL_KEY, properties.tournamentProperties.minLevel)
        nbt.putInt(MAX_LEVEL_KEY, properties.tournamentProperties.maxLevel)
        nbt.putBoolean(SHOW_PREVIEW_KEY, properties.tournamentProperties.showPreview)
        nbt.put(PLAYER_SET_KEY, savePlayersToNbt(players = properties.getPlayersIterator(), nbt = CompoundTag()))
        return nbt
    }

    override fun loadFromNBTHelper(nbt: CompoundTag): TournamentBuilderProperties {
        return TournamentBuilderProperties(
            name = nbt.getString(TOURNAMENT_BUILDER_NAME_KEY),
            tournamentBuilderID = nbt.getUUID(TOURNAMENT_BUILDER_ID_KEY),
            tournamentType = enumValueOf<TournamentType>(nbt.getString(TOURNAMENT_TYPE_KEY)),
            challengeFormat = enumValueOf<ChallengeFormat>(nbt.getString(CHALLENGE_FORMAT_KEY)),
            maxParticipants = nbt.getInt(MAX_PARTICIPANTS_KEY),
            teamSize = nbt.getInt(TEAM_SIZE_KEY),
            groupSize = nbt.getInt(GROUP_SIZE_KEY),
            minLevel = nbt.getInt(MIN_LEVEL_KEY),
            maxLevel = nbt.getInt(MAX_LEVEL_KEY),
            showPreview = nbt.getBoolean(SHOW_PREVIEW_KEY),
            players = loadPlayersFromNbt(nbt = nbt.getCompound(PLAYER_SET_KEY)),
        )
    }

    override fun logDebugHelper(properties: TournamentBuilderProperties) {
        Util.report(("Tournament Builder \"${properties.name}\" [${ChatUtil.shortUUID(uuid = properties.tournamentBuilderID)}]"))
        properties.tournamentProperties.logDebug()
        Util.report(("  Players:"))
        for (player in properties.getPlayersSortedBy { it.seed }) {
            player.logDebug()
        }
    }

    override fun displayInChatHelper(
        properties: TournamentBuilderProperties,
        player: ServerPlayer,
    ) {
        displayInChatSlimHelper(properties = properties, player = player)
        displayPlayersInChatHelper(properties = properties, player = player)
    }

    fun displayInChatSlimHelper(
        properties: TournamentBuilderProperties,
        player: ServerPlayer,
    ) {
        val title = ChatUtil.formatText(text = "Tournament Builder ")
        title.append(ChatUtil.formatTextQuoted(
            text = properties.name,
            color = ChatUtil.purple,
            spacingAfter = " ",
            bold = true,
        ))
        title.append(ChatUtil.formatTextBracketed(
            text = ChatUtil.shortUUID(uuid = properties.tournamentBuilderID),
            color = ChatUtil.purple,
        ))
        player.displayClientMessage(title, (false))

        properties.tournamentProperties.displaySlimInChat(player = player)

        val playerText = ChatUtil.formatText(text = "  Player Count ")
        playerText.append(ChatUtil.formatTextBracketed(
            text = properties.getPlayersSize().toString(),
            color = ChatUtil.yellow,
        ))
        player.displayClientMessage(playerText, (false))
    }

    private fun displayPlayersInChatHelper(
        properties: TournamentBuilderProperties,
        player: ServerPlayer
    ) {
        val display: (PlayerProperties) -> Unit = { props ->
            PlayerPropertiesHelper.displayInChatOptionalHelper(
                properties = props,
                player = player,
                spacing = "  ",
                displaySeed = true,
            )
        }
        properties.getSeededPlayers().forEach { display(it) }
        properties.getUnseededPlayers().forEach { display(it) }
    }

}
