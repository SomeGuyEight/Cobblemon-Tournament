package com.cobblemonrental.common.api.storage.team

import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.storage.PokemonStore
import com.cobblemon.mod.common.api.storage.StoreCoordinates
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemonrental.common.api.storage.RentalStorePosition
import com.cobblemonrental.common.api.storage.pokemon.RentalPokemon
import com.cobblemonrental.common.util.RentalDataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.*

class RentalTeamStore(final override val uuid: UUID)
    : PokemonStore<RentalStorePosition>()
{
    constructor(uuid: UUID, name: String?) : this(uuid)
    {
        this.name = name?: "Rental Team Store"
    }

    private var name = ""
    private val rentalTeams = mutableMapOf<UUID, RentalTeam>()
    private val observingUUIDs = mutableSetOf<UUID>()
    private val storeChangeObservable = SimpleObservable<Unit>()

    fun mapSize(): Int = rentalTeams.size

    override fun getAnyChangeObservable() = storeChangeObservable

    // always returns empty iterator, but maybe add ability to iterate through each team & get each pokemon...
    override fun iterator(): Iterator<RentalPokemon> = emptyList<RentalPokemon>().iterator()

    // always null, but maybe add ability to iterate through each team & get each pokemon...
    override operator fun get(position: RentalStorePosition): RentalPokemon? = null

    fun getTeam(position: RentalStorePosition): RentalTeam? = rentalTeams[position.uuid]

    fun getFirst(): RentalTeam? = rentalTeams.values.firstOrNull()

    override fun getFirstAvailablePosition() = firstAvailable()?.let { RentalStorePosition(it) }
    private fun firstAvailable() : UUID?
    {
        // limit so no infinite loop. Very unlikely to hit, but...
        for (i in 50 downTo 1) {
            val uuid = UUID.randomUUID()
            if (!rentalTeams.containsKey(uuid)) {
                return uuid
            }
        }
        return null
    }

    override fun getObservingPlayers() = observingUUIDs.mapNotNull { it.getPlayer() }

    override fun sendTo(player: ServerPlayer)
    {
        TODO("Not yet implemented")
        // ?? need to set up for display eventually ??
    }

    fun addObserver(player: ServerPlayer)
    {
        observingUUIDs.add(player.uuid)
        sendTo(player)
    }
    fun removeObserver(playerID: UUID) = observingUUIDs.remove(playerID)

    override fun initialize()
    {
        rentalTeams.forEach { (uuid: UUID, team: RentalTeam) ->
            team.storeCoordinates .set(StoreCoordinates(this, RentalStorePosition(uuid)))
            team.getChangeObservable().subscribe { storeChangeObservable.emit(Unit) }
        }
    }

    // always empty, b/c not applicable in this use case
    fun addEmptyTeam(name: String?): RentalTeam?
    {
        val position = getFirstAvailablePosition() ?: return null
        val team = RentalTeam(position.uuid,this)
        team.initialize(position.uuid,null,name)
        rentalTeams[position.uuid] = team
        return team
    }

    // always empty, b/c not applicable in this use case
    fun addTeam(pokemonID: List<UUID>): RentalTeam?
    {
        val position = getFirstAvailablePosition() ?: return null
        val team = RentalTeam(position.uuid,this)
        team.initialize(position.uuid,pokemonID,null)
        rentalTeams[position.uuid] = team
        return team
    }

    // always empty, b/c not applicable in this use case
    fun setTeam(position: RentalStorePosition, pokemonID: List<UUID>): RentalTeam?
    {
        if (rentalTeams.containsKey(position.uuid)) {
            return null
        }
        val team = RentalTeam(position.uuid,this)
        team.initialize(position.uuid,pokemonID,null)
        rentalTeams[position.uuid] = team
        return team
    }

    fun updateTeam(position: RentalStorePosition, pokemonID: List<UUID>,name: String?): Iterator<UUID>?
    {
        if (!rentalTeams.containsKey(position.uuid)) {
            return null
        }
        val team = rentalTeams[position.uuid]
        if (team != null && name != null) {
            team.name = name
        }
        return team?.addPokemonID(pokemonID)
    }

    fun updateTeamName(position: RentalStorePosition, name: String?)
    {
        if (!rentalTeams.containsKey(position.uuid) && name != null) {
            rentalTeams[position.uuid]?.name = name
        }
    }

    fun removeTeam(position: RentalStorePosition, pokemonID: List<UUID>): RentalTeam?
    {
        if (!rentalTeams.containsKey(position.uuid)) {
            return null
        }
        val team = RentalTeam(position.uuid,this)
        team.initialize(position.uuid,pokemonID,null)
        rentalTeams[position.uuid] = team
        return team
    }

    /**
     * always empty, b/c not applicable in this use case
     */
    override fun setAtPosition(position: RentalStorePosition, pokemon: Pokemon?) { }

    override fun isValidPosition(position: RentalStorePosition): Boolean
    {
        return !this.rentalTeams.containsKey(position.uuid)
    }

    // !! not abstract override, but may be helpful to override super fun in future !!
    //override fun sendPacketToObservers(packet: NetworkPacket<*>) = getObservingPlayers().forEach { it.sendPacket(packet) }

    /**
     * !! not abstract override, but always false b/c not applicable for this use case
     */
    override fun add(pokemon: Pokemon): Boolean = false

    // !! not abstract override, but always empty b/c not applicable for this use case !!
    override operator fun set(position: RentalStorePosition, pokemon: Pokemon) { }

    // !! not override, but needs to be blocked, or it will mess up storage
    override fun swap(position1: RentalStorePosition, position2: RentalStorePosition) { }

    // !! CAN'T override, but will be stopped at swap above, so it is all good !!
    /*
    fun move(pokemon: Pokemon, position: T) {
        val currentPosition = pokemon.storeCoordinates.get() ?: return
        if (currentPosition.store != this) {
            return
        }
        swap(currentPosition.position as T, position)
    }
     */

    // !! not abstract override, but be can be simpler for this use case !!
    override fun remove(position: RentalStorePosition): Boolean
    {
        return rentalTeams.remove(position.uuid) != null
    }

    // !! not abstract override, but overriding to block for this use case !!
    override fun remove(pokemon: Pokemon): Boolean = false

    // !! not abstract override, but can override if needed in the future !!
    //open fun handleInvalidSpeciesNBT(nbt: NbtCompound) { }

    // TODO test if works
    override fun saveToNBT(nbt: CompoundTag): CompoundTag
    {
        var index = 0
        rentalTeams.forEach { (_: UUID, team: RentalTeam) ->
            nbt.put(
                RentalDataKeys.INDEX + index++,
                team.saveRentalToNBT(CompoundTag())
            )
        }
        nbt.putInt(RentalDataKeys.SIZE,index)
        return nbt
    }

    // TODO test if works
    override fun loadFromNBT(nbt: CompoundTag): RentalTeamStore
    {
        val count = nbt.getInt(RentalDataKeys.SIZE)
        for (slot in 0 until count) {
            if (nbt.contains(RentalDataKeys.INDEX + slot)) {
                val rentalTeamNBT = nbt.getCompound(RentalDataKeys.INDEX + slot)
                val teamID = rentalTeamNBT.getUUID(RentalDataKeys.RENTAL_TEAM_ID) // TODO does this work?
                val team = RentalTeam(teamID,this).loadRentalFromNBT(rentalTeamNBT)
                rentalTeams[teamID] = team
            }
        }
        return this
    }

    override fun savePositionToNBT(position: RentalStorePosition, nbt: CompoundTag)
    {
        nbt.putUUID(RentalDataKeys.RENTAL_STORE_POSITION, position.uuid)
    }

    override fun loadPositionFromNBT(nbt: CompoundTag): StoreCoordinates<RentalStorePosition>
    {
        return StoreCoordinates(
            this,
            RentalStorePosition(nbt.getUUID(RentalDataKeys.RENTAL_STORE_POSITION))
        )
    }

    // !! not abstract override, but can override if needed !!
    //open fun handleInvalidSpeciesJSON(json: JsonObject) { }

    // TODO test if works
    override fun saveToJSON(json: JsonObject): JsonObject
    {
        var index = 0
        rentalTeams.forEach { (_: UUID, team: RentalTeam) ->
            json.add(RentalDataKeys.INDEX + index++, team.saveRentalToJSON(JsonObject()))
        }
        json.addProperty(RentalDataKeys.SIZE,index)
        return json
    }

    // TODO test if works
    override fun loadFromJSON(json: JsonObject): RentalTeamStore
    {
        val count = json.get(RentalDataKeys.SIZE).asInt
        for (slot in 0 until count) {
            if (json.has(RentalDataKeys.INDEX + slot)) {
                val teamJson = json.getAsJsonObject(RentalDataKeys.INDEX + slot)
                val id = UUID.fromString(json.get(RentalDataKeys.INDEX + slot).asString)
                val team = RentalTeam(id,this).loadRentalFromJSON(teamJson)
                rentalTeams[id] = team
            }
        }
        return this
    }

}