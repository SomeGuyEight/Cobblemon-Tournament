package com.cobblemonrental.common.api.storage.pokemon

import com.cobblemon.mod.common.api.storage.InvalidSpeciesException
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemonrental.common.util.RentalDataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class RentalPokemon(private val store: RentalPokemonStore) : Pokemon()
{
    private var _rentalID: UUID = UUID.randomUUID()
    private var _originalPokemon: Pokemon = Pokemon()

    fun rentalID():UUID = _rentalID
    fun originalPokemon():Pokemon = _originalPokemon

    fun initialize(id: UUID?,pokemon: Pokemon): RentalPokemon
    {
        if (id != null) _rentalID = id
        _originalPokemon = pokemon
        return this
    }

    fun saveRentalToNBT(nbt: CompoundTag): CompoundTag
    {
        nbt.putUUID(RentalDataKeys.RENTAL_POKEMON_ID,_rentalID)
        nbt.put(RentalDataKeys.ORIGINAL_POKEMON,_originalPokemon.saveToNBT(CompoundTag()))
        return nbt
    }
    fun loadRentalFromNBT(nbt: CompoundTag): RentalPokemon
    {
        _rentalID = nbt.getUUID(RentalDataKeys.RENTAL_POKEMON_ID)
        val pokemonNBT = nbt.getCompound(RentalDataKeys.ORIGINAL_POKEMON)
        try {
            _originalPokemon = Pokemon().loadFromNBT(pokemonNBT)
        } catch (_: InvalidSpeciesException) {
            store.handleInvalidSpeciesNBT(pokemonNBT)
        }
        return this
    }

    // TODO test if works
    fun saveRentalToJSON(json: JsonObject): JsonObject
    {
        json.addProperty(RentalDataKeys.RENTAL_POKEMON_ID,_rentalID.toString())
        json.add(RentalDataKeys.ORIGINAL_POKEMON,_originalPokemon.saveToJSON(JsonObject()))
        return json
    }

    // TODO test if works
    fun loadRentalFromJSON(json: JsonObject): RentalPokemon
    {
        _rentalID = UUID.fromString(json.get(RentalDataKeys.RENTAL_POKEMON_ID).asString)
        val pokemonJSON = json.get(RentalDataKeys.ORIGINAL_POKEMON).asJsonObject
        try {
            _originalPokemon = Pokemon().loadFromJSON(pokemonJSON)
        } catch (_: InvalidSpeciesException) {
            store.handleInvalidSpeciesJSON(pokemonJSON)
        }
        return this
    }
}
