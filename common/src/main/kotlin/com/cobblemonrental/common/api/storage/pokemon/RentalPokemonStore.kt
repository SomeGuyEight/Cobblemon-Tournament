package com.cobblemonrental.common.api.storage.pokemon

import com.cobblemon.mod.common.api.reactive.Observable.Companion.stopAfter
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.storage.*
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemonrental.common.api.storage.RentalStorePosition
import com.cobblemonrental.common.util.RentalDataKeys
import com.google.gson.JsonObject
import java.util.UUID
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

open class RentalPokemonStore(final override val uuid: UUID)
    : PokemonStore<RentalStorePosition>()
{
        constructor(uuid: UUID,name: String?) : this(uuid)
        {
            this.name = name?: "Rental Pokemon Store"
        }

    private var name = ""
    private val pokemon = mutableMapOf<UUID, RentalPokemon>()
    private val observingUUIDs = mutableSetOf<UUID>()
    private val storeChangeObservable = SimpleObservable<Unit>()

    fun mapSize(): Int = pokemon.size

    override fun getAnyChangeObservable() = storeChangeObservable

    override fun iterator(): Iterator<RentalPokemon> = pokemon.values.iterator()

    override operator fun get(position: RentalStorePosition) : RentalPokemon? = pokemon[position.uuid]

    override fun getFirstAvailablePosition() = firstAvailable()?.let { RentalStorePosition(it) }
    private fun firstAvailable() : UUID?
    {
        // limit so no infinite loop. Very unlikely to hit, but...
        for (i in 50 downTo 1) {
            val uuid = UUID.randomUUID()
            if (!pokemon.containsKey(uuid)) {
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
        // TODO figure out what is going on here
        //      I don't fully grasp what is happening,
        //      but it seems to be registering/loading the pokemon in the store & at the position
        pokemon.forEach { (uuid: UUID, pokemon: RentalPokemon) ->
            pokemon.storeCoordinates .set(
                StoreCoordinates(this, RentalStorePosition(uuid)))
            pokemon.getChangeObservable().pipe(
                stopAfter { it.storeCoordinates.get()?.store != this } )
                .subscribe{ storeChangeObservable.emit(Unit) }
        }
    }

    override fun setAtPosition(position: RentalStorePosition, pokemon: Pokemon?)
    {
        if (this.pokemon.containsKey(position.uuid) || pokemon == null) {
            return
        }
        this.pokemon[position.uuid] = RentalPokemon(this).initialize(position.uuid,pokemon)
        storeChangeObservable.emit(Unit)
    }

    override fun isValidPosition(position: RentalStorePosition): Boolean
    {
        return !this.pokemon.containsKey(position.uuid)
    }

    // !! not abstract override, but may be helpful to override super fun in future !!
    //override fun sendPacketToObservers(packet: NetworkPacket<*>) = getObservingPlayers().forEach { it.sendPacket(packet) }

    // !! not abstract override, but block until interactions are figured out !!
    override fun add(pokemon: Pokemon): Boolean {
        /* implementation in super:
        remove(pokemon)
        val position = getFirstAvailablePosition() ?: return false // Couldn't fit, shrug emoji
        set(position, pokemon)
        return true
         */
        return false
    }

    // !! not abstract override, but super fun will override current pokemon !!
    override operator fun set(position: RentalStorePosition, pokemon: Pokemon)
    {
        setAtPosition(position, pokemon) // will only set if no pokemon is at position
    }

    // !! not override, but needs to be blocked, or it will mess up storage
    override fun swap(position1: RentalStorePosition, position2: RentalStorePosition) { }

    //!! CAN'T override, but will be stopped at swap above, so it is all good !!
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
        return pokemon.remove(position.uuid) != null
    }

    // !! not abstract override, but overriding to address pokemon stored in a mutableMap !!
    override fun remove(pokemon: Pokemon): Boolean
    {
        if (pokemon !is RentalPokemon) {
            return false
        }
        val value = this.pokemon[pokemon.rentalID()]
        if (value != null){
            return this.pokemon.remove(pokemon.rentalID()) != null
        }
        return false
    }

    // !! not abstract override, but can override if needed in the future !!
    //open fun handleInvalidSpeciesNBT(nbt: NbtCompound) { }

    // TODO test if functioning
    override fun saveToNBT(nbt: CompoundTag): CompoundTag
    {
        var index = 0
        pokemon.forEach { (_: UUID, pokemon: RentalPokemon) ->
            nbt.put(
                RentalDataKeys.INDEX + index++,
                pokemon.saveToNBT(pokemon.saveRentalToNBT(CompoundTag()))
            )
        }
        nbt.putInt(RentalDataKeys.SIZE,index)
        return nbt
    }

    // TODO test if functioning
    override fun loadFromNBT(nbt: CompoundTag): RentalPokemonStore
    {
        val count = nbt.getInt(RentalDataKeys.SIZE)
        for (slot in 0 until count) {
            if (nbt.contains(RentalDataKeys.INDEX + slot)) {
                val rentalPokemonNBT = nbt.getCompound(RentalDataKeys.INDEX + slot)
                val rentalPokemon = RentalPokemon(this).loadRentalFromNBT(rentalPokemonNBT)
                pokemon[rentalPokemon.uuid] = rentalPokemon
            }
        }
        return this
    }

    // TODO test if functioning
    override fun savePositionToNBT(position: RentalStorePosition, nbt: CompoundTag)
    {
        nbt.putUUID(RentalDataKeys.RENTAL_STORE_POSITION, position.uuid)
    }

    // TODO test if functioning
    override fun loadPositionFromNBT(nbt: CompoundTag): StoreCoordinates<RentalStorePosition>
    {
        return StoreCoordinates(
            this,
            RentalStorePosition(nbt.getUUID(RentalDataKeys.RENTAL_STORE_POSITION))
        )
    }

    // !! not abstract override, but can override if needed !!
    //open fun handleInvalidSpeciesJSON(json: JsonObject) { }

    // TODO test if functioning
    override fun saveToJSON(json: JsonObject): JsonObject
    {
        var index = 0
        pokemon.forEach { (_: UUID, pokemon: RentalPokemon) ->
            json.add(RentalDataKeys.INDEX + index++, pokemon.saveRentalToJSON(JsonObject()))
        }
        json.addProperty(RentalDataKeys.SIZE,index)
        return json
    }

    // TODO test if functioning
    override fun loadFromJSON(json: JsonObject): RentalPokemonStore
    {
        val count = json.get(RentalDataKeys.SIZE).asInt
        for (slot in 0 until count) {
            if (json.has(RentalDataKeys.INDEX + slot)) {
                val rentalPokemonJSON = json.getAsJsonObject(RentalDataKeys.INDEX + slot)
                val rentalPokemon = RentalPokemon(this).loadRentalFromJSON(rentalPokemonJSON)
                pokemon[rentalPokemon.uuid] = rentalPokemon
            }
        }
        return this
    }

}

/* save examples
// from PCBox

for (slot in 0 until POKEMON_PER_BOX) {
    val pokemon = pokemon[slot] ?: continue
    json.add(DataKeys.STORE_SLOT + slot, pokemon.saveToJSON(JsonObject()))
}
return json

// from PCStore // not as applicable unless storage is divided at some point

json.addProperty(DataKeys.STORE_BOX_COUNT, boxes.size.toShort())
json.addProperty(DataKeys.STORE_BOX_COUNT_LOCKED, lockedSize)
boxes.forEachIndexed { index, box ->
    json.add(DataKeys.STORE_BOX + index, box.saveToJSON(JsonObject()))
}
json.add(DataKeys.STORE_BACKUP, backupStore.saveToJSON(JsonObject()))
return json
 */

/* load examples
// from bottomless store // most applicable here

var i = -1
while (json.has(DataKeys.STORE_SLOT + ++i)) {
   val pokemonJSON = json.getAsJsonObject(DataKeys.STORE_SLOT + i)
   try {
       pokemon.add(Pokemon().loadFromJSON(pokemonJSON))
   } catch (_: InvalidSpeciesException) {
       handleInvalidSpeciesJSON(pokemonJSON)
   }
}
return this


// from PCBox

for (slot in 0 until POKEMON_PER_BOX) {
if (json.has(DataKeys.STORE_SLOT + slot)) {
   val pokemonJson = json.getAsJsonObject(DataKeys.STORE_SLOT + slot)
   try {
       pokemon[slot] = Pokemon().loadFromJSON(pokemonJson)
   } catch (_: InvalidSpeciesException) {
       pc.handleInvalidSpeciesJSON(pokemonJson)
   }
}
}


// from PCStore // not as applicable unless storage is divided at some point

val boxCountStored = json.get(DataKeys.STORE_BOX_COUNT).asShort
for (boxNumber in 0 until boxCountStored) {
   boxes.add(PCBox(this).loadFromJSON(json.getAsJsonObject(DataKeys.STORE_BOX + boxNumber)))
}
lockedSize = json.get(DataKeys.STORE_BOX_COUNT_LOCKED).asBoolean
if (!lockedSize && boxes.size != Cobblemon.config.defaultBoxCount) {
   resize(newSize = Cobblemon.config.defaultBoxCount, lockNewSize = false)
} else {
   tryRestoreBackedUpPokemon()
}

removeDuplicates()

return this
*/
