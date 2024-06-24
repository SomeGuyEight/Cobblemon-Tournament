package com.cobblemontournament.common.round

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
import kotlin.collections.Map.Entry

class TournamentRound (override val uuid: UUID)
    : PokemonStore<TournamentStorePosition>()
{
    constructor(
        roundID: UUID,
        tournamentID: UUID,
        roundIndex: Int,
        roundType: RoundType,
        matchMap: HashMap<Int,UUID> = hashMapOf()
    ) : this(roundID)
    {
        this.tournamentID = tournamentID
        this.roundIndex = roundIndex
        this.roundType = roundType
        this.matchMap = matchMap
    }

    var tournamentID = UUID.randomUUID()
        private set
    var roundIndex: Int = -1
        private set
    var roundType = RoundType.None
        private set
    // don't expose Map publicly -> use iterator below
    private var matchMap = mutableMapOf<Int,UUID>()
    fun matchEntryIterator(): Iterator<Entry<Int,UUID>> = matchMap.entries.iterator()
    fun getMatchMapSize(): Int = matchMap.size

    private val observingUUIDs = mutableSetOf<UUID>()
    private val storeChangeObservable = SimpleObservable<Unit>()

    override fun initialize()
    {
        // TODO
        // ?? register observables ??
        // nothing is supposed to change here though...
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
        nbt.putUUID(TournamentDataKeys.TOURNAMENT_ID,tournamentID)
        nbt.putUUID(TournamentDataKeys.ROUND_ID,uuid)
        nbt.putInt(TournamentDataKeys.ROUND_INDEX,roundIndex)
        nbt.putString(TournamentDataKeys.ROUND_TYPE,roundType.toString())
        if (matchMap.isNotEmpty()) {
            val matchMapNBT = CompoundTag()
            var index = 0
            for ((roundMatchIndex,matchID) in matchMap) {
                matchMapNBT.putInt(TournamentDataKeys.ROUND_MATCH_INDEX + index, roundMatchIndex)
                matchMapNBT.putUUID(TournamentDataKeys.MATCH_ID + index++, matchID)
            }
            matchMapNBT.putInt(TournamentDataKeys.SIZE, matchMap.size)
            nbt.put(TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID_MAP, matchMapNBT)
        }
        return nbt
    }

    // TODO test if works
    override fun loadFromNBT(nbt: CompoundTag): TournamentRound
    {
        // need to initialize roundID with constructor b/c of inheritance override...
        // uuid = nbt.getUUID(TournamentDataKeys.ROUND_ID)
        tournamentID = nbt.getUUID(TournamentDataKeys.TOURNAMENT_ID)
        roundIndex = nbt.getInt(TournamentDataKeys.ROUND_INDEX)
        roundType = enumValueOf<RoundType>(nbt.getString(TournamentDataKeys.ROUND_TYPE))

        if (nbt.hasUUID(TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID_MAP)) {
            val matchMapNBT = nbt.getCompound(TournamentDataKeys.ROUND_MATCH_INDEX_TO_ID_MAP)
            val size = matchMapNBT.getInt(TournamentDataKeys.SIZE)
            for (i in 0 until size) {
                val roundMatchIndex = matchMapNBT.getInt(TournamentDataKeys.ROUND_MATCH_INDEX + i)
                val matchID = matchMapNBT.getUUID(TournamentDataKeys.MATCH_ID + i)
                matchMap[roundMatchIndex] = matchID
            }
        }
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
    override fun loadFromJSON(json: JsonObject): TournamentRound { TODO("Not yet implemented") }


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
