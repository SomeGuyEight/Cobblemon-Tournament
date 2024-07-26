package com.cobblemontournament.common.match

import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.match.properties.MatchProperties
import com.cobblemontournament.common.api.storage.MatchStore
import com.cobblemontournament.common.api.storage.PlayerStore
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.util.*
import com.someguy.storage.ClassStored
import com.google.gson.JsonObject
import com.someguy.storage.util.PlayerID
import com.someguy.storage.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

open class TournamentMatch(protected val properties: MatchProperties) : ClassStored {

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable(value = null)

    private val anyChangeObservable = SimpleObservable<TournamentMatch>()

    override val name: String get() = properties.name
    override var uuid: MatchID
        get() = properties.matchID
        protected set(value) { properties.matchID = value }
    val tournamentID: TournamentID get() = properties.tournamentID
    val roundID: RoundID get() = properties.roundID
    val roundIndex: Int get() = properties.roundIndex
    val tournamentMatchIndex: Int get() = properties.tournamentMatchIndex
    val roundMatchIndex: Int get() = properties.roundMatchIndex
    val matchConnections: MatchConnections get() = properties.connections
    private var matchStatus: MatchStatus
        get() = properties.matchStatus
        set(value) { properties.matchStatus = value }
    private var victorID: VictorID?
        get() = properties.victorID
        private set(value) { properties.victorID = value }
    // TODO handle with observable
    private val playerMap get() = properties.playerMap

    init {
        properties.getChangeObservable().subscribe { emitChange() }
    }

    /** &#9888; (UUID) constructor is needed for serialization method */
    constructor(matchID: MatchID = UUID.randomUUID()) : this(MatchProperties(matchID = matchID))

    override fun initialize() = this

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getChangeObservable() = anyChangeObservable

    fun playerEntrySet() = playerMap.toMutableMap()

    fun containsPlayer(playerID: UUID) = playerMap.containsKey(playerID)

    fun getPlayer(playerID: PlayerID) = playerMap[playerID]

    fun trySetPlayer(playerID: PlayerID, team: Int): Boolean {
        return if (!playerMap.containsKey(playerID)) {
            playerMap[playerID] = team
            emitChange()
            true
        } else {
            false
        }
    }

    fun removePlayer(playerID: PlayerID): Pair<UUID, Int>? {
        val teamIndex = playerMap.remove(playerID)
        return if (teamIndex != null) {
            getUpdatedMatchStatus()
            emitChange()
            playerID to teamIndex
        } else {
            null
        }
    }

    private fun getUpdatedProperties(): MatchProperties {
        getUpdatedMatchStatus()
        return properties
    }

    fun getUpdatedMatchStatus(): MatchStatus {
        when {
            victorID != null -> matchStatus = MatchStatus.FINALIZED
            playerMap.isEmpty() -> matchStatus = MatchStatus.EMPTY
            playerMap.size == 1 -> matchStatus = MatchStatus.NOT_READY
            else -> {
                val team: Int = playerMap.firstNotNullOf { (_ , t) -> t }
                // TODO add check for other match states or properties here (like 2v2 etc)
                matchStatus = if (playerMap.any { team != it.value }) {
                    MatchStatus.READY
                } else {
                    MatchStatus.NOT_READY
                }
            }
        }
        return matchStatus
    }

    // TODO clean up & condense
    fun updateVictorID(newVictorID: VictorID?) {
        if (victorID == newVictorID) {
            return
        }

        victorID = newVictorID?.let { _ ->
            // TODO add resetting match after victory update already applied
            getUpdatedMatchStatus()
            return
        }

        val tournament = TournamentStoreManager.getInstance(
            storeClass = TournamentStore::class.java,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
            instanceID = tournamentID,
        )

        val victorTeamIndex = playerMap[victorID]
        val victorNextMatch = matchConnections.victorNextMatch
            ?.let { nextMatchID ->
                TournamentStoreManager.getInstance(
                    storeClass = MatchStore::class.java,
                    storeID = tournamentID,
                    instanceID = nextMatchID,
                )
            }

        val defeatedNextMatch = matchConnections.defeatedNextMatch
            ?.let { nextMatchID ->
                TournamentStoreManager.getInstance(
                    storeClass = MatchStore::class.java,
                    storeID = tournamentID,
                    instanceID = nextMatchID,
                )
            }

        for ((playerID, team) in playerMap) {
            val player = TournamentStoreManager.getInstance(
                storeClass = PlayerStore::class.java,
                storeID = tournamentID,
                instanceID = playerID,
            ) ?: continue

            val serverPlayer = CobblemonTournament.getServerPlayer(playerID)

            if (team == victorTeamIndex) {
                handleVictor(victorNextMatch, player, serverPlayer, tournament)
            } else {
                handleDefeated(defeatedNextMatch, tournament, player, serverPlayer)
            }
        }
        getUpdatedMatchStatus()
    }

    private fun handleVictor(
        victorNextMatch: TournamentMatch?,
        player: TournamentPlayer,
        serverPlayer: ServerPlayer?,
        tournament: Tournament?,
    ) {
        if (victorNextMatch == null) {
            val textStart = "Congratulations Trainer ${player.name}! You won first place"
            val textEnd = tournament?.let { " in \"${it.name}\"" } ?: "!"
            player.finalPlacement = 1
            serverPlayer?.displayInChat(
                text = textStart + textEnd,
                color = ChatUtil.GREEN_FORMAT,
            )
            tournament?.checkIfComplete() ?: TODO()
        }
        player.currentMatchID = victorNextMatch?.uuid
    }

    private fun handleDefeated(
        defeatedNextMatch: TournamentMatch?,
        tournament: Tournament?,
        player: TournamentPlayer,
        serverPlayer: ServerPlayer?,
    ) {
        if (defeatedNextMatch == null) {
            tournament?.let { _ ->
                player.finalPlacement = tournament.getFinalPlacement(
                    player = player,
                    finalMatch = this,
                )
            }
            if (serverPlayer != null) {
                val textStart = "Congratulations Trainer ${player.name}! " +
                        "You finished in ${player.finalPlacement} place"
                val textEnd = tournament?.let { " in \"${it.name}\"" } ?: "!"

                serverPlayer.displayInChat(
                    text = textStart + textEnd,
                    color = ChatUtil.GREEN_FORMAT,
                )
            }
        }
        player.currentMatchID = defeatedNextMatch?.uuid
    }


    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        nbt.put(MATCH_PROPERTIES_KEY, properties.saveToNbt(CompoundTag()))
        return nbt
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO() }

    override fun loadFromNBT(nbt: CompoundTag): TournamentMatch {
        properties.setFromNbt(nbt.getCompound(MATCH_PROPERTIES_KEY))
        return this
    }

    override fun loadFromJSON(json: JsonObject): TournamentMatch { TODO() }

    override fun printProperties() = getUpdatedProperties().logDebug()

    companion object {
        fun loadFromNbt(nbt: CompoundTag): TournamentMatch {
            return TournamentMatch(
                MatchProperties.loadFromNbt(nbt.getCompound(MATCH_PROPERTIES_KEY))
            )
        }
    }

}
