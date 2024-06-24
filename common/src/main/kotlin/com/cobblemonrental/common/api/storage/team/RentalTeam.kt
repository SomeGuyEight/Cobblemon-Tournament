package com.cobblemonrental.common.api.storage.team

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.storage.StoreCoordinates
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemonrental.common.util.RentalDataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import kotlin.collections.*;
import java.util.*

class RentalTeam(val id: UUID, val store: RentalTeamStore) : Iterable<UUID>
{
    var name = ""
    private var _rentalID: UUID = UUID.randomUUID()
    private var _pokemonID =  mutableListOf<UUID>()
    private val observingUUIDs = mutableSetOf<UUID>()
    private val anyChangeObservable = SimpleObservable<RentalTeam>()
    val storeCoordinates = SettableObservable<StoreCoordinates<*>?>(null)

    fun rentalID(): UUID = _rentalID
    fun teamSize(): Int = _pokemonID.size

    fun getChangeObservable(): Observable<RentalTeam> = anyChangeObservable
    private fun emitObservableChange() = anyChangeObservable.emit(this)

    override fun iterator(): Iterator<UUID> = _pokemonID.asSequence().iterator()

    fun addObserver(player: ServerPlayer)
    {
        observingUUIDs.add(player.uuid)
        sendTo(player)
    }

    fun removeObserver(playerID: UUID) = observingUUIDs.remove(playerID)

    fun getObservingPlayers() = observingUUIDs.mapNotNull { it.getPlayer() }

    fun sendTo(player: ServerPlayer)
    {
        TODO("Not yet implemented")
        // ?? need to set up for display eventually ??
    }

    fun initialize(id: UUID?, pokemonID: List<UUID>?,name: String?): RentalTeam
    {
        if (id != null) _rentalID = id
        this.name = name?: "Rental Team"
        if (!pokemonID.isNullOrEmpty()) {
            pokemonID.forEach { rentalPokemon ->
                _pokemonID.add(rentalPokemon)
            }
        }
        emitObservableChange()
        return this
    }

    fun addPokemonID(pokemonID: List<UUID>): Iterator<UUID>
    {
        if (pokemonID.isNotEmpty()) {
            pokemonID.forEach { id ->
                _pokemonID.add(id)
            }
            emitObservableChange()
        }
        return iterator()
    }

    fun removePokemonID(pokemonID: List<UUID>)
    {
        if (pokemonID.isNotEmpty()) {
            pokemonID.forEach { id ->
                _pokemonID.remove(id)
            }
            emitObservableChange()
        }
    }

    /**
     * - resets the current list of pokemon
     * - returns the previous list's pokemon
     */
    fun clearPokemonID(pokemonID: List<UUID>): List<UUID>
    {
        val ref = _pokemonID
        _pokemonID = mutableListOf()
        emitObservableChange()
        return ref
    }

    fun saveRentalToNBT(nbt: CompoundTag): CompoundTag
    {
        nbt.putUUID(RentalDataKeys.RENTAL_TEAM_ID,_rentalID)
        var index = 0;
        _pokemonID.forEach { id ->
            nbt.putUUID(RentalDataKeys.RENTAL_POKEMON_ID + index++,id)
        }
        nbt.putInt(RentalDataKeys.SIZE,index)
        emitObservableChange()
        return nbt
    }

    fun loadRentalFromNBT(nbt: CompoundTag): RentalTeam
    {
        _rentalID = nbt.getUUID(RentalDataKeys.RENTAL_TEAM_ID)
        val count = nbt.getInt(RentalDataKeys.SIZE)
        for (slot in 0 until count) {
            if (nbt.contains(RentalDataKeys.RENTAL_POKEMON_ID + slot)) {
                _pokemonID.add(nbt.getUUID(RentalDataKeys.INDEX + slot))
            }
        }
        emitObservableChange()
        return this
    }

    // TODO test if works
    fun saveRentalToJSON(json: JsonObject): JsonObject
    {
        json.addProperty(RentalDataKeys.RENTAL_TEAM_ID,_rentalID.toString())
        var index = 0;
        _pokemonID.forEach { id ->
            json.addProperty(RentalDataKeys.RENTAL_POKEMON_ID + index++,id.toString())
        }
        json.addProperty(RentalDataKeys.SIZE,index)
        emitObservableChange()
        return json
    }

    // TODO test if works
    fun loadRentalFromJSON(json: JsonObject): RentalTeam
    {
        _rentalID = UUID.fromString(json.get(RentalDataKeys.RENTAL_TEAM_ID).asString)
        val count = json.getAsJsonPrimitive(RentalDataKeys.SIZE).asInt
        for (slot in 0 until count) {
            if (json.has(RentalDataKeys.RENTAL_POKEMON_ID + slot)) {
                // TODO may need to trim "" from string
                _pokemonID.add(UUID.fromString(json.get(RentalDataKeys.INDEX + slot).asString))
            }
        }
        emitObservableChange()
        return this
    }

}
