package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.storage.*
import com.sg8.properties.PropertiesHelper
import com.sg8.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object PlayerPropertiesHelper : PropertiesHelper<PlayerProperties> {

    override fun saveToNbt(properties: PlayerProperties, nbt: CompoundTag): CompoundTag {
        nbt.putString(PLAYER_NAME_KEY, properties.name)
        nbt.putString(ACTOR_TYPE_KEY, properties.actorType.name)
        nbt.putUUID(PLAYER_ID_KEY, properties.uuid)
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

    override fun loadFromNbt(nbt: CompoundTag): PlayerProperties {
        return PlayerProperties(
            name = nbt.getString(PLAYER_NAME_KEY),
            actorType = nbt.getConstantStrict<ActorType>(ACTOR_TYPE_KEY),
            uuid = nbt.getUUID(PLAYER_ID_KEY),
            tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY),
            seed = nbt.getInt(SEED_KEY),
            originalSeed = nbt.getInt(ORIGINAL_SEED_KEY),
            finalPlacement = nbt.getInt(FINAL_PLACEMENT_KEY),
            pokemonTeamID = nbt.getUuidOrNull(POKEMON_TEAM_ID_KEY),
            currentMatchID = nbt.getUuidOrNull(CURRENT_MATCH_ID_KEY),
            pokemonFinal = nbt.getBoolean(POKEMON_FINAL_KEY),
            lockPokemonOnSet = nbt.getBoolean(LOCK_POKEMON_ON_SET_KEY),
        )
    }

    override fun setFromNbt(mutable: PlayerProperties, nbt: CompoundTag): PlayerProperties {
        mutable.name = nbt.getString(PLAYER_NAME_KEY)
        mutable.actorType = nbt.getConstantStrict<ActorType>(ACTOR_TYPE_KEY)
        mutable.uuid = nbt.getUUID(PLAYER_ID_KEY)
        mutable.tournamentID = nbt.getUUID(TOURNAMENT_ID_KEY)
        mutable.seed = nbt.getInt(SEED_KEY)
        mutable.originalSeed = nbt.getInt(ORIGINAL_SEED_KEY)
        mutable.finalPlacement = nbt.getInt(FINAL_PLACEMENT_KEY)
        mutable.pokemonTeamID = nbt.getUuidOrNull(POKEMON_TEAM_ID_KEY)
        mutable.currentMatchID = nbt.getUuidOrNull(CURRENT_MATCH_ID_KEY)
        mutable.pokemonFinal = nbt.getBoolean(POKEMON_FINAL_KEY)
        mutable.lockPokemonOnSet = nbt.getBoolean(LOCK_POKEMON_ON_SET_KEY)
        return mutable
    }

    override fun deepCopy(properties: PlayerProperties) = copy(properties)

    override fun copy(properties: PlayerProperties): PlayerProperties {
        return PlayerProperties(
            name = properties.name,
            actorType = properties.actorType,
            uuid = properties.uuid,
            tournamentID = properties.tournamentID,
            seed = properties.seed,
            originalSeed = properties.originalSeed,
            finalPlacement = properties.finalPlacement,
            pokemonTeamID = properties.pokemonTeamID,
            currentMatchID = properties.currentMatchID,
            pokemonFinal = properties.pokemonFinal,
            lockPokemonOnSet = properties.lockPokemonOnSet,
        )
    }

    override fun printDebug(properties: PlayerProperties) {
        Util.report(("Player \"${properties.name}\" (${properties.actorType}) " +
                "[${properties.uuid.short() }]"))
        Util.report(("- Seed: ${properties.seed} [Original: ${properties.originalSeed}]"))
        Util.report(("- Pokemon Team ID: [${properties.pokemonTeamID.short()}] " +
                "(Final: ${properties.pokemonFinal})"))
        Util.report(("- Current Match: [${properties.currentMatchID.short()}]"))
        if (properties.finalPlacement > 0) {
            Util.report(("- Final Placement: ${properties.finalPlacement}"))
        }
    }

    override fun displayInChat(properties: PlayerProperties, player: ServerPlayer) {
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
            textColor = AQUA_FORMAT,
            padding = 0 to 1,
        )
        titleComponent.appendWithBracketed(
            text = properties.uuid.short(),
            textColor = AQUA_FORMAT,
            bold = true,
        )

        player.displayClientMessage(titleComponent, (false))

        if (displaySeed) {
            val seedComponent = getComponent(text = "  Seed ", padding = padStart to 0)
            seedComponent.appendWithBracketed(
                text = "${properties.seed}",
                textColor = YELLOW_FORMAT,
                bold = true,
            )
            seedComponent.appendWith(text = " Original ")
            seedComponent.appendWithBracketed(
                text = "${properties.originalSeed}",
                textColor = YELLOW_FORMAT,
                bold = true,
            )
            player.displayClientMessage(seedComponent, (false))
        }

////      TODO uncomment out when saving pokemon or rental teams are implemented
//        if (displayPokemon) {
//            val pokemonComponent = ChatUtil.formatText(text = "$spacing  Pokemon Team ID ")
//            pokemonComponent.appendWithBracketed(
//                text = ChatUtil.shortUUID(uuid = properties.pokemonTeamID),
//                textColor = YELLOW_FORMAT,
//                bold = true,
//            )
//            pokemonComponent.appendWith(text = " Team Final ")
//            pokemonComponent.appendWithBracketed(
//                text = "${properties.pokemonFinal}",
//                textColor = YELLOW_FORMAT,
//                bold = true,
//            )
//            player.displayClientMessage(pokemonComponent, (false))
//        }

        if (displayCurrentMatch) {
            val matchComponent = getComponent(text = "  Current Match ".padStart(padStart))
            matchComponent.appendWithBracketed(
                text = (properties.currentMatchID.short()),
                textColor = YELLOW_FORMAT,
                bold = true,
            )
            player.displayClientMessage(matchComponent, (false))
        }
        if (displayPlacement && properties.finalPlacement > 0) {
            val placementText = getComponent(text = "  Final Placement ".padStart(padStart))
            placementText.appendWithBracketed(
                text = "${properties.finalPlacement}",
                textColor = YELLOW_FORMAT,
                bold = true,
            )
            player.displayClientMessage(placementText, (false))
        }
    }

}
