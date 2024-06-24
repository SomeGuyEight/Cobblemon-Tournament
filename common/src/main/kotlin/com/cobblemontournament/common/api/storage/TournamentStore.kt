package com.cobblemontournament.common.api.storage

import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.storage.PokemonStore
import com.cobblemon.mod.common.api.storage.StoreCoordinates
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemontournament.common.api.tournament.TournamentProperties
import com.cobblemontournament.common.util.TournamentDataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.*

class TournamentStore(override val uuid: UUID)
    : PokemonStore<TournamentStorePosition>()
{
    constructor (
        tournamentID: UUID,
        properties: TournamentProperties,
        roundIDs: Set<UUID>,
        matchIDs: Set<UUID>,
        playerIDs: Set<UUID>
    ) :this(tournamentID) {
        this.properties = properties
        this.roundIDs.addAll(roundIDs)
        this.matchIDs.addAll(matchIDs)
        this.playerIDs.addAll(playerIDs)
    }
    var name = "Tournament Store"
        private set
    var properties: TournamentProperties = TournamentProperties()
        private set
    private var roundIDs = mutableSetOf<UUID>()
    private var matchIDs = mutableSetOf<UUID>()
    private var playerIDs = mutableSetOf<UUID>()

    private val observingUUIDs = mutableSetOf<UUID>()
    private val storeChangeObservable = SimpleObservable<Unit>()

    override fun initialize()
    {
        // TODO
        // ?? register observables for each of its children (round,match,player) ??
        // nothing is supposed to change here specifically though...
    }

    override fun getAnyChangeObservable() = storeChangeObservable

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

    /* !! not abstract override, but may be helpful to override super fun in future !!
    override fun sendPacketToObservers(packet: NetworkPacket<*>) = getObservingPlayers().forEach { it.sendPacket(packet) }
     */



    // TODO test if works
    override fun saveToNBT(nbt: CompoundTag): CompoundTag
    {
        // All that is needed is tournament ID & Properties
        //  - tournament ID is used to get rounds, matches, & players from storage
        //  - each store will be registered with store manager & the main factory when loading
        nbt.putUUID(TournamentDataKeys.TOURNAMENT_ID, uuid)


        // TODO save properties

        return nbt
    }

    // TODO test if works
    override fun loadFromNBT(nbt: CompoundTag): TournamentStore
    {
        // uuid = nbt.getUUID(TournamentDataKeys.TOURNAMENT_ID)

        // TODO all that is stored is tournament ID
        //  - use it to get data from store
        // TODO ?? need to pass the tournament manager in to handle retrieving custom stores ??

        return this
    }

    override fun savePositionToNBT(position: TournamentStorePosition, nbt: CompoundTag)
    {
        nbt.putUUID(TournamentDataKeys.STORE_POSITION, position.uuid)
    }
    override fun loadPositionFromNBT(nbt: CompoundTag): StoreCoordinates<TournamentStorePosition>
    {
        return StoreCoordinates(this, TournamentStorePosition(nbt.getUUID(TournamentDataKeys.STORE_POSITION)))
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not yet implemented") }
    override fun loadFromJSON(json: JsonObject): TournamentStore { TODO("Not yet implemented") }


/*

        !!!!!!!!        !!!!!!!!        !!!!!!!!        !!!!!!!!        !!!!!!!!

        Below here is blocking abstract or super methods that could affect data

        !!!!!!!!        !!!!!!!!        !!!!!!!!        !!!!!!!!        !!!!!!!!

 */

    // always returns empty iterator b/c not applicable here
    override fun iterator(): Iterator<Pokemon> = emptyList<Pokemon>().iterator()

    override fun isValidPosition(position: TournamentStorePosition): Boolean = false

    // !! need to block b/c more than one 'collection' !!
    override fun getFirstAvailablePosition() = null

    // always null, but maybe add ability to iterate through each team & get each pokemon...
    override operator fun get(position: TournamentStorePosition): Pokemon? = null

    // !! not abstract override, but always false b/c not applicable for this use case
    override fun add(pokemon: Pokemon): Boolean = false

    // always empty, b/c not applicable in this use case
    override fun setAtPosition(position: TournamentStorePosition, pokemon: Pokemon?) { }

    // !! not abstract override, but always empty b/c not applicable for this use case
    override operator fun set(position: TournamentStorePosition, pokemon: Pokemon) { }

    // !! not override, but needs to be blocked, or it will mess up storage
    override fun swap(position1: TournamentStorePosition, position2: TournamentStorePosition) { }

    // !! CAN'T override, but will be stopped at swap above, so it is all good
    // fun move(pokemon: Pokemon, position: T) { }

    // !! not abstract override, but not applicable here
    override fun remove(position: TournamentStorePosition): Boolean = false

    // !! not abstract override, but overriding to block for this use case
    override fun remove(pokemon: Pokemon): Boolean = false

    // !! not abstract override, but can override if needed in the future !!
    //open fun handleInvalidSpeciesNBT(nbt: NbtCompound) { }

    // !! not abstract override, but can override if needed !!
    //open fun handleInvalidSpeciesJSON(json: JsonObject) { }

}