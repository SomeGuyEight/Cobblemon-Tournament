package com.cobblemontournament.common.match.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.match.MatchConnections
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_MATCH_STATUS
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_ROUND_MATCH_INDEX
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_TOURNAMENT_MATCH_INDEX
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_VICTOR_ID
import com.cobblemontournament.common.round.properties.RoundPropertiesHelper.DEFAULT_ROUND_INDEX
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.properties.Properties
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MatchProperties(
    matchID: UUID,
    tournamentID: UUID,
    roundID: UUID,
    roundIndex: Int,
    tournamentMatchIndex: Int,
    roundMatchIndex: Int,
    connections: MatchConnections = MatchConnections(),
    matchStatus: MatchStatus = DEFAULT_MATCH_STATUS,
    victorID: UUID? = DEFAULT_VICTOR_ID,
    playerMap: MutableMap<UUID, Int> = mutableMapOf(),
) : Properties<MatchProperties> {

    override val instance = this
    val name get() = "Match $roundMatchIndex ($tournamentMatchIndex)"
    var matchID: UUID = matchID
        set(value) {
            field = value
            emitChange()
        }
    var tournamentID: UUID = tournamentID
        set(value) {
            field = value
            emitChange()
        }
    var roundID: UUID = roundID
        set(value) {
            field = value
            emitChange()
        }
    var roundIndex = roundIndex
        set(value) {
            field = value
            emitChange()
        }
    var tournamentMatchIndex = tournamentMatchIndex
        set(value) {
            field = value
            emitChange()
        }
    var roundMatchIndex = roundMatchIndex
        set(value) {
            field = value
            emitChange()
        }
    /** exposed publicly as val, so observable can be set once & updated by the connections observable */
    val connections = connections.deepCopy()
    var matchStatus = matchStatus
        set(value) {
            field = value
            emitChange()
        }
    var victorID: UUID? = victorID
        set(value) {
            field = value
            emitChange()
        }
    var playerMap = TournamentUtil.shallowCopy(map = playerMap)
        set(value) {
            field = value
            emitChange()
        }

    override val helper = MatchPropertiesHelper
    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<MatchProperties>()

    init {
        registerObservable(connections.getChangeObservable())
    }

    constructor(uuid: UUID = UUID.randomUUID()) : this(
        matchID = uuid,
        tournamentID = UUID.randomUUID(),
        roundID = UUID.randomUUID(),
        roundIndex = DEFAULT_ROUND_INDEX,
        tournamentMatchIndex = DEFAULT_TOURNAMENT_MATCH_INDEX,
        roundMatchIndex = DEFAULT_ROUND_MATCH_INDEX,
    )

    private fun emitChange() = anyChangeObservable.emit((this))

    override fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable() = anyChangeObservable

    private fun registerObservable(observable: Observable<*>): Observable<*> {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit((this)) }
        return observable
    }

    companion object {
        private val HELPER = MatchPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNBTHelper(nbt = nbt)
    }

}
