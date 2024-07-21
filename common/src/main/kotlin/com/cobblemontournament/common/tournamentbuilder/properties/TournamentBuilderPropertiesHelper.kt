package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.deepCopyPlayers
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.loadPlayersFromNBT
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.savePlayersToNBT
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.player.properties.PlayerProperties
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
            players                 = deepCopyPlayers( properties.getPlayersDeepCopy() ) )
    }

    override fun setFromPropertiesHelper(
        mutable: TournamentBuilderProperties,
        from: TournamentBuilderProperties
    ): TournamentBuilderProperties
    {
        mutable.name                                    = from.name
        mutable.tournamentBuilderID                     = from.tournamentBuilderID
        mutable.tournamentProperties.tournamentType     = from.tournamentProperties.tournamentType
        mutable.tournamentProperties.challengeFormat    = from.tournamentProperties.challengeFormat
        mutable.tournamentProperties.maxParticipants    = from.tournamentProperties.maxParticipants
        mutable.tournamentProperties.teamSize           = from.tournamentProperties.teamSize
        mutable.tournamentProperties.groupSize          = from.tournamentProperties.groupSize
        mutable.tournamentProperties.minLevel           = from.tournamentProperties.minLevel
        mutable.tournamentProperties.maxLevel           = from.tournamentProperties.maxLevel
        mutable.tournamentProperties.showPreview        = from.tournamentProperties.showPreview
        for ( player in deepCopyPlayers( from.getPlayersDeepCopy() ) ) {
            mutable.addPlayer( player )
        }
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: TournamentBuilderProperties,
        nbt: CompoundTag,
    ): TournamentBuilderProperties
    {
        mutable.name                                    = nbt.getString(    DataKeys.TOURNAMENT_BUILDER_NAME )
        mutable.tournamentBuilderID                     = nbt.getUUID(      DataKeys.TOURNAMENT_BUILDER_ID )
        mutable.tournamentProperties.tournamentType     = enumValueOf <TournamentType>( nbt.getString( DataKeys.TOURNAMENT_TYPE ) )
        mutable.tournamentProperties.challengeFormat    = enumValueOf <ChallengeFormat>( nbt.getString( DataKeys.CHALLENGE_FORMAT ) )
        mutable.tournamentProperties.maxParticipants    = nbt.getInt(       DataKeys.MAX_PARTICIPANTS )
        mutable.tournamentProperties.teamSize           = nbt.getInt(       DataKeys.TEAM_SIZE )
        mutable.tournamentProperties.groupSize          = nbt.getInt(       DataKeys.GROUP_SIZE )
        mutable.tournamentProperties.minLevel           = nbt.getInt(       DataKeys.MIN_LEVEL )
        mutable.tournamentProperties.maxLevel           = nbt.getInt(       DataKeys.MAX_LEVEL )
        mutable.tournamentProperties.showPreview        = nbt.getBoolean(   DataKeys.SHOW_PREVIEW )
        for ( player in loadPlayersFromNBT( nbt.getCompound( DataKeys.PLAYER_SET ) ) ) {
            mutable.addPlayer( player )
        }
        return mutable
    }

    override fun saveToNBTHelper(
        properties: TournamentBuilderProperties,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putString(  DataKeys.TOURNAMENT_BUILDER_NAME  , properties.name)
        nbt.putUUID(    DataKeys.TOURNAMENT_BUILDER_ID    , properties.tournamentBuilderID)
        nbt.putString(  DataKeys.TOURNAMENT_TYPE    , properties.tournamentProperties.tournamentType.name)
        nbt.putString(  DataKeys.CHALLENGE_FORMAT   , properties.tournamentProperties.challengeFormat.name)
        nbt.putInt(     DataKeys.MAX_PARTICIPANTS   , properties.tournamentProperties.maxParticipants)
        nbt.putInt(     DataKeys.TEAM_SIZE          , properties.tournamentProperties.teamSize)
        nbt.putInt(     DataKeys.GROUP_SIZE         , properties.tournamentProperties.groupSize)
        nbt.putInt(     DataKeys.MIN_LEVEL          , properties.tournamentProperties.minLevel)
        nbt.putInt(     DataKeys.MAX_LEVEL          , properties.tournamentProperties.maxLevel)
        nbt.putBoolean( DataKeys.SHOW_PREVIEW       , properties.tournamentProperties.showPreview)
        nbt.put( DataKeys.PLAYER_SET, savePlayersToNBT( properties.getPlayersIterator(), CompoundTag() ) )
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
            players                 = loadPlayersFromNBT(   nbt.getCompound( DataKeys.PLAYER_SET ) ) )

    }

    override fun logDebugHelper( properties: TournamentBuilderProperties )
    {
        Util.report("Tournament Builder \"${ properties.name }\" [${ ChatUtil.shortUUID( properties.tournamentBuilderID ) }]")
        properties.tournamentProperties.logDebug()
        Util.report("  Players:")
        for ( player in properties.getPlayersSortedBy { it.seed } ) {
            player.logDebug()
        }
    }

    override fun displayInChatHelper(
        properties: TournamentBuilderProperties,
        player: ServerPlayer)
    {
        displayInChatSlimHelper( properties = properties, player = player )
        displayPlayersInChatHelper( properties = properties, player = player )
    }

    fun displayInChatSlimHelper(
        properties  : TournamentBuilderProperties,
        player      : ServerPlayer )
    {
        val component0 = ChatUtil.formatText(
            text    = "Tournament Builder " )
        component0.append( ChatUtil.formatTextQuoted(
            text    = properties.name,
            color   = ChatUtil.purple,
            spacingAfter = " ",
            bold    = true ) )
        component0.append( ChatUtil.formatTextBracketed(
            text    = ChatUtil.shortUUID( properties.tournamentBuilderID ),
            color   = ChatUtil.purple ) )
        player.displayClientMessage( component0, false )

        properties.tournamentProperties.displaySlimInChat( player )

        val component1 = ChatUtil.formatText(
            text    = "  Player Count " )
        component1.append( ChatUtil.formatTextBracketed(
            text    = properties.getPlayersSize().toString(),
            color   = ChatUtil.yellow ) )
        player.displayClientMessage( component1, false )
    }

    private fun displayPlayersInChatHelper(
        properties  : TournamentBuilderProperties,
        player      : ServerPlayer )
    {
        val display: (PlayerProperties) -> Unit = { props ->
            PlayerPropertiesHelper.displayInChatOptionalHelper(
                properties  = props,
                player      = player,
                spacing     = "  ",
                displaySeed = true )
        }
        properties.getSeededPlayers().forEach   { display( it ) }
        properties.getUnseededPlayers().forEach { display( it ) }
    }

}
