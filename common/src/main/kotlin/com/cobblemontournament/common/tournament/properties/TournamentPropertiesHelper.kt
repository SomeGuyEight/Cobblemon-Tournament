package com.cobblemontournament.common.tournament.properties

import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.util.TournamentDataKeys
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat
import net.minecraft.nbt.CompoundTag
import java.util.*

object TournamentPropertiesHelper: PropertiesHelper <TournamentPropertyFields, TournamentProperties, MutableTournamentProperties>
{
    const val DEFAULT_TOURNAMENT_NAME = "Tournament"

    override fun deepCopyHelper(
        properties: TournamentPropertyFields
    ): TournamentProperties
    {
        return TournamentProperties(
            name                = properties.name,
            tournamentID        = properties.tournamentID,
            tournamentType      = properties.tournamentType,
            challengeFormat     = properties.challengeFormat,
            maxParticipants     = properties.maxParticipants,
            teamSize            = properties.teamSize,
            groupSize           = properties.groupSize,
            minLevel            = properties.minLevel,
            maxLevel            = properties.maxLevel,
            showPreview         = properties.showPreview,
            totalRounds         = properties.totalRounds,
            totalMatches        = properties.totalMatches,
            totalPlayers        = properties.totalPlayers,
            players             = properties.players.toMap()
        )
    }

    override fun deepMutableCopyHelper(
        properties: TournamentPropertyFields
    ): MutableTournamentProperties
    {
        return MutableTournamentProperties(
            name                = properties.name,
            tournamentID        = properties.tournamentID,
            tournamentType      = properties.tournamentType,
            challengeFormat     = properties.challengeFormat,
            maxParticipants     = properties.maxParticipants,
            teamSize            = properties.teamSize,
            groupSize           = properties.groupSize,
            minLevel            = properties.minLevel,
            maxLevel            = properties.maxLevel,
            showPreview         = properties.showPreview,
            totalRounds         = properties.totalRounds,
            totalMatches        = properties.totalMatches,
            totalPlayers        = properties.totalPlayers,
            players             = properties.players.toMutableMap()
        )
    }

    @Suppress("DuplicatedCode")
    override fun setFromPropertiesHelper(
        mutable: MutableTournamentProperties,
        from: TournamentPropertyFields
    ): MutableTournamentProperties
    {
        mutable.name              = from.name
        mutable.tournamentID      = from.tournamentID
        mutable.tournamentType    = from.tournamentType
        mutable.challengeFormat   = from.challengeFormat
        mutable.groupSize         = from.groupSize
        mutable.maxParticipants   = from.maxParticipants
        mutable.minLevel          = from.minLevel
        mutable.maxLevel          = from.maxLevel
        mutable.showPreview       = from.showPreview
        mutable.totalRounds       = from.totalRounds
        mutable.totalMatches      = from.totalMatches
        mutable.totalPlayers      = from.totalPlayers
        mutable.players           = from.players.toMutableMap()
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: MutableTournamentProperties,
        nbt: CompoundTag,
    ): MutableTournamentProperties
    {
        mutable.name             = nbt.getString(    TournamentDataKeys.TOURNAMENT_NAME)
        mutable.tournamentID     = nbt.getUUID(      TournamentDataKeys.TOURNAMENT_ID)
        mutable.tournamentType   = enumValueOf<TournamentType>( nbt.getString(TournamentDataKeys.TOURNAMENT_TYPE))
        mutable.challengeFormat  = enumValueOf<ChallengeFormat>( nbt.getString(TournamentDataKeys.CHALLENGE_FORMAT))
        mutable.groupSize        = nbt.getInt(       TournamentDataKeys.GROUP_SIZE)
        mutable.maxParticipants  = nbt.getInt(       TournamentDataKeys.MAX_PARTICIPANTS)
        mutable.minLevel         = nbt.getInt(       TournamentDataKeys.MIN_LEVEL)
        mutable.maxLevel         = nbt.getInt(       TournamentDataKeys.MAX_LEVEL)
        mutable.showPreview      = nbt.getBoolean(   TournamentDataKeys.SHOW_PREVIEW)
        mutable.totalRounds      = nbt.getInt(       TournamentDataKeys.TOTAL_ROUNDS)
        mutable.totalMatches     = nbt.getInt(       TournamentDataKeys.TOTAL_MATCHES)
        mutable.totalPlayers     = nbt.getInt(       TournamentDataKeys.TOTAL_PLAYERS)
        mutable.players          = loadPlayersData( nbt.getCompound( TournamentDataKeys.PLAYER_ID_TO_NAME))
        return mutable
    }

