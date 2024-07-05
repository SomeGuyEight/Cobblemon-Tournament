package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.util.TournamentDataKeys
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import com.someguy.storage.util.StoreUtil.getNullableUUID
import com.someguy.storage.util.StoreUtil.putIfNotNull
import net.minecraft.nbt.CompoundTag

object PlayerPropertiesHelper : PropertiesHelper<PlayerPropertyFields,PlayerProperties,MutablePlayerProperties>
{
    const val   DEFAULT_PLAYER_NAME             = "Player Name"
    val         DEFAULT_ACTOR_TYPE              = ActorType.PLAYER
    const val   DEFAULT_SEED                    = -1
    const val   DEFAULT_FINAL_PLACEMENT         = -1
    val         DEFAULT_POKEMON_TEAM_ID         = null
    val         DEFAULT_CURRENT_MATCH_ID        = null
    const val   DEFAULT_POKEMON_FINAL           = false
    const val   DEFAULT_LOCK_POKEMON_ON_SET     = true

    override fun deepCopyHelper(
        properties: PlayerPropertyFields
    ): PlayerProperties
    {
        val props = PlayerProperties (
            name                = properties.name,
            actorType           = properties.actorType,
            playerID            = properties.playerID,
            tournamentID        = properties.tournamentID,
            seed                = properties.seed,
            originalSeed        = properties.originalSeed,
            finalPlacement      = properties.finalPlacement,
            pokemonTeamID       = properties.pokemonTeamID,
            currentMatchID      = properties.currentMatchID,
            pokemonFinal        = properties.pokemonFinal,
            lockPokemonOnSet    = properties.lockPokemonOnSet)
        return props
    }

    override fun deepMutableCopyHelper(
        properties: PlayerPropertyFields
    ): MutablePlayerProperties
    {
        val props = MutablePlayerProperties(
            name                = properties.name,
            actorType           = properties.actorType,
            playerID            = properties.playerID,
            tournamentID        = properties.tournamentID,
            seed                = properties.seed,
            originalSeed        = properties.originalSeed,
            finalPlacement      = properties.finalPlacement,
            pokemonTeamID       = properties.pokemonTeamID,
            currentMatchID      = properties.currentMatchID,
            pokemonFinal        = properties.pokemonFinal,
            lockPokemonOnSet    = properties.lockPokemonOnSet)
        return props
    }

    @Suppress("DuplicatedCode")
    override fun setFromPropertiesHelper(
        mutable: MutablePlayerProperties,
        from: PlayerPropertyFields,
    ): MutablePlayerProperties
    {
        mutable.name                    = from.name
        mutable.actorType               = from.actorType
        mutable.playerID                = from.playerID
        mutable.tournamentID            = from.tournamentID
        mutable.seed                    = from.seed
        mutable.originalSeed            = from.originalSeed
        mutable.finalPlacement          = from.finalPlacement
        mutable.pokemonTeamID           = from.pokemonTeamID
        mutable.currentMatchID          = from.currentMatchID
        mutable.pokemonFinal            = from.pokemonFinal
        mutable.lockPokemonOnSet        = from.lockPokemonOnSet
        return mutable
    }
    override fun setFromNBTHelper(
        mutable: MutablePlayerProperties,
        nbt: CompoundTag
    ): MutablePlayerProperties
    {
        mutable.name                = nbt.getString(        TournamentDataKeys.PLAYER_NAME)
        mutable.actorType           = enumValueOf<ActorType> ( nbt.getString( TournamentDataKeys.ACTOR_TYPE))
        mutable.playerID            = nbt.getUUID(          TournamentDataKeys.PLAYER_ID)
        mutable.tournamentID        = nbt.getUUID(          TournamentDataKeys.TOURNAMENT_ID)
        mutable.seed                = nbt.getInt(           TournamentDataKeys.SEED)
        mutable.originalSeed        = nbt.getInt(           TournamentDataKeys.ORIGINAL_SEED)
        mutable.finalPlacement      = nbt.getInt(           TournamentDataKeys.FINAL_PLACEMENT)
        mutable.pokemonTeamID       = nbt.getNullableUUID(  TournamentDataKeys.POKEMON_TEAM_ID)
        mutable.currentMatchID      = nbt.getNullableUUID(  TournamentDataKeys.CURRENT_MATCH_ID)
        mutable.pokemonFinal        = nbt.getBoolean(       TournamentDataKeys.POKEMON_FINAL)
        mutable.lockPokemonOnSet    = nbt.getBoolean(       TournamentDataKeys.LOCK_POKEMON_ON_SET)
        return mutable
    }

