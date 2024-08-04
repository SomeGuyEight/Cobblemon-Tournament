package com.cobblemontournament.common.tournament.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.MatchMap
import com.cobblemontournament.common.api.MutableMatchMap
import com.cobblemontournament.common.api.MutablePlayerMap
import com.cobblemontournament.common.api.MutableRoundMap
import com.cobblemontournament.common.api.PlayerMap
import com.cobblemontournament.common.api.RoundMap
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.config.TournamentConfig
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.util.MATCH_MAP_HANDLER
import com.cobblemontournament.common.util.PLAYER_MAP_HANDLER
import com.cobblemontournament.common.util.ROUND_MAP_HANDLER
import com.sg8.collections.reactive.map.mutableObservableMapOf
import com.sg8.properties.DefaultProperties
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID


class TournamentProperties(
    name: String = DEFAULT_TOURNAMENT_NAME,
    uuid: UUID = UUID.randomUUID(),
    tournamentStatus: TournamentStatus = DEFAULT_TOURNAMENT_STATUS,
    tournamentType: TournamentType = TournamentConfig.defaultTournamentType(),
    challengeFormat: ChallengeFormat = TournamentConfig.defaultChallengeFormat(),
    maxParticipants: Int = TournamentConfig.defaultMaxParticipants(),
    teamSize: Int = TournamentConfig.defaultTeamSize(),
    groupSize: Int = TournamentConfig.defaultGroupSize(),
    minLevel: Int = TournamentConfig.defaultMinLevel(),
    maxLevel: Int = TournamentConfig.defaultMaxLevel(),
    showPreview: Boolean = TournamentConfig.defaultShowPreview(),
    roundMap: RoundMap? = null,
    matchMap: MatchMap? = null,
    playerMap: PlayerMap? = null,
) : DefaultProperties<TournamentProperties> {

    override val instance: TournamentProperties = this
    override val helper = TournamentPropertiesHelper
    override val observable = SimpleObservable<TournamentProperties>()
    private val subscriptionsMap: MutableMap<Observable<*>, ObservableSubscription<*>> = mutableMapOf()

    private val _name = SettableObservable(name).subscribe()
    private val _uuid = SettableObservable(uuid).subscribe()
    private val _tournamentStatus = SettableObservable(tournamentStatus).subscribe()
    private val _tournamentType = SettableObservable(tournamentType).subscribe()
    private val _challengeFormat = SettableObservable(challengeFormat).subscribe()
    private val _maxParticipants = SettableObservable(maxParticipants).subscribe()
    private val _teamSize = SettableObservable(teamSize).subscribe()
    private val _groupSize = SettableObservable(groupSize).subscribe()
    private val _minLevel = SettableObservable(minLevel).subscribe()
    private val _maxLevel = SettableObservable(maxLevel).subscribe()
    private val _showPreview = SettableObservable(showPreview).subscribe()
    private var _roundMap: MutableRoundMap
    private var _matchMap: MutableMatchMap
    private var _playerMap: MutablePlayerMap

    var name: String
        get() = _name.get()
        set(value) { _name.set(value) }
    var uuid: UUID
        get() = _uuid.get()
        set(value) { _uuid.set(value) }
    var tournamentStatus: TournamentStatus
        get() = _tournamentStatus.get()
        set(value) { _tournamentStatus.set(value) }
    var tournamentType: TournamentType
        get() = _tournamentType.get()
        set(value) { _tournamentType.set(value) }
    var challengeFormat: ChallengeFormat
        get() = _challengeFormat.get()
        set(value) { _challengeFormat.set(value) }
    var maxParticipants: Int
        get() = _maxParticipants.get()
        set(value) { _maxParticipants.set(value) }
    var teamSize: Int
        get() = _teamSize.get()
        set(value) { _teamSize.set(value) }
    var groupSize: Int
        get() = _groupSize.get()
        set(value) { _groupSize.set(value) }
    var minLevel: Int
        get() = _minLevel.get()
        set(value) { _minLevel.set(value) }
    var maxLevel: Int
        get() = _maxLevel.get()
        set(value) { _maxLevel.set(value) }
    var showPreview: Boolean
        get() = _showPreview.get()
        set(value) { _showPreview.set(value) }
    var roundMap: MutableRoundMap
        get () = _roundMap
        set(value) { _roundMap = replaceSubscription(_roundMap, value) }
    var matchMap: MutableMatchMap
        get() = _matchMap
        set(value) { _matchMap = replaceSubscription(_matchMap, value) }
    var playerMap: MutablePlayerMap
        get() = _playerMap
        set(value) { _playerMap = replaceSubscription(_playerMap, value) }


    init {
        _roundMap = roundMap?.mutableCopy() ?: mutableObservableMapOf(ROUND_MAP_HANDLER)
        _roundMap.subscribe()
        _matchMap = matchMap?.mutableCopy() ?: mutableObservableMapOf(MATCH_MAP_HANDLER)
        _playerMap = playerMap?.mutableCopy() ?: mutableObservableMapOf(PLAYER_MAP_HANDLER)
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

    fun displaySlimInChat(player: ServerPlayer) {
        helper.displaySlimInChatHelper(properties = this, player = player)
    }

    fun displayResultsInChat(player: ServerPlayer) {
        helper.displayResultsInChatHelper(properties = this, player = player)
    }

    companion object {
        private val HELPER = TournamentPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbt(nbt = nbt)
    }

}
