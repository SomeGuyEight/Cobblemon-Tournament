package com.cobblemontournament.common.tournament.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.config.TournamentConfig
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.util.*
import com.someguy.storage.Properties
import com.someguy.storage.util.SubscriptionMap
import com.someguy.storage.util.registerObservable
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class TournamentProperties(
    name: String = DEFAULT_TOURNAMENT_NAME,
    tournamentID: TournamentID = UUID.randomUUID(),
    tournamentStatus: TournamentStatus = DEFAULT_TOURNAMENT_STATUS,
    tournamentType: TournamentType = TournamentConfig.defaultTournamentType(),
    challengeFormat: ChallengeFormat = TournamentConfig.defaultChallengeFormat(),
    maxParticipants: Int = TournamentConfig.defaultMaxParticipants(),
    teamSize: Int = TournamentConfig.defaultTeamSize(),
    groupSize: Int = TournamentConfig.defaultGroupSize(),
    minLevel: Int = TournamentConfig.defaultMinLevel(),
    maxLevel: Int = TournamentConfig.defaultMaxLevel(),
    showPreview: Boolean = TournamentConfig.defaultShowPreview(),
    rounds: RoundMap = mutableMapOf(),
    matches: MatchMap = mutableMapOf(),
    players: PlayerMap = mutableMapOf(),
) : Properties<TournamentProperties> {

    override val instance: TournamentProperties = this
    override val helper = TournamentPropertiesHelper

    // TODO handle with observable
    var rounds: RoundMap = rounds.toMutableMap()
    var matches: MatchMap = matches.toMutableMap()
    var players: PlayerMap = players.toMutableMap()

    private val anyChangeObservable = SimpleObservable<TournamentProperties>()
    private val subscriptionsMap: SubscriptionMap = mutableMapOf()

    private val nameObservable = registerObservable(SettableObservable(name))
    private val tournamentIDObservable = registerObservable(SettableObservable(tournamentID))
    private val tournamentStatusObservable =
        registerObservable(SettableObservable(tournamentStatus))
    private val tournamentTypeObservable = registerObservable(SettableObservable(tournamentType))
    private val challengeFormatObservable = registerObservable(SettableObservable(challengeFormat))
    private val maxParticipantsObservable = registerObservable(SettableObservable(maxParticipants))
    private val teamSizeObservable = registerObservable(SettableObservable(teamSize))
    private val groupSizeObservable = registerObservable(SettableObservable(groupSize))
    private val minLevelObservable = registerObservable(SettableObservable(minLevel))
    private val maxLevelObservable = registerObservable(SettableObservable(maxLevel))
    private val showPreviewObservable = registerObservable(SettableObservable(showPreview))

    var name: String
        get() = nameObservable.get()
        set(value) { nameObservable.set(value) }
    var tournamentID: TournamentID
        get() = tournamentIDObservable.get()
        set(value) { tournamentIDObservable.set(value) }
    var tournamentStatus: TournamentStatus
        get() = tournamentStatusObservable.get()
        set(value) { tournamentStatusObservable.set(value) }
    var tournamentType: TournamentType
        get() = tournamentTypeObservable.get()
        set(value) { tournamentTypeObservable.set(value) }
    var challengeFormat: ChallengeFormat
        get() = challengeFormatObservable.get()
        set(value) { challengeFormatObservable.set(value) }
    var maxParticipants: Int
        get() = maxParticipantsObservable.get()
        set(value) { maxParticipantsObservable.set(value) }
    var teamSize: Int
        get() = teamSizeObservable.get()
        set(value) { teamSizeObservable.set(value) }
    var groupSize: Int
        get() = groupSizeObservable.get()
        set(value) { groupSizeObservable.set(value) }
    var minLevel: Int
        get() = minLevelObservable.get()
        set(value) { minLevelObservable.set(value) }
    var maxLevel: Int
        get() = maxLevelObservable.get()
        set(value) { maxLevelObservable.set(value) }
    var showPreview: Boolean
        get() = showPreviewObservable.get()
        set(value) { showPreviewObservable.set(value) }

    init {
        for (round in rounds.values) {
            round.getChangeObservable().registerObservable(subscriptionsMap) { emitChange() }
        }
    }

    private fun <T, O : Observable<T>> registerObservable(observable: O): O {
        return observable.registerObservable(subscriptionsMap) { emitChange() }
    }

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getChangeObservable() = anyChangeObservable

    fun displaySlimInChat(player: ServerPlayer) {
        helper.displaySlimInChatHelper(properties = this, player = player)
    }

    fun displayResultsInChat(player: ServerPlayer) {
        helper.displayResultsInChatHelper(properties = this, player = player)
    }

    companion object {
        private val HELPER = TournamentPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbtHelper(nbt = nbt)
    }

}
