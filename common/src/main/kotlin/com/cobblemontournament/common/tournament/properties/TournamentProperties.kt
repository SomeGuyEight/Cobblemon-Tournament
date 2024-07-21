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

class TournamentProperties : Properties <TournamentProperties>
{
    companion object {
        private val HELPER = TournamentPropertiesHelper
        fun loadFromNBT( nbt: CompoundTag ) = HELPER.loadFromNBTHelper( nbt )
    }

    constructor( uuid: UUID = UUID.randomUUID() ) : this ( tournamentID = uuid )

    constructor(
        name            : String            = DEFAULT_TOURNAMENT_NAME,
        tournamentID    : UUID              = UUID.randomUUID(),
        tournamentStatus: TournamentStatus  = TournamentPropertiesHelper.DEFAULT_TOURNAMENT_STATUS,
        tournamentType  : TournamentType    = TournamentConfig.defaultTournamentType(),
        challengeFormat : ChallengeFormat   = TournamentConfig.defaultChallengeFormat(),
        maxParticipants : Int               = TournamentConfig.defaultMaxParticipants(),
        teamSize        : Int               = TournamentConfig.defaultTeamSize(),
        groupSize       : Int               = TournamentConfig.defaultGroupSize(),
        minLevel        : Int               = TournamentConfig.defaultMinLevel(),
        maxLevel        : Int               = TournamentConfig.defaultMaxLevel(),
        showPreview     : Boolean           = TournamentConfig.defaultShowPreview(),
        rounds          : MutableMap <UUID,TournamentRound>  = mutableMapOf(),
        matches         : MutableMap <UUID,TournamentMatch>  = mutableMapOf(),
        players         : MutableMap <UUID,TournamentPlayer> = mutableMapOf(),
    ) : super ()
    {
        this.name               = name
        this.tournamentID       = tournamentID
        this.tournamentStatus   = tournamentStatus
        this.tournamentType     = tournamentType
        this.challengeFormat    = challengeFormat
        this.maxParticipants    = maxParticipants
        this.teamSize           = teamSize
        this.groupSize          = groupSize
        this.minLevel           = minLevel
        this.maxLevel           = maxLevel
        this.showPreview        = showPreview
        this.rounds             = rounds.shallowCopy()
        this.matches            = matches.shallowCopy()
        this.players            = players.shallowCopy()
    }

    override val instance = this
    override val helper = TournamentPropertiesHelper

    var name = DEFAULT_TOURNAMENT_NAME
        set( value ) { field = value; emitChange() }

    var tournamentID: UUID = UUID.randomUUID()
        set( value ) { field = value; emitChange() }

    var tournamentStatus = TournamentPropertiesHelper.DEFAULT_TOURNAMENT_STATUS
        set( value ) { field = value; emitChange() }

    var tournamentType = TournamentConfig.defaultTournamentType()
        set( value ) { field = value; emitChange() }

    var challengeFormat = TournamentConfig.defaultChallengeFormat()
        set( value ) { field = value; emitChange() }

    var maxParticipants = TournamentConfig.defaultMaxParticipants()
        set( value ) { field = value; emitChange() }

    var teamSize = TournamentConfig.defaultTeamSize()
        set( value ) { field = value; emitChange() }

    var groupSize = TournamentConfig.defaultGroupSize()
        set( value ) { field = value; emitChange() }

    var minLevel = TournamentConfig.defaultMinLevel()
        set( value ) { field = value; emitChange() }

    var maxLevel = TournamentConfig.defaultMaxLevel()
        set( value ) { field = value; emitChange() }

    var showPreview = TournamentConfig.defaultShowPreview()
        set( value ) { field = value; emitChange() }

    var rounds = mutableMapOf <UUID,TournamentRound>()
        set( value ) { field = value; emitChange() }

    // No change emitter needed, b/c matches are serialized separately from tournament
    //      if the match object mutates it will be serialized by the match store,
    //      b/c the match store is subscribed to these Match objects
    var matches = mutableMapOf <UUID,TournamentMatch>()

    // No change emitter needed, b/c players are serialized separately from tournament
    //      same as matches above
    var players = mutableMapOf <UUID,TournamentPlayer>()


    private val observables = mutableListOf <Observable <*>>()
    val anyChangeObservable = SimpleObservable <TournamentProperties>()

    private fun emitChange() = anyChangeObservable.emit( values = arrayOf( this ) )
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    fun displaySlimInChat( player: ServerPlayer ) {
        helper.displaySlimInChatHelper( properties = this, player = player )
    }

    fun displayResultsInChat( player: ServerPlayer ) {
        helper.displayResultsInChatHelper( properties = this, player = player )
    }

}
