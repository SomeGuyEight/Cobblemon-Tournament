package com.cobblemontournament.common.player

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.storage.PokemonStore
import com.cobblemon.mod.common.api.storage.StoreCoordinates
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemontournament.common.api.storage.TournamentStorePosition
import com.cobblemontournament.common.util.TournamentDataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.*

class TournamentPlayer  (override val uuid: UUID)
    : PokemonStore<TournamentStorePosition>()
{
    constructor(
        playerID: UUID,
        tournamentID: UUID,
        actorType: ActorType,
        seed: Int,
        pokemonTeamID: UUID?,
        currentMatchID: UUID?
    ) : this(playerID)
    {
        this.tournamentID = tournamentID
        this.actorType = actorType
        this.seed = seed
        this.pokemonTeamID = pokemonTeamID
        this.currentMatchID = currentMatchID
    }

    var tournamentID: UUID? = null
        private set
    var actorType = ActorType.PLAYER
        private set
    var seed: Int = 0
        private set
    var pokemonTeamID: UUID? = null
        private set
    var currentMatchID: UUID? = null
        private set
    var finalPlacement: Int = -1

    private val observingUUIDs = mutableSetOf<UUID>()
    private val storeChangeObservable = SimpleObservable<Unit>()

    override fun initialize()
    {
        // TODO
        // ?? register observables ??
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
        nbt.putUUID(TournamentDataKeys.TOURNAMENT_ID,tournamentID!!)
        nbt.putUUID(TournamentDataKeys.PLAYER_ID, uuid)
        nbt.putString(TournamentDataKeys.ACTOR_TYPE, actorType.toString())
        nbt.putInt(TournamentDataKeys.SEED, seed)
        nbt.putInt(TournamentDataKeys.FINAL_PLACEMENT, finalPlacement)
        if (pokemonTeamID != null) {
            nbt.putUUID(TournamentDataKeys.POKEMON_TEAM_ID, pokemonTeamID!!)
        }
        if (currentMatchID != null) {
            nbt.putUUID(TournamentDataKeys.MATCH_ID, currentMatchID!!)
        }
        return nbt
    }

    // TODO test if works
    override fun loadFromNBT(nbt: CompoundTag): TournamentPlayer
    {
        // need to initialize roundID with constructor b/c of inheritance override...
        // uuid = nbt.getUUID(TournamentDataKeys.PLAYER_ID)
        actorType = enumValueOf<ActorType>(nbt.getString(TournamentDataKeys.ACTOR_TYPE))
        seed = nbt.getInt(TournamentDataKeys.SEED)
        finalPlacement = nbt.getInt(TournamentDataKeys.FINAL_PLACEMENT)
        pokemonTeamID = if (nbt.hasUUID(TournamentDataKeys.POKEMON_TEAM_ID)) {
            nbt.getUUID(TournamentDataKeys.PLAYER_ID)
        } else null
        currentMatchID = if (nbt.hasUUID(TournamentDataKeys.CURRENT_MATCH_ID)) {
            nbt.getUUID(TournamentDataKeys.CURRENT_MATCH_ID)
        } else null
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
    override fun loadFromJSON(json: JsonObject): TournamentPlayer { TODO("Not yet implemented") }


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