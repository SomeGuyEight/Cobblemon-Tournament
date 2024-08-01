package com.cobblemontournament.common.match.properties

import com.cobblemon.mod.common.api.reactive.*
import com.cobblemontournament.common.match.*
import com.cobblemontournament.common.round.properties.DEFAULT_ROUND_INDEX
import com.sg8.collections.reactive.map.*
import com.sg8.properties.DefaultProperties
import net.minecraft.nbt.CompoundTag
import java.util.UUID

typealias PlayerTeamMap = ObservableMap<UUID, Int>
typealias MutablePlayerTeamMap = MutableObservableMap<UUID, Int>

class MatchProperties(
    uuid: UUID = UUID.randomUUID(),
    tournamentID: UUID = UUID.randomUUID(),
    roundID: UUID = UUID.randomUUID(),
    roundIndex: Int = DEFAULT_ROUND_INDEX,
    tournamentMatchIndex: Int = DEFAULT_TOURNAMENT_MATCH_INDEX,
    roundMatchIndex: Int = DEFAULT_ROUND_MATCH_INDEX,
    matchStatus: MatchStatus = DEFAULT_MATCH_STATUS,
    victorID: UUID? = DEFAULT_VICTOR_ID,
    matchConnections: MatchConnections? = null,
    playerMap: PlayerTeamMap? = null,
) : DefaultProperties<MatchProperties> {

    override val instance = this
    override val helper = MatchPropertiesHelper
    override val observable = SimpleObservable<MatchProperties>()
    private val subscriptionsMap: MutableMap<Observable<*>, ObservableSubscription<*>> = mutableMapOf()

    private val _uuid = SettableObservable(uuid).subscribe()
    private val _tournamentID = SettableObservable(tournamentID).subscribe()
    private val _roundID = SettableObservable(roundID).subscribe()
    private val _roundIndex = SettableObservable(roundIndex).subscribe()
    private val _tournamentMatchIndex = SettableObservable(tournamentMatchIndex).subscribe()
    private val _roundMatchIndex = SettableObservable(roundMatchIndex).subscribe()
    private val _matchStatus = SettableObservable(matchStatus).subscribe()
    private val _victorID = SettableObservable(victorID).subscribe()
    private var _matchConnections = matchConnections?.deepCopy() ?: MatchConnections()
    private var _playerMap = playerMap?.mutableCopy() ?: observableMapOf()

    val name: String get() = "Match $roundMatchIndex ($tournamentMatchIndex)"
    var uuid: UUID
        get() = _uuid.get()
        set(value) { _uuid.set(value) }
    var tournamentID: UUID
        get() = _tournamentID.get()
        set(value) { _tournamentID.set(value) }
    var roundID: UUID
        get() = _roundID.get()
        set(value) { _roundID.set(value) }
    var roundIndex: Int
        get() = _roundIndex.get()
        set(value) { _roundIndex.set(value) }
    var tournamentMatchIndex: Int
        get() = _tournamentMatchIndex.get()
        set(value) { _tournamentMatchIndex.set(value) }
    var roundMatchIndex: Int
        get() = _roundMatchIndex.get()
        set(value) { _roundMatchIndex.set(value) }
    var matchStatus: MatchStatus
        get() = _matchStatus.get()
        set(value) { _matchStatus.set(value) }
    var victorID: UUID?
        get() = _victorID.get()
        set(value) { _victorID.set(value) }
    var matchConnections: MatchConnections
        get() = _matchConnections
        set(value) {
            replaceSubscription(old = _matchConnections.observable, new = value.observable)
            _matchConnections = value
        }
    var playerMap: MutablePlayerTeamMap
        get() = _playerMap
        set(value) { _playerMap = replaceSubscription(old = _playerMap, new = value) }


    init {
        _matchConnections.observable.subscribe()
        _playerMap.subscribe()
    }


    private fun <T, O : Observable<T>> replaceSubscription(old: O, new: O): O {
        old.unsubscribe()
        return new.subscribe()
    }

    private fun <T, O : Observable<T>> O.subscribe(): O {
        subscriptionsMap[this] = this.subscribe { emitChange() }
        return this
    }

    private fun <T, O : Observable<T>> O.unsubscribe(): O {
        subscriptionsMap[this]?.unsubscribe()
        return this
    }

    override fun emitChange() = observable.emit(this)

    companion object {
        private val HELPER = MatchPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbt(nbt = nbt)
    }

}
