package com.cobblemontournament.common.tournament

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.someguy.storage.classstored.ClassStored
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_PROPERTIES_KEY
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.match.MatchStatus
import com.google.gson.JsonObject
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

/** &#9888; (UUID) constructor is needed for serialization method */
open class Tournament(protected val properties: TournamentProperties) : ClassStored {

    override val name get() = properties.name
    override var uuid get() = properties.tournamentID
        protected set(value) { properties.tournamentID = value }
    private var tournamentStatus get() = properties.tournamentStatus
        set(value) { properties.tournamentStatus = value }
    private val tournamentType get() = properties.tournamentType
    val challengeFormat get() = properties.challengeFormat
    val maxParticipants get() = properties.maxParticipants
    val teamSize get() = properties.teamSize
    val groupSize get() = properties.groupSize
    val minLevel get() = properties.minLevel
    val maxLevel get() = properties.maxLevel
    val showPreview get() = properties.showPreview
    val totalRounds get() = properties.rounds.size
    val totalMatches get() = properties.matches.size
    val totalPlayers get() = properties.players.size
    // TODO handle collection inside properties
    private val rounds get() = properties.rounds
    // TODO handle collection inside properties
    private val matches get() = properties.matches
    // TODO handle collection inside properties
    protected val players get() = properties.players

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> = SettableObservable(value = null)
    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<Tournament>()

    constructor(uuid: UUID = UUID.randomUUID()) : this(TournamentProperties(uuid))

    override fun initialize(): Tournament {
        registerObservable(observable = properties.anyChangeObservable)
        return this
    }

    fun getPlayerSet() = players.values.toSet()

    fun containsPlayerID(playerID: UUID) = players.contains(playerID)

    fun getCurrentMatch(playerID: UUID): TournamentMatch? {
        val player = players[playerID]
            ?: return null
        return if (player.tournamentID == uuid && players[player.uuid] != null) {
            matches[player.currentMatchID]
        } else {
            null
        }
    }

    fun checkComplete(): Boolean {
        return if (tournamentStatus == TournamentStatus.FINALIZED) {
            true
        } else when (tournamentType) {
            TournamentType.SINGLE_ELIMINATION -> {
                val lastRoundIndex = rounds.size - 1
                val lastRound = rounds.values.firstOrNull { it.roundIndex == lastRoundIndex }
                    ?: return false // TODO log?
                val lastMatchID = lastRound.getMatchID(roundMatchIndex = 0)
                    ?: return false // TODO log?
                val lastMatch = matches[lastMatchID]
                    ?: return false // TODO log?
                return if (lastMatch.matchStatus == MatchStatus.FINALIZED) {
                    finalize()
                    return true
                } else false
            }
            else -> false
            // TODO add other tournament types
        }
    }

    private fun finalize() {
        tournamentStatus = TournamentStatus.FINALIZED
        TournamentStoreManager.transferInstance(
            storeClass = TournamentStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            newStoreID = TournamentStoreManager.INACTIVE_STORE_ID,
            instance = this,
        )
    }

    fun getFinalPlacement(player: TournamentPlayer, finalMatch: TournamentMatch): Int {
        // TODO add switch for other tournament types
        // this is for single elimination
        // - if the player won their last match, they should be #1 and never get here...
        if ( player.uuid == finalMatch.victorID ) {
            return 1
        }
        return rounds[finalMatch.roundID]?.matchMapSize?.plus(other = 1) ?: -69420 // lolz
    }

    private fun registerObservable(observable: Observable<*>): Observable<*> {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit((this)) }
        return observable
    }

    fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable() = anyChangeObservable

    override fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.put(TOURNAMENT_PROPERTIES_KEY, properties.saveToNBT(nbt = CompoundTag()))
        return nbt
    }
    override fun loadFromNBT(nbt: CompoundTag): Tournament {
        properties.setFromNBT(nbt = nbt.getCompound(TOURNAMENT_PROPERTIES_KEY))
        return this
    }
    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not yet implemented") }
    override fun loadFromJSON(json: JsonObject): ClassStored { TODO("Not yet implemented") }

    override fun printProperties() = properties.logDebug()
    fun displayOverviewInChat(player: ServerPlayer) = properties.displayInChat(player = player)
    fun displayResultsInChat(player: ServerPlayer) = properties.displayResultsInChat(player = player)

    companion object {
        /** &#9888; Observables will be broken if [initialize] is not called after construction */
        fun loadFromNbt(nbt: CompoundTag): Tournament {
            return Tournament(
                TournamentProperties.loadFromNbt(nbt = nbt.getCompound(TOURNAMENT_PROPERTIES_KEY))
            )
        }
    }

}
