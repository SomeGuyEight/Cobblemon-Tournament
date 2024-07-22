package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ACTOR_TYPE_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.CURRENT_MATCH_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.FINAL_PLACEMENT_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.LOCK_POKEMON_ON_SET_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.ORIGINAL_SEED_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.PLAYER_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.PLAYER_NAME_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.PLAYER_PROPERTIES_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.POKEMON_FINAL_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.POKEMON_TEAM_ID_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.SEED_KEY
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_ID_KEY
import com.cobblemontournament.common.util.ChatUtil
import com.someguy.storage.properties.PropertiesHelper
import com.someguy.storage.util.StoreDataKeys
import com.someguy.storage.store.StoreUtil.getNullableUUID
import com.someguy.storage.store.StoreUtil.putIfNotNull
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object PlayerPropertiesHelper : PropertiesHelper<PlayerProperties> {

    const val DEFAULT_PLAYER_NAME = "Player Name"
    val DEFAULT_ACTOR_TYPE = ActorType.PLAYER
    const val DEFAULT_SEED = -1
    const val DEFAULT_FINAL_PLACEMENT = -1
    val DEFAULT_POKEMON_TEAM_ID = null
    val DEFAULT_CURRENT_MATCH_ID = null
    const val DEFAULT_POKEMON_FINAL = false
    const val DEFAULT_LOCK_POKEMON_ON_SET = true

    override fun deepCopyHelper(properties: PlayerProperties): PlayerProperties {
        val props = PlayerProperties (
            name = properties.name,
            actorType = properties.actorType,
            playerID = properties.playerID,
            tournamentID = properties.tournamentID,
            seed = properties.seed,
            originalSeed = properties.originalSeed,
            finalPlacement = properties.finalPlacement,
            pokemonTeamID = properties.pokemonTeamID,
            currentMatchID = properties.currentMatchID,
            pokemonFinal = properties.pokemonFinal,
            lockPokemonOnSet = properties.lockPokemonOnSet,
        )
        return props
    }

    @Suppress("DuplicatedCode")
    override fun setFromPropertiesHelper(
        mutable: PlayerProperties,
        from: PlayerProperties,
    ): PlayerProperties {
        mutable.name = from.name
        mutable.actorType = from.actorType
        mutable.playerID = from.playerID
        mutable.tournamentID = from.tournamentID
        mutable.seed = from.seed
        mutable.originalSeed = from.originalSeed
        mutable.finalPlacement = from.finalPlacement
        mutable.pokemonTeamID = from.pokemonTeamID
        mutable.currentMatchID = from.currentMatchID
        mutable.pokemonFinal = from.pokemonFinal
        mutable.lockPokemonOnSet = from.lockPokemonOnSet
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: PlayerProperties,
        nbt: CompoundTag
    ): PlayerProperties {
        mutable.name = nbt.getString(PLAYER_NAME_KEY)
        mutable.actorType = enumValueOf<ActorType>(nbt.getString(ACTOR_TYPE_KEY))
        mutable.playerID = nbt.getUUID(PLAYER_ID_KEY)
        mutable.tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.seed = nbt.getInt(SEED_KEY)
        mutable.originalSeed = nbt.getInt(ORIGINAL_SEED_KEY)
        mutable.finalPlacement = nbt.getInt(FINAL_PLACEMENT_KEY)
        mutable.pokemonTeamID = nbt.getNullableUUID(POKEMON_TEAM_ID_KEY)
        mutable.currentMatchID = nbt.getNullableUUID(CURRENT_MATCH_ID_KEY)
        mutable.pokemonFinal = nbt.getBoolean(POKEMON_FINAL_KEY)
        mutable.lockPokemonOnSet = nbt.getBoolean(LOCK_POKEMON_ON_SET_KEY)
        return mutable
    }

    override fun saveToNBTHelper(
        properties: PlayerProperties,
        nbt: CompoundTag
    ): CompoundTag {
        nbt.putString(PLAYER_NAME_KEY, properties.name)
        nbt.putString(ACTOR_TYPE_KEY, properties.actorType.name)
        nbt.putUUID(PLAYER_ID_KEY, properties.playerID)
        nbt.putUUID(TOURNAMENT_ID_KEY, properties.tournamentID)
        nbt.putInt(SEED_KEY, properties.seed)
        nbt.putInt(ORIGINAL_SEED_KEY, properties.originalSeed)
        nbt.putInt(FINAL_PLACEMENT_KEY, properties.finalPlacement)
        nbt.putIfNotNull(POKEMON_TEAM_ID_KEY, properties.pokemonTeamID)
        nbt.putIfNotNull(CURRENT_MATCH_ID_KEY, properties.currentMatchID)
        nbt.putBoolean(POKEMON_FINAL_KEY, properties.pokemonFinal)
        nbt.putBoolean(LOCK_POKEMON_ON_SET_KEY, properties.lockPokemonOnSet)
        return nbt
    }

    override fun loadFromNBTHelper(nbt: CompoundTag): PlayerProperties {
        return PlayerProperties(
            name = nbt.getString(PLAYER_NAME_KEY),
            actorType = enumValueOf<ActorType>(nbt.getString(ACTOR_TYPE_KEY)),
            playerID = nbt.getUUID(PLAYER_ID_KEY),
            tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY),
            seed = nbt.getInt(SEED_KEY),
            originalSeed = nbt.getInt(ORIGINAL_SEED_KEY),
            finalPlacement = nbt.getInt(FINAL_PLACEMENT_KEY),
            pokemonTeamID = nbt.getNullableUUID(POKEMON_TEAM_ID_KEY),
            currentMatchID = nbt.getNullableUUID(CURRENT_MATCH_ID_KEY),
            pokemonFinal = nbt.getBoolean(POKEMON_FINAL_KEY),
            lockPokemonOnSet = nbt.getBoolean(LOCK_POKEMON_ON_SET_KEY),
        )
    }

    override fun logDebugHelper(properties: PlayerProperties) {
        Util.report(("Player \"${properties.name}\" (${properties.actorType}) [${ChatUtil.shortUUID(properties.playerID) }]"))
        Util.report(("- Seed: ${properties.seed} [Original: ${properties.originalSeed}]"))
        Util.report(("- Pokemon Team ID: [${ChatUtil.shortUUID(properties.pokemonTeamID)}] (Final: ${properties.pokemonFinal})"))
        Util.report(("- Current Match: [${ChatUtil.shortUUID( properties.currentMatchID)}]"))
        if (properties.finalPlacement > 0) {
            Util.report(("- Final Placement: ${properties.finalPlacement}"))
        }
    }

    override fun displayInChatHelper(properties: PlayerProperties, player: ServerPlayer) {
        displayInChatOptionalHelper(
            properties = properties,
            player = player,
            displaySeed = true,
            displayPokemon = true,
            displayCurrentMatch = true,
            displayPlacement = true,
        )
    }

    fun displayInChatOptionalHelper(
        properties: PlayerProperties,
        player: ServerPlayer,
        spacing: String = "",
        displaySeed: Boolean = false,
        displayPokemon: Boolean = false,
        displayCurrentMatch: Boolean = false,
        displayPlacement: Boolean = false,
    ) {
        val titleText = ChatUtil.formatTextBracketed(
            text = "${properties.actorType}",
            spacingBefore = spacing,
            spacingAfter = " ",
        )
        titleText.append(ChatUtil.formatTextQuoted(
            text = properties.name,
            color = ChatUtil.aqua,
            spacingAfter = " ",
        ))
        titleText.append(ChatUtil.formatTextBracketed(
            text = ChatUtil.shortUUID(uuid = properties.playerID),
            color = ChatUtil.aqua,
            bold = true,
        ))
        player.displayClientMessage(titleText, (false))

        if (displaySeed) {
            val seedText = ChatUtil.formatText(text = "$spacing  Seed ")
            seedText.append(ChatUtil.formatTextBracketed(
                text = "${properties.seed}",
                color = ChatUtil.yellow,
                bold = true,
            ))
            seedText.append(ChatUtil.formatText(text = " Original "))
            seedText.append(ChatUtil.formatTextBracketed(
                text = "${properties.originalSeed}",
                color = ChatUtil.yellow,
                bold = true,
            ))
            player.displayClientMessage(seedText, (false))
        }

////      TODO uncomment out when saving pokemon or rental teams are implemented
//        if (displayPokemon) {
//            val pokemonText = ChatUtil.formatText(text = "$spacing  Pokemon Team ID ")
//            pokemonText.append(ChatUtil.formatTextBracketed(
//                text = ChatUtil.shortUUID(uuid = properties.pokemonTeamID),
//                color = ChatUtil.yellow,
//                bold = true,
//            ))
//            pokemonText.append(ChatUtil.formatText(text = " Team Final "))
//            pokemonText.append(ChatUtil.formatTextBracketed(
//                text = "${properties.pokemonFinal}",
//                color = ChatUtil.yellow,
//                bold = true,
//            ))
//            player.displayClientMessage(pokemonText, (false))
//        }

        if (displayCurrentMatch) {
            val matchText = ChatUtil.formatText(text = "$spacing  Current Match ")
            matchText.append(ChatUtil.formatTextBracketed(
                text = ChatUtil.shortUUID(uuid = properties.currentMatchID),
                color = ChatUtil.yellow,
                bold = true,
            ))
            player.displayClientMessage(matchText, (false))
        }
        if (displayPlacement && properties.finalPlacement > 0) {
            val placementText = ChatUtil.formatText(text = "$spacing  Final Placement ")
            placementText.append(ChatUtil.formatTextBracketed(
                text = "${properties.finalPlacement}",
                color = ChatUtil.yellow,
                bold = true,
            ))
            player.displayClientMessage(placementText, (false))
        }
    }

    // below are functions needed by some classes for collections of Player Properties

    fun deepCopyPlayers(
        players: MutableSet<PlayerProperties>,
    ): MutableSet<PlayerProperties> {
        val copy = mutableSetOf<PlayerProperties>()
        players.forEach { copy.add(it.deepCopy()) }
        return copy
    }

    fun savePlayersToNbt(
        players: Iterator<PlayerProperties>,
        nbt: CompoundTag,
    ): CompoundTag {
        var size = 0
        players.forEach { properties ->
            nbt.put((PLAYER_PROPERTIES_KEY + (size++)), properties.saveToNBT(nbt = CompoundTag()))
        }
        nbt.putInt(StoreDataKeys.SIZE, size)
        return nbt
    }

    fun loadPlayersFromNbt(nbt: CompoundTag): MutableSet<PlayerProperties> {
        val players = mutableSetOf<PlayerProperties>()
        if (nbt.contains(StoreDataKeys.SIZE) && (nbt.getInt(StoreDataKeys.SIZE) != 0) ) {
            val size = nbt.getInt(StoreDataKeys.SIZE)
            for (i in 0 until size) {
                players.add(loadFromNBTHelper(nbt = nbt.getCompound((PLAYER_PROPERTIES_KEY + i))))
            }
        }
        return players
    }

}