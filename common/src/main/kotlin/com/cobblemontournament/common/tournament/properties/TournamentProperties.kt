package com.cobblemontournament.common.tournament.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.config.TournamentConfig
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentPropertiesHelper.DEFAULT_TOURNAMENT_NAME
import com.someguy.storage.classstored.ClassStoredUtil.shallowCopy
import com.someguy.storage.properties.Properties
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class TournamentProperties(
    name: String = DEFAULT_TOURNAMENT_NAME,
    tournamentID: UUID = UUID.randomUUID(),
    tournamentStatus: TournamentStatus = TournamentPropertiesHelper.DEFAULT_TOURNAMENT_STATUS,
    tournamentType: TournamentType = TournamentConfig.defaultTournamentType(),
    challengeFormat: ChallengeFormat = TournamentConfig.defaultChallengeFormat(),
    maxParticipants: Int = TournamentConfig.defaultMaxParticipants(),
    teamSize: Int = TournamentConfig.defaultTeamSize(),
    groupSize: Int = TournamentConfig.defaultGroupSize(),
    minLevel: Int = TournamentConfig.defaultMinLevel(),
    maxLevel: Int = TournamentConfig.defaultMaxLevel(),
    showPreview: Boolean = TournamentConfig.defaultShowPreview(),
    rounds: MutableMap<UUID, TournamentRound> = mutableMapOf(),
    matches: MutableMap<UUID, TournamentMatch> = mutableMapOf(),
    players: MutableMap<UUID, TournamentPlayer> = mutableMapOf(),
) : Properties<TournamentProperties> {

    override val instance = this
    var name: String = name
        set(value) {
            field = value
            emitChange()
        }
    var tournamentID: UUID = tournamentID
        set(value) {
            field = value
            emitChange()
        }
    var tournamentStatus = tournamentStatus
        set(value) {
            field = value
            emitChange()
        }
    var tournamentType = tournamentType
        set(value) {
            field = value
            emitChange()
        }
    var challengeFormat = challengeFormat
        set(value) {
            field = value
            emitChange()
        }
    var maxParticipants = maxParticipants
        set(value) {
            field = value
            emitChange()
        }
    var teamSize = teamSize
        set(value) {
            field = value
            emitChange()
        }
    var groupSize = groupSize
        set(value) {
            field = value
            emitChange()
        }
    var minLevel = minLevel
        set(value) {
            field = value
            emitChange()
        }
    var maxLevel = maxLevel
        set(value) {
            field = value
            emitChange()
        }
    var showPreview = showPreview
        set(value) {
            field = value
            emitChange()
        }
    var rounds = rounds.shallowCopy()
        set(value) {
            field = value
            emitChange()
        }
    // No change emitter needed, b/c matches are serialized separately from tournament
    //      if the match object mutates it will be serialized by the match store,
    //      b/c the match store is subscribed to these Match objects
    var matches = matches.shallowCopy()
    // No change emitter needed, b/c players are serialized separately from tournament
    //      same as matches above
    var players = players.shallowCopy()

    override val helper = TournamentPropertiesHelper
    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<TournamentProperties>()

    constructor(uuid: UUID = UUID.randomUUID()) : this(tournamentID = uuid)

    private fun emitChange() = anyChangeObservable.emit((this))

    override fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable() = anyChangeObservable

    fun displaySlimInChat(player: ServerPlayer) {
        helper.displaySlimInChatHelper(properties = this, player = player)
    }

    fun displayResultsInChat(player: ServerPlayer) {
        helper.displayResultsInChatHelper(properties = this, player = player)
    }

    companion object {
        private val HELPER = TournamentPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNBTHelper(nbt = nbt)
    }

}
