package com.cobblemontournament.common.api.pokemon;

import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.UUID;
import java.util.Vector;

public record PokemonEntry(
        UUID id,
        Vector<Pokemon> pokemon
) {

}
