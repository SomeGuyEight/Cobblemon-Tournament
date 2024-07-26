package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.util.*
import com.someguy.storage.PropertiesHelper
import com.someguy.storage.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object PlayerPropertiesHelper : PropertiesHelper<PlayerProperties> {

    override fun setFromNbtHelper(mutable: PlayerProperties, nbt: CompoundTag): PlayerProperties {
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

    fun deepCopyPlayers(players: MutableSet<PlayerProperties>): MutableSet<PlayerProperties> {
        val copy = mutableSetOf<PlayerProperties>()
        players.forEach { copy.add(it.deepCopy()) }
        return copy
    }

    override fun saveToNbtHelper(properties: PlayerProperties, nbt: CompoundTag): CompoundTag {
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

    fun savePlayersToNbt(players: Iterator<PlayerProperties>, nbt: CompoundTag): CompoundTag {
        var size = 0
        players.forEach { properties ->
            nbt.put((PLAYER_PROPERTIES_KEY + size++), properties.saveToNbt(nbt = CompoundTag()))
        }
        nbt.putInt(SIZE_KEY, size)
        return nbt
    }

    override fun loadFromNbtHelper(nbt: CompoundTag): PlayerProperties {
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

    fun loadPlayersFromNbt(nbt: CompoundTag): MutableSet<PlayerProperties> {
        val players = mutableSetOf<PlayerProperties>()
        if (nbt.contains(SIZE_KEY) && (nbt.getInt(SIZE_KEY) != 0) ) {
            val size = nbt.getInt(SIZE_KEY)
            for (i in 0 until size) {
                players.add(
                    loadFromNbtHelper(nbt = nbt.getCompound((PLAYER_PROPERTIES_KEY + i)))
                )
            }
        }
        return players
    }

    override fun logDebugHelper(properties: PlayerProperties) {
        Util.report(("Player \"${properties.name}\" (${properties.actorType}) " +
                "[${properties.playerID.shortUUID() }]"))
        Util.report(("- Seed: ${properties.seed} [Original: ${properties.originalSeed}]"))
        Util.report(("- Pokemon Team ID: [${properties.pokemonTeamID.shortUUID()}] " +
                "(Final: ${properties.pokemonFinal})"))
        Util.report(("- Current Match: [${properties.currentMatchID.shortUUID()}]"))
        if (properties.finalPlacement > 0) {
            Util.report(("- Final Placement: ${properties.finalPlacement}"))
        }
    }

    override fun displayInChatHelper(properties: PlayerProperties, player: ServerPlayer) {
        displayInChatHelper(
            properties = properties,
            player = player,
            displaySeed = true,
            displayPokemon = true,
            displayCurrentMatch = true,
            displayPlacement = true,
        )
    }

    fun displayInChatHelper(
        properties: PlayerProperties,
        player: ServerPlayer,
        padStart: Int = 0,
        displaySeed: Boolean = false,
        displayPokemon: Boolean = false,
        displayCurrentMatch: Boolean = false,
        displayPlacement: Boolean = false,
    ) {
        val titleComponent = getBracketedComponent(
            text = "${properties.actorType}",
            padding = padStart to 1,
        )
        titleComponent.appendWithQuoted(
            text = properties.name,
            textColor = ChatUtil.AQUA_FORMAT,
            padding = 0 to 1,
        )
        titleComponent.appendWithBracketed(
            text = properties.playerID.shortUUID(),
            textColor = ChatUtil.AQUA_FORMAT,
            bold = true,
        )

        player.displayClientMessage(titleComponent, (false))

        if (displaySeed) {
            val seedComponent = getComponent(text = "  Seed ", padding = padStart to 0)
            seedComponent.appendWithBracketed(
                text = "${properties.seed}",
                textColor = ChatUtil.YELLOW_FORMAT,
                bold = true,
            )
            seedComponent.appendWith(text = " Original ")
            seedComponent.appendWithBracketed(
                text = "${properties.originalSeed}",
                textColor = ChatUtil.YELLOW_FORMAT,
                bold = true,
            )
            player.displayClientMessage(seedComponent, (false))
        }

////      TODO uncomment out when saving pokemon or rental teams are implemented
//        if (displayPokemon) {
//            val pokemonComponent = ChatUtil.formatText(text = "$spacing  Pokemon Team ID ")
//            pokemonComponent.appendWithBracketed(
//                text = ChatUtil.shortUUID(uuid = properties.pokemonTeamID),
//                textColor = ChatUtil.yellow,
//                bold = true,
//            )
//            pokemonComponent.appendWith(text = " Team Final ")
//            pokemonComponent.appendWithBracketed(
//                text = "${properties.pokemonFinal}",
//                textColor = ChatUtil.yellow,
//                bold = true,
//            )
//            player.displayClientMessage(pokemonComponent, (false))
//        }

        if (displayCurrentMatch) {
            val matchComponent = getComponent(text = "  Current Match ".padStart(padStart))
            matchComponent.appendWithBracketed(
                text = (properties.currentMatchID.shortUUID()),
                textColor = ChatUtil.YELLOW_FORMAT,
                bold = true,
            )
            player.displayClientMessage(matchComponent, (false))
        }
        if (displayPlacement && properties.finalPlacement > 0) {
            val placementText = getComponent(text = "  Final Placement ".padStart(padStart))
            placementText.appendWithBracketed(
                text = "${properties.finalPlacement}",
                textColor = ChatUtil.YELLOW_FORMAT,
                bold = true,
            )
            player.displayClientMessage(placementText, (false))
        }
    }

}
