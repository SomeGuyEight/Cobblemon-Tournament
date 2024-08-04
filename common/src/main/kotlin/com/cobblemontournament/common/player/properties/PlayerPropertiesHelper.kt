package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.storage.DataKeys
import com.sg8.properties.PropertiesHelper
import com.sg8.util.appendWith
import com.sg8.util.appendWithBracketed
import com.sg8.util.appendWithQuoted
import com.sg8.util.ComponentUtil
import com.sg8.util.getConstantStrict
import com.sg8.util.getUuidOrNull
import com.sg8.util.putIfNotNull
import com.sg8.util.short
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util


object PlayerPropertiesHelper : PropertiesHelper<PlayerProperties> {

    override fun saveToNbt(properties: PlayerProperties, nbt: CompoundTag): CompoundTag {
        nbt.putString(DataKeys.PLAYER_NAME, properties.name)
        nbt.putString(DataKeys.ACTOR_TYPE, properties.actorType.name)
        nbt.putUUID(DataKeys.PLAYER_ID, properties.uuid)
        nbt.putUUID(DataKeys.TOURNAMENT_ID, properties.tournamentID)
        nbt.putInt(DataKeys.SEED, properties.seed)
        nbt.putInt(DataKeys.ORIGINAL_SEED, properties.originalSeed)
        nbt.putInt(DataKeys.FINAL_PLACEMENT, properties.finalPlacement)
        nbt.putIfNotNull(DataKeys.POKEMON_TEAM_ID, properties.pokemonTeamID)
        nbt.putIfNotNull(DataKeys.CURRENT_MATCH_ID, properties.currentMatchID)
        nbt.putBoolean(DataKeys.POKEMON_FINAL, properties.pokemonFinal)
        nbt.putBoolean(DataKeys.LOCK_POKEMON_ON_SET, properties.lockPokemonOnSet)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): PlayerProperties {
        return PlayerProperties(
            name = nbt.getString(DataKeys.PLAYER_NAME),
            actorType = nbt.getConstantStrict<ActorType>(DataKeys.ACTOR_TYPE),
            uuid = nbt.getUUID(DataKeys.PLAYER_ID),
            tournamentID = nbt.getUUID(DataKeys.TOURNAMENT_ID),
            seed = nbt.getInt(DataKeys.SEED),
            originalSeed = nbt.getInt(DataKeys.ORIGINAL_SEED),
            finalPlacement = nbt.getInt(DataKeys.FINAL_PLACEMENT),
            pokemonTeamID = nbt.getUuidOrNull(DataKeys.POKEMON_TEAM_ID),
            currentMatchID = nbt.getUuidOrNull(DataKeys.CURRENT_MATCH_ID),
            pokemonFinal = nbt.getBoolean(DataKeys.POKEMON_FINAL),
            lockPokemonOnSet = nbt.getBoolean(DataKeys.LOCK_POKEMON_ON_SET),
        )
    }

    override fun setFromNbt(mutable: PlayerProperties, nbt: CompoundTag): PlayerProperties {
        mutable.name = nbt.getString(DataKeys.PLAYER_NAME)
        mutable.actorType = nbt.getConstantStrict<ActorType>(DataKeys.ACTOR_TYPE)
        mutable.uuid = nbt.getUUID(DataKeys.PLAYER_ID)
        mutable.tournamentID = nbt.getUUID(DataKeys.TOURNAMENT_ID)
        mutable.seed = nbt.getInt(DataKeys.SEED)
        mutable.originalSeed = nbt.getInt(DataKeys.ORIGINAL_SEED)
        mutable.finalPlacement = nbt.getInt(DataKeys.FINAL_PLACEMENT)
        mutable.pokemonTeamID = nbt.getUuidOrNull(DataKeys.POKEMON_TEAM_ID)
        mutable.currentMatchID = nbt.getUuidOrNull(DataKeys.CURRENT_MATCH_ID)
        mutable.pokemonFinal = nbt.getBoolean(DataKeys.POKEMON_FINAL)
        mutable.lockPokemonOnSet = nbt.getBoolean(DataKeys.LOCK_POKEMON_ON_SET)
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
        val titleComponent = ComponentUtil.getBracketedComponent(
            text = "${properties.actorType}",
            padding = padStart to 1,
        )
        titleComponent.appendWithQuoted(
            text = properties.name,
            textColor = ChatFormatting.AQUA,
            padding = 0 to 1,
        )
        titleComponent.appendWithBracketed(
            text = properties.uuid.short(),
            textColor = ChatFormatting.AQUA,
            bold = true,
        )

        player.displayClientMessage(titleComponent, (false))

        if (displaySeed) {
            val seedComponent = ComponentUtil.getComponent(
                text = "  Seed ",
                padding = padStart to 0,
            )
            seedComponent.appendWithBracketed(
                text = "${properties.seed}",
                textColor = ChatFormatting.YELLOW,
                bold = true,
            )
            seedComponent.appendWith(text = " Original ")
            seedComponent.appendWithBracketed(
                text = "${properties.originalSeed}",
                textColor = ChatFormatting.YELLOW,
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
            val matchComponent = ComponentUtil.getComponent(
                text = "  Current Match ".padStart(padStart),
            )
            matchComponent.appendWithBracketed(
                text = (properties.currentMatchID.short()),
                textColor = ChatFormatting.YELLOW,
                bold = true,
            )
            player.displayClientMessage(matchComponent, (false))
        }
        if (displayPlacement && properties.finalPlacement > 0) {
            val placementText = ComponentUtil.getComponent(
                text = "  Final Placement ".padStart(padStart),
            )
            placementText.appendWithBracketed(
                text = "${properties.finalPlacement}",
                textColor = ChatFormatting.YELLOW,
                bold = true,
            )
            player.displayClientMessage(placementText, (false))
        }
    }

}