    override fun saveToNBTHelper(
        properties: PlayerPropertyFields,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putString(      TournamentDataKeys.PLAYER_NAME          , properties.name)
        nbt.putString(      TournamentDataKeys.ACTOR_TYPE           , properties.actorType.name)
        nbt.putUUID(        TournamentDataKeys.PLAYER_ID            , properties.playerID)
        nbt.putUUID(        TournamentDataKeys.TOURNAMENT_ID        , properties.tournamentID)
        nbt.putInt(         TournamentDataKeys.SEED                 , properties.seed)
        nbt.putInt(         TournamentDataKeys.ORIGINAL_SEED        , properties.originalSeed)
        nbt.putInt(         TournamentDataKeys.FINAL_PLACEMENT      , properties.finalPlacement)
        nbt.putIfNotNull(   TournamentDataKeys.POKEMON_TEAM_ID      , properties.pokemonTeamID)
        nbt.putIfNotNull(   TournamentDataKeys.CURRENT_MATCH_ID     , properties.currentMatchID)
        nbt.putBoolean(     TournamentDataKeys.POKEMON_FINAL        , properties.pokemonFinal)
        nbt.putBoolean(     TournamentDataKeys.LOCK_POKEMON_ON_SET  , properties.lockPokemonOnSet)
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ): PlayerProperties
    {
        return PlayerProperties(
            name                = nbt.getString(        TournamentDataKeys.PLAYER_NAME),
            actorType           = enumValueOf<ActorType>( nbt.getString( TournamentDataKeys.ACTOR_TYPE)),
            playerID            = nbt.getUUID(          TournamentDataKeys.PLAYER_ID),
            tournamentID        = nbt.getUUID(          TournamentDataKeys.TOURNAMENT_ID),
            seed                = nbt.getInt(           TournamentDataKeys.SEED),
            originalSeed        = nbt.getInt(           TournamentDataKeys.ORIGINAL_SEED),
            finalPlacement      = nbt.getInt(           TournamentDataKeys.FINAL_PLACEMENT),
            pokemonTeamID       = nbt.getNullableUUID(  TournamentDataKeys.POKEMON_TEAM_ID),
            currentMatchID      = nbt.getNullableUUID(  TournamentDataKeys.CURRENT_MATCH_ID),
            pokemonFinal        = nbt.getBoolean(       TournamentDataKeys.POKEMON_FINAL),
            lockPokemonOnSet    = nbt.getBoolean(       TournamentDataKeys.LOCK_POKEMON_ON_SET),
        )
    }

    override fun loadMutableFromNBT(
        nbt: CompoundTag
    ): MutablePlayerProperties
    {
        return MutablePlayerProperties(
            name                = nbt.getString(        TournamentDataKeys.PLAYER_NAME),
            actorType           = enumValueOf<ActorType>( nbt.getString( TournamentDataKeys.ACTOR_TYPE)),
            playerID            = nbt.getUUID(          TournamentDataKeys.PLAYER_ID),
            tournamentID        = nbt.getUUID(          TournamentDataKeys.TOURNAMENT_ID),
            seed                = nbt.getInt(           TournamentDataKeys.SEED),
            originalSeed        = nbt.getInt(           TournamentDataKeys.ORIGINAL_SEED),
            finalPlacement      = nbt.getInt(           TournamentDataKeys.FINAL_PLACEMENT),
            pokemonTeamID       = nbt.getNullableUUID(  TournamentDataKeys.POKEMON_TEAM_ID),
            currentMatchID      = nbt.getNullableUUID(  TournamentDataKeys.CURRENT_MATCH_ID),
            pokemonFinal        = nbt.getBoolean(       TournamentDataKeys.POKEMON_FINAL),
            lockPokemonOnSet    = nbt.getBoolean(       TournamentDataKeys.LOCK_POKEMON_ON_SET),
        )
    }

    // below are functions needed by some classes for collections of Player Properties

    fun deepCopy(
        players: MutableSet<MutablePlayerProperties>
    ) : MutableSet<MutablePlayerProperties>
    {
        val copy = mutableSetOf<MutablePlayerProperties>()
        players.forEach { player -> copy.add( player.deepMutableCopy() ) }
        return copy
    }

    fun savePlayersToNBT(
        players: MutableSet<MutablePlayerProperties>,
        nbt: CompoundTag,
    ) : CompoundTag
    {
        var index = 0
        players.forEach { properties ->
            nbt.put(TournamentDataKeys.PLAYER_PROPERTIES + index++, properties.saveToNBT( CompoundTag()))
        }
        nbt.putInt(StoreDataKeys.SIZE,players.size)
        return nbt
    }

    fun loadPlayersFromNBT(
        nbt: CompoundTag,
    ) : MutableSet<MutablePlayerProperties>
    {
        val players = mutableSetOf<MutablePlayerProperties>()
        if (nbt.contains(StoreDataKeys.SIZE) && nbt.getInt(StoreDataKeys.SIZE) != 0) {
            val size = nbt.getInt(StoreDataKeys.SIZE)
            for (i in 0 until size) {
                players.add( loadMutableFromNBT(
                    nbt.getCompound(TournamentDataKeys.PLAYER_PROPERTIES + i)))
            }
        }
        return players
    }

}