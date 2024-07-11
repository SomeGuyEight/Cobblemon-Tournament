package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.deepCopyPlayers
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.loadPlayersFromNBT
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.savePlayersToNBT
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.cobblemontournament.common.util.ChatUtil
import com.someguy.storage.properties.PropertiesHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object TournamentBuilderPropertiesHelper : PropertiesHelper <TournamentBuilderProperties>
{
    const val DEFAULT_TOURNAMENT_BUILDER_NAME = "Tournament Builder"

    override fun deepCopyHelper(
        properties : TournamentBuilderProperties
    ): TournamentBuilderProperties
    {
        return TournamentBuilderProperties(
            name                    = properties.name,
            tournamentBuilderID     = properties.tournamentBuilderID,
            tournamentType          = properties.tournamentProperties.tournamentType,
            challengeFormat         = properties.tournamentProperties.challengeFormat,
            maxParticipants         = properties.tournamentProperties.maxParticipants,
            teamSize                = properties.tournamentProperties.teamSize,
            groupSize               = properties.tournamentProperties.groupSize,
            minLevel                = properties.tournamentProperties.minLevel,
            maxLevel                = properties.tournamentProperties.maxLevel,
            showPreview             = properties.tournamentProperties.showPreview,
            seededPlayers           = deepCopyPlayers(properties.seededPlayers),
            unseededPlayers         = deepCopyPlayers(properties.seededPlayers))
    }

    override fun setFromPropertiesHelper(
        mutable: TournamentBuilderProperties,
        from: TournamentBuilderProperties
    ): TournamentBuilderProperties
    {
        mutable.name                    = from.name
        mutable.tournamentBuilderID     = from.tournamentBuilderID
        mutable.seededPlayers           = deepCopyPlayers(from.seededPlayers)
        mutable.unseededPlayers         = deepCopyPlayers(from.unseededPlayers)
        mutable.tournamentProperties.tournamentType     = from.tournamentProperties.tournamentType
        mutable.tournamentProperties.challengeFormat    = from.tournamentProperties.challengeFormat
        mutable.tournamentProperties.maxParticipants    = from.tournamentProperties.maxParticipants
        mutable.tournamentProperties.teamSize           = from.tournamentProperties.teamSize
        mutable.tournamentProperties.groupSize          = from.tournamentProperties.groupSize
        mutable.tournamentProperties.minLevel           = from.tournamentProperties.minLevel
        mutable.tournamentProperties.maxLevel           = from.tournamentProperties.maxLevel
        mutable.tournamentProperties.showPreview        = from.tournamentProperties.showPreview
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): TournamentBuilderProperties
    {
        mutable.name                    = nbt.getString(        DataKeys.TOURNAMENT_BUILDER_NAME)
        mutable.tournamentBuilderID     = nbt.getUUID(          DataKeys.TOURNAMENT_BUILDER_ID)
        mutable.seededPlayers           = loadPlayersFromNBT(   nbt.getCompound(DataKeys.SEEDED_PLAYERS))
        mutable.unseededPlayers         = loadPlayersFromNBT(   nbt.getCompound(DataKeys.UNSEEDED_PLAYERS))
        mutable.tournamentProperties.tournamentType     = enumValueOf<TournamentType>( nbt.getString(DataKeys.TOURNAMENT_TYPE))
        mutable.tournamentProperties.challengeFormat    = enumValueOf<ChallengeFormat>( nbt.getString(DataKeys.CHALLENGE_FORMAT))
        mutable.tournamentProperties.maxParticipants    = nbt.getInt(           DataKeys.MAX_PARTICIPANTS)
        mutable.tournamentProperties.teamSize           = nbt.getInt(           DataKeys.TEAM_SIZE)
        mutable.tournamentProperties.groupSize          = nbt.getInt(           DataKeys.GROUP_SIZE)
        mutable.tournamentProperties.minLevel           = nbt.getInt(           DataKeys.MIN_LEVEL)
        mutable.tournamentProperties.maxLevel           = nbt.getInt(           DataKeys.MAX_LEVEL)
        mutable.tournamentProperties.showPreview        = nbt.getBoolean(       DataKeys.SHOW_PREVIEW)
        return mutable
    }

    override fun saveToNBTHelper(
        properties: TournamentBuilderProperties,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putString(  DataKeys.TOURNAMENT_BUILDER_NAME  , properties.name)
        nbt.putUUID(    DataKeys.TOURNAMENT_BUILDER_ID    , properties.tournamentBuilderID)
        nbt.put( DataKeys.SEEDED_PLAYERS, savePlayersToNBT( properties.seededPlayers,CompoundTag()))
        nbt.put( DataKeys.UNSEEDED_PLAYERS, savePlayersToNBT( properties.unseededPlayers,CompoundTag()))
        nbt.putString(  DataKeys.TOURNAMENT_TYPE  , properties.tournamentProperties.tournamentType.name)
        nbt.putString(  DataKeys.CHALLENGE_FORMAT , properties.tournamentProperties.challengeFormat.name)
        nbt.putInt(     DataKeys.MAX_PARTICIPANTS , properties.tournamentProperties.maxParticipants)
        nbt.putInt(     DataKeys.TEAM_SIZE        , properties.tournamentProperties.teamSize)
        nbt.putInt(     DataKeys.GROUP_SIZE       , properties.tournamentProperties.groupSize)
        nbt.putInt(     DataKeys.MIN_LEVEL        , properties.tournamentProperties.minLevel)
        nbt.putInt(     DataKeys.MAX_LEVEL        , properties.tournamentProperties.maxLevel)
        nbt.putBoolean( DataKeys.SHOW_PREVIEW     , properties.tournamentProperties.showPreview)
        return nbt
    }

    override fun loadFromNBTHelper(
        nbt: CompoundTag
    ): TournamentBuilderProperties
    {
        return TournamentBuilderProperties(
            name                    = nbt.getString(        DataKeys.TOURNAMENT_BUILDER_NAME),
            tournamentBuilderID     = nbt.getUUID(          DataKeys.TOURNAMENT_BUILDER_ID),
            tournamentType          = enumValueOf<TournamentType>( nbt.getString(DataKeys.TOURNAMENT_TYPE)),
            challengeFormat         = enumValueOf<ChallengeFormat>( nbt.getString(DataKeys.CHALLENGE_FORMAT)),
            maxParticipants         = nbt.getInt(           DataKeys.MAX_PARTICIPANTS),
            teamSize                = nbt.getInt(           DataKeys.TEAM_SIZE),
            groupSize               = nbt.getInt(           DataKeys.GROUP_SIZE),
            minLevel                = nbt.getInt(           DataKeys.MIN_LEVEL),
            maxLevel                = nbt.getInt(           DataKeys.MAX_LEVEL),
            showPreview             = nbt.getBoolean(       DataKeys.SHOW_PREVIEW),
            seededPlayers           = loadPlayersFromNBT(   nbt.getCompound(DataKeys.SEEDED_PLAYERS)),
            unseededPlayers         = loadPlayersFromNBT(   nbt.getCompound(DataKeys.UNSEEDED_PLAYERS)),
        )
    }

    override fun logDebugHelper(properties: TournamentBuilderProperties)
    {
        Util.report("Tournament Builder \"${properties.name}\" [${ChatUtil.shortUUID( properties.tournamentBuilderID )}]")
        properties.tournamentProperties.logDebug()
        Util.report("  Seeded Players:")
        properties.seededPlayers.forEach { it.logDebug() }
        Util.report("  Unseeded Players:")
        properties.unseededPlayers.forEach { it.logDebug() }
    }

    override fun displayInChatHelper(
        properties: TournamentBuilderProperties,
        player: ServerPlayer)
    {
        ChatUtil.displayInPlayerChat(
            player  = player,
            text    = "Tournament Builder \"${properties.name}\" [${ChatUtil.shortUUID( properties.tournamentBuilderID )}]",
            color   = ChatUtil.purple,
            bold    = true)
        properties.tournamentProperties.displaySlimInChat(player)
        val seededSize = properties.seededPlayers.size
        val unseededSize = properties.unseededPlayers.size
        val component0 = ChatUtil.formatText( text = "  Player Count " )
        component0.append( ChatUtil.formatTextBracketed( text = "${seededSize + unseededSize}", color = ChatUtil.yellow ) )
        component0.append( ChatUtil.formatText( text = ": Seeded " ) )
        component0.append( ChatUtil.formatTextBracketed( text = "$seededSize", color = ChatUtil.yellow ) )
        component0.append( ChatUtil.formatText( text = " Unseeded " ) )
        component0.append( ChatUtil.formatTextBracketed( text = "$unseededSize", color = ChatUtil.yellow ) )
        player.displayClientMessage( component0, false )
        val component1 = ChatUtil.formatText( text = "  Seeded:", ChatUtil.aqua )
        player.displayClientMessage( component1, false )
        for (playerProps in properties.seededPlayers ) {
            PlayerPropertiesHelper.displayInChatOptionalHelper(
                properties  = playerProps,
                player      = player,
                spacing     = "  ",
                displaySeed = true )
        }
        val component2 = ChatUtil.formatText( text = "  Unseeded:", ChatUtil.aqua )
        player.displayClientMessage( component2, false )
        for (playerProps in properties.unseededPlayers ) {
            PlayerPropertiesHelper.displayInChatOptionalHelper(
                properties  = playerProps,
                player      = player,
                spacing     = "  ",
                displaySeed = true )
        }
    }

}
