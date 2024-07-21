package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.util.ChatUtil
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import com.someguy.storage.store.StoreUtil.getNullableUUID
import com.someguy.storage.store.StoreUtil.putIfNotNull
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object PlayerPropertiesHelper : PropertiesHelper <PlayerProperties>
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
        properties: PlayerProperties
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

    @Suppress("DuplicatedCode")
    override fun setFromPropertiesHelper(
        mutable: PlayerProperties,
        from: PlayerProperties,
    ): PlayerProperties
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
        mutable: PlayerProperties,
        nbt: CompoundTag
    ): PlayerProperties
    {
        mutable.name                = nbt.getString(        DataKeys.PLAYER_NAME )
        mutable.actorType           = enumValueOf <ActorType>( nbt.getString( DataKeys.ACTOR_TYPE) )
        mutable.playerID            = nbt.getUUID(          DataKeys.PLAYER_ID )
        mutable.tournamentID        = nbt.getUUID(          DataKeys.TOURNAMENT_ID )
        mutable.seed                = nbt.getInt(           DataKeys.SEED )
        mutable.originalSeed        = nbt.getInt(           DataKeys.ORIGINAL_SEED )
        mutable.finalPlacement      = nbt.getInt(           DataKeys.FINAL_PLACEMENT )
        mutable.pokemonTeamID       = nbt.getNullableUUID(  DataKeys.POKEMON_TEAM_ID )
        mutable.currentMatchID      = nbt.getNullableUUID(  DataKeys.CURRENT_MATCH_ID )
        mutable.pokemonFinal        = nbt.getBoolean(       DataKeys.POKEMON_FINAL )
        mutable.lockPokemonOnSet    = nbt.getBoolean(       DataKeys.LOCK_POKEMON_ON_SET )
        return mutable
    }

    override fun saveToNBTHelper(
        properties: PlayerProperties,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putString(      DataKeys.PLAYER_NAME          , properties.name )
        nbt.putString(      DataKeys.ACTOR_TYPE           , properties.actorType.name )
        nbt.putUUID(        DataKeys.PLAYER_ID            , properties.playerID )
        nbt.putUUID(        DataKeys.TOURNAMENT_ID        , properties.tournamentID )
        nbt.putInt(         DataKeys.SEED                 , properties.seed )
        nbt.putInt(         DataKeys.ORIGINAL_SEED        , properties.originalSeed )
        nbt.putInt(         DataKeys.FINAL_PLACEMENT      , properties.finalPlacement )
        nbt.putIfNotNull(   DataKeys.POKEMON_TEAM_ID      , properties.pokemonTeamID )
        nbt.putIfNotNull(   DataKeys.CURRENT_MATCH_ID     , properties.currentMatchID )
        nbt.putBoolean(     DataKeys.POKEMON_FINAL        , properties.pokemonFinal )
        nbt.putBoolean(     DataKeys.LOCK_POKEMON_ON_SET  , properties.lockPokemonOnSet )
        return nbt
    }

    override fun loadFromNBTHelper(
        nbt: CompoundTag
    ): PlayerProperties
    {
        return PlayerProperties(
            name                = nbt.getString(        DataKeys.PLAYER_NAME),
            actorType           = enumValueOf<ActorType>( nbt.getString( DataKeys.ACTOR_TYPE)),
            playerID            = nbt.getUUID(          DataKeys.PLAYER_ID),
            tournamentID        = nbt.getUUID(          DataKeys.TOURNAMENT_ID),
            seed                = nbt.getInt(           DataKeys.SEED),
            originalSeed        = nbt.getInt(           DataKeys.ORIGINAL_SEED),
            finalPlacement      = nbt.getInt(           DataKeys.FINAL_PLACEMENT),
            pokemonTeamID       = nbt.getNullableUUID(  DataKeys.POKEMON_TEAM_ID),
            currentMatchID      = nbt.getNullableUUID(  DataKeys.CURRENT_MATCH_ID),
            pokemonFinal        = nbt.getBoolean(       DataKeys.POKEMON_FINAL),
            lockPokemonOnSet    = nbt.getBoolean(       DataKeys.LOCK_POKEMON_ON_SET),
        )
    }

    override fun logDebugHelper(properties: PlayerProperties)
    {
        Util.report( "Player \"${ properties.name }\" (${ properties.actorType }) [${ ChatUtil.shortUUID( properties.playerID ) }]" )
        Util.report( "- Seed: ${ properties.seed } [Original: ${ properties.originalSeed }]" )
        Util.report( "- Pokemon Team ID: [${ ChatUtil.shortUUID( properties.pokemonTeamID ) }] (Final: ${ properties.pokemonFinal })" )
        Util.report( "- Current Match: [${ ChatUtil.shortUUID( properties.currentMatchID ) }]" )
        if (properties.finalPlacement > 0) {
            Util.report( "- Final Placement: ${ properties.finalPlacement }" )
        }
    }

    override fun displayInChatHelper(
        properties  : PlayerProperties,
        player      : ServerPlayer )
    {
        displayInChatOptionalHelper(
            properties          = properties,
            player              = player,
            displaySeed         = true,
            displayPokemon      = true,
            displayCurrentMatch = true,
            displayPlacement    = true )
    }

    fun displayInChatOptionalHelper(
        properties          : PlayerProperties,
        player              : ServerPlayer,
        spacing             : String = "",
        displaySeed         : Boolean = false,
        displayPokemon      : Boolean = false,
        displayCurrentMatch : Boolean = false,
        displayPlacement    : Boolean = false )
    {
        val titleText = ChatUtil.formatTextBracketed(
            text            = "${properties.actorType}",
            spacingBefore   = spacing,
            spacingAfter    = " ",)
        titleText.append( ChatUtil.formatTextQuoted(
            text            = properties.name,
            color           = ChatUtil.aqua,
            spacingAfter    = " ") )
        titleText.append( ChatUtil.formatTextBracketed(
            text    = ChatUtil.shortUUID( properties.playerID ),
            color   = ChatUtil.aqua,
            bold    = true ) )
        player.displayClientMessage( titleText ,false )

        if ( displaySeed ) {
            val seedText = ChatUtil.formatText(  text = "$spacing  Seed " )
            seedText.append( ChatUtil.formatTextBracketed(
                text    = "${ properties.seed }",
                color   = ChatUtil.yellow,
                bold    = true ) )
            seedText.append( ChatUtil.formatText( text = " Original " ) )
            seedText.append( ChatUtil.formatTextBracketed(
                text    = "${ properties.originalSeed }",
                color   = ChatUtil.yellow,
                bold    = true ) )
            player.displayClientMessage( seedText ,false )
        }

//        if ( displayPokemon ) {
            // TODO uncomment out when saving pokemon or rental teams are implemented
//            val pokemonText = ChatUtil.formatText( text = "$spacing  Pokemon Team ID " )
//            pokemonText.append(
//                ChatUtil.formatTextBracketed(
//                    text = ChatUtil.shortUUID( properties.pokemonTeamID ),
//                    color = ChatUtil.yellow,
//                    bold = true ) )
//            pokemonText.append(ChatUtil.formatText( text = " Team Final " ) )
//            pokemonText.append(
//                ChatUtil.formatTextBracketed(
//                    text = "${ properties.pokemonFinal }",
//                    color = ChatUtil.yellow,
//                    bold = true ) )
//            player.displayClientMessage( pokemonText ,false )
//        }
        if ( displayCurrentMatch ) {
            val matchText = ChatUtil.formatText(text = "$spacing  Current Match ")
            matchText.append(
                ChatUtil.formatTextBracketed(
                    text = ChatUtil.shortUUID( properties.currentMatchID ),
                    color = ChatUtil.yellow,
                    bold = true ) )
            player.displayClientMessage(matchText, false)
        }
        if (displayPlacement && properties.finalPlacement > 0) {
            val placementText = ChatUtil.formatText( text = "$spacing  Final Placement " )
            placementText.append( ChatUtil.formatTextBracketed(
                text    = "${ properties.finalPlacement }",
                color   = ChatUtil.yellow,
                bold    = true ) )
            player.displayClientMessage( placementText ,false )
        }
    }

    // below are functions needed by some classes for collections of Player Properties

    fun deepCopyPlayers(
        players: MutableSet <PlayerProperties>
    ): MutableSet <PlayerProperties>
    {
        val copy = mutableSetOf <PlayerProperties>()
        players.forEach { player -> copy.add( player.deepCopy() ) }
        return copy
    }

    fun savePlayersToNBT(
        players : Iterator <PlayerProperties>,
        nbt     : CompoundTag,
    ): CompoundTag
    {
        var size = 0
        players.forEach { properties ->
            nbt.put( DataKeys.PLAYER_PROPERTIES + size++, properties.saveToNBT( CompoundTag() ) )
        }
        nbt.putInt( StoreDataKeys.SIZE, size )
        return nbt
    }

    fun loadPlayersFromNBT(
        nbt: CompoundTag,
    ): MutableSet <PlayerProperties>
    {
        val players = mutableSetOf <PlayerProperties>()
        if ( nbt.contains( StoreDataKeys.SIZE ) && nbt.getInt( StoreDataKeys.SIZE ) != 0 ) {
            val size = nbt.getInt( StoreDataKeys.SIZE )
            for ( i in 0 until size ) {
                players.add( loadFromNBTHelper( nbt.getCompound( DataKeys.PLAYER_PROPERTIES + i ) ) )
            }
        }
        return players
    }

}