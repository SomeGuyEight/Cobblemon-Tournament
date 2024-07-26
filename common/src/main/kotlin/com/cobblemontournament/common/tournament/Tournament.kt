package com.cobblemontournament.common.tournament

import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.util.*
import com.google.gson.JsonObject
import com.someguy.storage.ClassStored
import com.someguy.storage.StoreCoordinates
import com.someguy.storage.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

/** &#9888; (UUID) constructor is needed for serialization method */
open class Tournament(
    protected val properties: TournamentProperties
) : ClassStored {

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable(value = null)

    val anyChangeObservable = SimpleObservable<Tournament>()

    override val name: String get() = properties.name
    override var uuid: TournamentID
        get() = properties.tournamentID
        protected set(value) { properties.tournamentID = value }
    var tournamentStatus
        get() = properties.tournamentStatus
        private set(value) { properties.tournamentStatus = value }
    val tournamentType: TournamentType get() = properties.tournamentType
    val challengeFormat: ChallengeFormat get() = properties.challengeFormat
    val maxParticipants: Int get() = properties.maxParticipants
    val teamSize: Int get() = properties.teamSize
    val groupSize:Int get() = properties.groupSize
    val minLevel: Int get() = properties.minLevel
    val maxLevel: Int get() = properties.maxLevel
    val showPreview: Boolean get() = properties.showPreview
    val totalRounds: Int get() = properties.rounds.size
    val totalMatches: Int get() = properties.matches.size
    val totalPlayers: Int get() = properties.players.size
    // TODO handle collection inside properties
    private val rounds get() = properties.rounds
    private val matches get() = properties.matches
    private val players get() = properties.players

    init {
        properties.getChangeObservable().subscribe { emitChange() }
    }

    /** &#9888; (UUID) constructor is needed for serialization method */
    constructor(tournamentID: TournamentID = UUID.randomUUID()) :
            this(TournamentProperties(tournamentID = tournamentID))

    override fun initialize() = this

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getChangeObservable() = anyChangeObservable

    fun getPlayerSet() = players.values.toSet()

    fun getCurrentMatch(playerID: PlayerID): TournamentMatch? {
        val player = players[playerID] ?: return null
        return if (player.tournamentID == uuid && (players[player.uuid] != null)) {
            matches[player.currentMatchID]
        } else {
            null
        }
    }

    fun containsPlayer(playerID: PlayerID) = players.contains(playerID)

    fun checkIfComplete(): Boolean {
        return if (tournamentStatus == TournamentStatus.FINALIZED) {
            true
        } else when (tournamentType) {
            TournamentType.SINGLE_ELIMINATION -> {
                val lastRoundIndex = rounds.size - 1
                val lastRound = rounds.values
                    .firstOrNull { it.roundIndex == lastRoundIndex }
                    ?: return false
                val lastMatchID = lastRound
                    .getMatchID(roundMatchIndex = 0)
                    ?: return false
                val lastMatch = matches[lastMatchID]
                    ?: return false
                return if (lastMatch.getUpdatedMatchStatus() == MatchStatus.FINALIZED) {
                    finalize()
                    return true
                } else {
                    false
                }
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
        if ( player.currentMatchID == finalMatch.uuid ) {
            return 1
        }
        return rounds[finalMatch.roundID]
            ?.matchMapSize
            ?.plus(other = 1)
            ?: -69420 // lolz
    }

    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        nbt.put(TOURNAMENT_PROPERTIES_KEY, properties.saveToNbt(nbt = CompoundTag()))
        return nbt
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO() }

    override fun loadFromNBT(nbt: CompoundTag): Tournament {
        properties.setFromNbt(nbt = nbt.getCompound(TOURNAMENT_PROPERTIES_KEY))
        return this
    }

    override fun loadFromJSON(json: JsonObject): ClassStored { TODO() }

    override fun printProperties() = properties.logDebug()

    fun displayOverviewInChat(player: ServerPlayer) = properties.displayInChat(player = player)

    fun displayResultsInChat(player: ServerPlayer) = properties.displayResultsInChat(player = player)

    companion object {
        fun loadFromNbt(nbt: CompoundTag): Tournament {
            return Tournament(
                TournamentProperties.loadFromNbt(
                    nbt = nbt.getCompound(TOURNAMENT_PROPERTIES_KEY),
                )
            )
        }
    }

}
