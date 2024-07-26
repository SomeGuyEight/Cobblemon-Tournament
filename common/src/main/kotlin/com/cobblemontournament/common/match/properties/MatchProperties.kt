package com.cobblemontournament.common.match.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.match.MatchConnections
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.round.properties.DEFAULT_ROUND_INDEX
import com.cobblemontournament.common.util.*
import com.someguy.storage.Properties
import com.someguy.storage.util.SubscriptionMap
import com.someguy.storage.util.registerObservable
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MatchProperties(
    matchID: MatchID = UUID.randomUUID(),
    tournamentID: TournamentID = UUID.randomUUID(),
    roundID: RoundID = UUID.randomUUID(),
    roundIndex: Int = DEFAULT_ROUND_INDEX,
    tournamentMatchIndex: Int = DEFAULT_TOURNAMENT_MATCH_INDEX,
    roundMatchIndex: Int = DEFAULT_ROUND_MATCH_INDEX,
    connections: MatchConnections = MatchConnections(),
    matchStatus: MatchStatus = DEFAULT_MATCH_STATUS,
    victorID: VictorID? = DEFAULT_VICTOR_ID,
    playerMap: PlayerToTeamMap = mutableMapOf(),
) : Properties<MatchProperties> {

    override val instance = this
    override val helper = MatchPropertiesHelper

    val connections = connections.deepCopy()
    // TODO handle with observable
    val playerMap: PlayerToTeamMap = playerMap.toMutableMap()

    private val anyChangeObservable = SimpleObservable<MatchProperties>()
    private val subscriptionsMap: SubscriptionMap = mutableMapOf()

    private val matchIDObservable = subscribeTo(SettableObservable(matchID))
    private val tournamentIDObservable = subscribeTo(SettableObservable(tournamentID))
    private val roundIDObservable = subscribeTo(SettableObservable(roundID))
    private val roundIndexObservable = subscribeTo(SettableObservable(roundIndex))
    private val tournamentMatchIndexObservable =
        subscribeTo(SettableObservable(tournamentMatchIndex))
    private val roundMatchIndexObservable = subscribeTo(SettableObservable(roundMatchIndex))
    private val victorIDObservable = subscribeTo(SettableObservable(victorID))
    private val matchStatusObservable = subscribeTo(SettableObservable(matchStatus))

    val name get() = "Match $roundMatchIndex ($tournamentMatchIndex)"
    var matchID: MatchID
        get() = matchIDObservable.get()
        set(value) { matchIDObservable.set(value) }
    var tournamentID: TournamentID
        get() = tournamentIDObservable.get()
        set(value) { tournamentIDObservable.set(value) }
    var roundID: RoundID
        get() = roundIDObservable.get()
        set(value) { roundIDObservable.set(value) }
    var roundIndex: Int
        get() = roundIndexObservable.get()
        set(value) { roundIndexObservable.set(value) }
    var tournamentMatchIndex: Int
        get() = tournamentMatchIndexObservable.get()
        set(value) { tournamentMatchIndexObservable.set(value) }
    var roundMatchIndex: Int
        get() = roundMatchIndexObservable.get()
        set(value) { roundMatchIndexObservable.set(value) }
    var matchStatus: MatchStatus
        get() = matchStatusObservable.get()
        set(value) { matchStatusObservable.set(value) }
    var victorID: VictorID?
        get() = victorIDObservable.get()
        set(value) { victorIDObservable.set(value) }

    init {
        subscribeTo(connections.getChangeObservable())
    }

    private fun <T, O : Observable<T>> subscribeTo(observable: O): O {
        return observable.registerObservable(subscriptionsMap) { emitChange() }
    }

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getChangeObservable() = anyChangeObservable

    fun trySetPlayer(playerID: UUID, team: Int): Boolean {
        return if (!playerMap.containsKey(playerID)) {
            playerMap[playerID] = team
            emitChange()
            true
        } else {
            false
        }
    }

    fun removePlayer(playerID: UUID): Pair<UUID, Int>? {
        val teamIndex = playerMap.remove(playerID)
        return if (teamIndex != null) {
            emitChange()
            return Pair(playerID, teamIndex)
        } else {
            null
        }
    }

    companion object {
        private val HELPER = MatchPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbtHelper(nbt = nbt)
    }

}
