package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.config.Config
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.tournamentbuilder.properties.TournamentBuilderPropertiesHelper.DEFAULT_TOURNAMENT_BUILDER_NAME
import com.someguy.storage.properties.Properties
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class TournamentBuilderProperties : Properties <TournamentBuilderProperties>
{
    companion object {
        val HELPER = TournamentBuilderPropertiesHelper
        fun loadFromNBT( nbt: CompoundTag ) = HELPER.loadFromNBTHelper( nbt )
    }

    constructor(
        name                    : String            = DEFAULT_TOURNAMENT_BUILDER_NAME,
        tournamentBuilderID     : UUID              = UUID.randomUUID(),
        tournamentType          : TournamentType    = Config.defaultTournamentType(),
        challengeFormat         : ChallengeFormat = Config.defaultChallengeFormat(),
        maxParticipants         : Int               = Config.defaultMaxParticipants(),
        teamSize                : Int               = Config.defaultTeamSize(),
        groupSize               : Int               = Config.defaultGroupSize(),
        minLevel                : Int               = Config.defaultMinLevel(),
        maxLevel                : Int               = Config.defaultMaxLevel(),
        showPreview             : Boolean           = Config.defaultShowPreview(),
        seededPlayers           : MutableSet<PlayerProperties>   = mutableSetOf(),
        unseededPlayers         : MutableSet<PlayerProperties>   = mutableSetOf() )
    {
        this.name                                   = name
        this.tournamentBuilderID                    = tournamentBuilderID
        this.tournamentProperties.tournamentType    = tournamentType
        this.tournamentProperties.challengeFormat   = challengeFormat
        this.tournamentProperties.maxParticipants   = maxParticipants
        this.tournamentProperties.teamSize          = teamSize
        this.tournamentProperties.groupSize         = groupSize
        this.tournamentProperties.minLevel          = minLevel
        this.tournamentProperties.maxLevel          = maxLevel
        this.tournamentProperties.showPreview       = showPreview
        this.seededPlayers.addAll( seededPlayers )
        this.unseededPlayers.addAll( unseededPlayers )

        registerObservable( tournamentProperties.getChangeObservable() )
    }

    override val instance = this
    override val helper = TournamentBuilderPropertiesHelper

    var name = DEFAULT_TOURNAMENT_BUILDER_NAME
        set( value ) { field = value; emitChange() }

    var tournamentBuilderID: UUID = UUID.randomUUID()
        set( value ) { field = value; emitChange() }

    /** This is used to simplify builder properties class by piggybacking off tournament properties:
     * - getters/setters & observables
     * - debug logging & display in chat
     * */
    val tournamentProperties = TournamentProperties()

    var seededPlayers = mutableSetOf <PlayerProperties>()
        set( value ) { field = value; emitChange() }

    var unseededPlayers = mutableSetOf <PlayerProperties>()
        set( value ) { field = value; emitChange() }

    private val observables = mutableListOf <Observable <*>>()
    private val anyChangeObservable = SimpleObservable <TournamentBuilderProperties>()

    private fun emitChange() = anyChangeObservable.emit( values = arrayOf(this) )
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    protected fun registerObservable(
        observable: Observable <*>
    ) : Observable <*>
    {
        observables.add( observable )
        observable.subscribe { anyChangeObservable.emit( values = arrayOf(this) ) }
        return observable
    }
}
