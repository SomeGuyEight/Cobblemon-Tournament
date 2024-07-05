package com.cobblemontournament.common.player.properties

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.someguy.storage.properties.PropertyFields
import org.slf4j.helpers.Util
import java.util.UUID

interface PlayerPropertyFields : PropertyFields
{
    val name                : String
    val actorType           : ActorType
    val playerID            : UUID
    val tournamentID        : UUID
    val seed                : Int
    val originalSeed        : Int
    val finalPlacement      : Int
    val pokemonTeamID       : UUID?
    val currentMatchID      : UUID?
    val pokemonFinal        : Boolean
    val lockPokemonOnSet    : Boolean

    override fun printProperties()
    {
        Util.report("Player Details - ID: $playerID")
        Util.report("- Tournament ID: $tournamentID")
        Util.report("- Name: $name")
        Util.report("- Actor Type: $actorType")
        Util.report("- Seed: $seed")
        Util.report("- Original Seed: $originalSeed")
        Util.report("- Pokemon Team ID: $pokemonTeamID")
        Util.report("- Current Match: $currentMatchID")
        Util.report("- Final Placement: $finalPlacement")
        Util.report("- Pokemon Final: $pokemonFinal")
        Util.report("- Lock Pokemon On Set: $lockPokemonOnSet")
    }

}
