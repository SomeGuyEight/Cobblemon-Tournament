package com.cobblemontournament.common.tournament

import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.api.storage.store.TournamentStore
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.google.gson.JsonObject
import com.sg8.storage.StoreCoordinates
import com.sg8.storage.TypeStored
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

open class Tournament(protected val properties: TournamentProperties) : TypeStored {

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable(value = null)

    val anyChangeObservable = SimpleObservable<Tournament>()

    override val name: String get() = properties.name
    override val uuid: UUID get() = properties.uuid
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
    val totalRounds: Int get() = roundMap.size
    val totalMatches: Int get() = matchMap.size
    val totalPlayers: Int get() = playerMap.size
    val roundMap get() = properties.roundMap
    val matchMap get() = properties.matchMap
    val playerMap get() = properties.playerMap


    init {
        properties.observable.subscribe { emitChange() }
    }


    constructor(tournamentUuid: UUID = UUID.randomUUID()) :
            this(TournamentProperties(uuid = tournamentUuid))

    override fun initialize() = this

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getObservable() = anyChangeObservable

    fun getPlayerSet() = playerMap.values.toSet()

    fun getCurrentMatch(playerUuid: UUID): TournamentMatch? {
        val player = playerMap[playerUuid] ?: return null
        return if (player.uuid == uuid && (playerMap[player.uuid] != null)) {
            matchMap[player.currentMatchID]
        } else {
            null
        }
    }

    fun containsPlayer(playerID: UUID) = playerMap.contains { it.value.playerID == playerID }

    fun checkIfComplete(): Boolean {
        return if (tournamentStatus == TournamentStatus.FINALIZED) {
            true
        } else when (tournamentType) {
            TournamentType.SINGLE_ELIMINATION -> {
                val lastRoundIndex = roundMap.size - 1
                roundMap.firstValueOrNull { it.value.roundIndex == lastRoundIndex }
                    ?.getMatchID(roundMatchIndex = 0)
                    ?.let { lastMatchID -> matchMap[lastMatchID] }
                    ?.let { lastMatch ->
                        if (lastMatch.getUpdatedMatchStatus() == MatchStatus.FINALIZED) {
                            finalize()
                            return true
                        }
                    }
                return false
            }
            else -> false
            // TODO add other tournament types
        }
    }

    private fun finalize() {
        tournamentStatus = TournamentStatus.FINALIZED
        TournamentStoreManager.transferInstance(
            storeClass = TournamentStore::class.java,
            currentStoreID = TournamentStoreManager.ACTIVE_STORE_ID,
            newStoreID = TournamentStoreManager.INACTIVE_STORE_ID,
            instance = this,
        )
    }

    fun getFinalPlacement(player: TournamentPlayer, finalMatch: TournamentMatch): Int {
        if (player.currentMatchID == finalMatch.uuid) {
            return 1
        }
        return roundMap[finalMatch.roundID]?.matchMapSize?.plus(other = 1) ?: -69420 // lolz
    }

    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        nbt.put(DataKeys.TOURNAMENT_PROPERTIES, properties.saveToNbt(nbt = CompoundTag()))
        return nbt
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO() }

    override fun loadFromNBT(nbt: CompoundTag): Tournament {
        properties.setFromNbt(nbt = nbt.getCompound(DataKeys.TOURNAMENT_PROPERTIES))
        return this
    }

    override fun loadFromJSON(json: JsonObject): Tournament { TODO() }

    fun deepCopy() = Tournament(properties.deepCopy())

    fun copy() = Tournament(properties.copy())

    override fun printProperties() = properties.printDebug()

    fun displayOverviewInChat(player: ServerPlayer) = properties.displayInChat(player)

    fun displayResultsInChat(player: ServerPlayer) = properties.displayResultsInChat(player)

    companion object {

        fun loadFromNbt(nbt: CompoundTag): Tournament {
            return Tournament(
                TournamentProperties.loadFromNbt(nbt.getCompound(DataKeys.TOURNAMENT_PROPERTIES))
            )
        }

        fun victorNextMatchIndex(roundMatchIndex: Int, roundIndex: Int, roundCount: Int): Int? {
            return if (roundIndex + 1 < roundCount) {
                roundMatchIndex shr 1
            } else {
                null
            }
        }

        fun defeatedNextMatchIndex(tournamentType: TournamentType): Int? {
            return when (tournamentType) {
                TournamentType.SINGLE_ELIMINATION -> null
                TournamentType.DOUBLE_ELIMINATION -> TODO()
                TournamentType.ROUND_ROBIN -> TODO()
                TournamentType.VGC -> TODO()
            }
        }

        fun previousMatchIndices(roundMatchIndex: Int, roundIndex: Int): Pair<Int?, Int?> {
            return if (roundIndex > 0) {
                (roundMatchIndex * 2) to ((roundMatchIndex * 2) + 1)
            } else {
                null to null
            }
        }

    }

}
