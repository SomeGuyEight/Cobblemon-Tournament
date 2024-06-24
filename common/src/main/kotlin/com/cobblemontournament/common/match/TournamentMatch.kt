package com.cobblemontournament.common.match

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

class TournamentMatch (override val uuid: UUID)
    : PokemonStore<TournamentStorePosition>()
{
    constructor(
        matchID: UUID,
        tournamentID: UUID,
        roundID: UUID,
        tournamentMatchIndex: Int,
        roundMatchIndex: Int,
        status: MatchStatus,
        playerMap: HashMap<UUID,Int>?
    ) : this(matchID) {
        this.tournamentID = tournamentID
        this.roundID = roundID
        this.tournamentMatchIndex = tournamentMatchIndex
        this.roundMatchIndex = roundMatchIndex
        this.status = status
        if (playerMap != null) {
            this.playerMap.putAll(playerMap)
        }
    }

    var tournamentID = UUID.randomUUID()
        private set
    var roundID = UUID.randomUUID()
        private set
    var tournamentMatchIndex: Int = 0
        private set
    var roundMatchIndex: Int = 0
        private set
    var status: MatchStatus = MatchStatus.Empty
        get() {
            return updateStatus()
        }
        private set
    var victorID: UUID? = null
    // don't expose Map publicly -> use iterator below
    private val playerMap = mutableMapOf<UUID,Int>()
    fun playerMapIterator(): Iterator<Entry<UUID,Int>> = playerMap.entries.iterator()
    fun playerEntrySet(): Set<Entry<UUID,Int>> = playerMap.entries.toSet()

    private val observingUUIDs = mutableSetOf<UUID>()
    private val storeChangeObservable = SimpleObservable<Unit>()

    override fun initialize()
    {
        // TODO
        // ?? register observables ??
        // ?? or is it done by Tournament ??
    }

    fun trySetPlayer(playerID: UUID, team: Int)
    {
        if (!playerMap.containsKey(playerID)) {
            playerMap[playerID] = team
        }
    }

    fun updatePlayer(playerID: UUID, team: Int)
    {
        if (playerMap.containsKey(playerID) && playerMap[playerID] != team) {
            playerMap.remove(playerID)
            playerMap[playerID] = team
        }
    }

    fun removePlayer(playerID: UUID): Pair<UUID,Int>?
    {
        val team = playerMap.remove(playerID)
        return if (team != null) {
            updateStatus()
            return Pair(playerID,team)
        } else null
    }

    private fun updateStatus(): MatchStatus
    {
        var entry1: Pair<UUID,Int>? = null
        playerMap.firstNotNullOfOrNull {
            (playerID, team) -> entry1 = Pair(playerID,team)
        }
        if (entry1 == null) {
            status = MatchStatus.Empty
            return status
        }

        var entry2: Pair<UUID,Int>? = null
        playerMap.filter { (_,team) -> team != entry1!!.second }
            .firstNotNullOfOrNull { (playerID, team) -> entry2 = Pair(playerID,team) }
        if (entry2 == null) {
            status = MatchStatus.NotReady
            return status
        }

        // TODO add check for other match pre-reqs here (like 2v2 etc)

        if (status == MatchStatus.Error || status == MatchStatus.Empty || status == MatchStatus.NotReady) {
            status = MatchStatus.Ready
        }
        // other statuses should be updated on match start, completion, confirmation, etc
        return status
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
    override fun sendPacketToObservers(packet: NetworkPacket<*>) = getObservingPlayers().forEach { it.sendPacket(packet) } */

    // TODO test if works
    override fun saveToNBT(nbt: CompoundTag): CompoundTag
    {
        nbt.putUUID(TournamentDataKeys.MATCH_ID, uuid)
        nbt.putUUID(TournamentDataKeys.TOURNAMENT_ID, tournamentID)
        nbt.putUUID(TournamentDataKeys.ROUND_ID, roundID)
        nbt.putInt(TournamentDataKeys.TOURNAMENT_MATCH_INDEX, tournamentMatchIndex)
        nbt.putInt(TournamentDataKeys.ROUND_MATCH_INDEX, roundMatchIndex)
        if (playerMap.isNotEmpty()) {
            val playerMapNBT = CompoundTag()
            var index = 0
            for ((key, value) in playerMap) {
                playerMapNBT.putUUID(TournamentDataKeys.PLAYER_ID + index, key)
                playerMapNBT.putInt(TournamentDataKeys.TEAM_INDEX + index++, value)
            }
            playerMapNBT.putInt(TournamentDataKeys.SIZE, index)
        }
        if (victorID != null) {
            nbt.putUUID(TournamentDataKeys.VICTOR_ID, victorID!!)
        }
        return nbt
    }

    // TODO test if works
    override fun loadFromNBT(nbt: CompoundTag): TournamentMatch
    {
        // need to initialize roundID with constructor b/c of inheritance override...
        // uuid = nbt.getUUID(TournamentDataKeys.MATCH_ID)
        tournamentID = nbt.getUUID(TournamentDataKeys.TOURNAMENT_ID)
        roundID = nbt.getUUID(TournamentDataKeys.ROUND_ID)
        tournamentMatchIndex = nbt.getInt(TournamentDataKeys.TOURNAMENT_MATCH_INDEX)
        roundMatchIndex = nbt.getInt(TournamentDataKeys.ROUND_MATCH_INDEX)
        if (nbt.hasUUID(TournamentDataKeys.PLAYER_ID_TO_TEAM_INDEX_MAP)) {
            val playerMapNBT = nbt.getCompound(TournamentDataKeys.PLAYER_ID_TO_TEAM_INDEX_MAP)
            val size = playerMapNBT.getInt(TournamentDataKeys.SIZE)
            for (i in 0 until size) {
                val playerID = playerMapNBT.getUUID(TournamentDataKeys.PLAYER_ID + i)
                val team = playerMapNBT.getInt(TournamentDataKeys.TEAM_INDEX + i)
                playerMap[playerID] = team
            }
        }
        victorID = if (nbt.hasUUID(TournamentDataKeys.VICTOR_ID)) {
            nbt.getUUID(TournamentDataKeys.VICTOR_ID)
        } else null
        status = enumValueOf<MatchStatus>(nbt.getString(TournamentDataKeys.MATCH_STATUS))
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
    override fun loadFromJSON(json: JsonObject): TournamentMatch { TODO("Not yet implemented") }


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