    override fun saveToNBTHelper(
        properties: TournamentPropertyFields,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putString(      TournamentDataKeys.TOURNAMENT_NAME  , properties.name)
        nbt.putUUID(        TournamentDataKeys.TOURNAMENT_ID    , properties.tournamentID)
        nbt.putString(      TournamentDataKeys.TOURNAMENT_TYPE  , properties.tournamentType.name)
        nbt.putString(      TournamentDataKeys.CHALLENGE_FORMAT , properties.challengeFormat.name)
        nbt.putInt(         TournamentDataKeys.MAX_PARTICIPANTS , properties.maxParticipants)
        nbt.putInt(         TournamentDataKeys.TEAM_SIZE        , properties.teamSize)
        nbt.putInt(         TournamentDataKeys.GROUP_SIZE       , properties.groupSize)
        nbt.putInt(         TournamentDataKeys.MIN_LEVEL        , properties.minLevel)
        nbt.putInt(         TournamentDataKeys.MAX_LEVEL        , properties.maxLevel)
        nbt.putBoolean(     TournamentDataKeys.SHOW_PREVIEW     , properties.showPreview)
        nbt.putInt(         TournamentDataKeys.TOTAL_ROUNDS     , properties.totalRounds)
        nbt.putInt(         TournamentDataKeys.TOTAL_MATCHES    , properties.totalMatches)
        nbt.putInt(         TournamentDataKeys.TOTAL_PLAYERS    , properties.totalPlayers)
        nbt.putInt(         TournamentDataKeys.TOTAL_PLAYERS    , properties.totalPlayers)
        nbt.put( TournamentDataKeys.PLAYER_ID_TO_NAME, savePlayersData( properties.players, CompoundTag()))
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ): TournamentProperties
    {
        return TournamentProperties(
            name                = nbt.getString(    TournamentDataKeys.TOURNAMENT_NAME),
            tournamentID        = nbt.getUUID(      TournamentDataKeys.TOURNAMENT_ID),
            tournamentType      = enumValueOf<TournamentType>( nbt.getString(TournamentDataKeys.TOURNAMENT_TYPE)),
            challengeFormat     = enumValueOf<ChallengeFormat>( nbt.getString(TournamentDataKeys.CHALLENGE_FORMAT)),
            groupSize           = nbt.getInt(       TournamentDataKeys.GROUP_SIZE),
            maxParticipants     = nbt.getInt(       TournamentDataKeys.MAX_PARTICIPANTS),
            minLevel            = nbt.getInt(       TournamentDataKeys.MIN_LEVEL),
            maxLevel            = nbt.getInt(       TournamentDataKeys.MAX_LEVEL),
            showPreview         = nbt.getBoolean(   TournamentDataKeys.SHOW_PREVIEW),
            totalRounds         = nbt.getInt(       TournamentDataKeys.TOTAL_ROUNDS),
            totalMatches        = nbt.getInt(       TournamentDataKeys.TOTAL_MATCHES),
            totalPlayers        = nbt.getInt(       TournamentDataKeys.TOTAL_PLAYERS),
            players             = loadPlayersData( nbt.getCompound( TournamentDataKeys.PLAYER_ID_TO_NAME))
        )
    }

    override fun loadMutableFromNBT(
        nbt: CompoundTag
    ): MutableTournamentProperties
    {
        return MutableTournamentProperties(
            name                = nbt.getString(    TournamentDataKeys.TOURNAMENT_NAME),
            tournamentID        = nbt.getUUID(      TournamentDataKeys.TOURNAMENT_ID),
            tournamentType      = enumValueOf<TournamentType>( nbt.getString(TournamentDataKeys.TOURNAMENT_TYPE)),
            challengeFormat     = enumValueOf<ChallengeFormat>( nbt.getString(TournamentDataKeys.CHALLENGE_FORMAT)),
            groupSize           = nbt.getInt(       TournamentDataKeys.GROUP_SIZE),
            maxParticipants     = nbt.getInt(       TournamentDataKeys.MAX_PARTICIPANTS),
            minLevel            = nbt.getInt(       TournamentDataKeys.MIN_LEVEL),
            maxLevel            = nbt.getInt(       TournamentDataKeys.MAX_LEVEL),
            showPreview         = nbt.getBoolean(   TournamentDataKeys.SHOW_PREVIEW),
            totalRounds         = nbt.getInt(       TournamentDataKeys.TOTAL_ROUNDS),
            totalMatches        = nbt.getInt(       TournamentDataKeys.TOTAL_MATCHES),
            totalPlayers        = nbt.getInt(       TournamentDataKeys.TOTAL_PLAYERS),
            players             = loadPlayersData( nbt.getCompound( TournamentDataKeys.PLAYER_ID_TO_NAME))
        )
    }



    // below is private functions to help with the methods above

    private fun savePlayersData(
        players: Map<UUID,String>,
        nbt: CompoundTag
    ) : CompoundTag
    {
        nbt.putInt(StoreDataKeys.SIZE,players.size)
        var index = 0
        val key   = TournamentDataKeys.PLAYER_ID
        val value = TournamentDataKeys.PLAYER_NAME
        players.forEach {
            nbt.putUUID("$key$index", it.key)
            nbt.putString("$value${index++}", it.value)
        }
        return nbt
    }

    private fun loadPlayersData(
        nbt: CompoundTag
    ) : MutableMap<UUID, String>
    {
        val map   = mutableMapOf<UUID,String>()
        val size  = nbt.getInt(StoreDataKeys.SIZE)
        val key   = TournamentDataKeys.PLAYER_ID
        val value = TournamentDataKeys.PLAYER_NAME
        for (i in 0 until size) {
            map[nbt.getUUID("$key$i")] = nbt.getString("$value$i")
        }
        return map
    }

}