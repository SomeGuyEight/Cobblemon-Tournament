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
import com.someguy.storage.properties.Properties
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MatchProperties : Properties <MatchProperties>
{
    companion object {
        private val HELPER = MatchPropertiesHelper
        fun loadFromNBT( nbt: CompoundTag ) = HELPER.loadFromNBTHelper( nbt )
    }

    constructor( uuid: UUID = UUID.randomUUID() ) : this (
        matchID                 = uuid,
        tournamentID            = UUID.randomUUID(),
        roundID                 = UUID.randomUUID(),
        roundIndex              = DEFAULT_ROUND_INDEX,
        tournamentMatchIndex    = DEFAULT_TOURNAMENT_MATCH_INDEX,
        roundMatchIndex         = DEFAULT_ROUND_MATCH_INDEX )

    constructor(
        matchID                 : UUID,
        tournamentID            : UUID,
        roundID                 : UUID,
        roundIndex              : Int,
        tournamentMatchIndex    : Int,
        roundMatchIndex         : Int,
        connections             : MatchConnections      = MatchConnections(),
        matchStatus             : MatchStatus           = DEFAULT_MATCH_STATUS,
        victorID                : UUID?                 = DEFAULT_VICTOR_ID,
        playerMap               : MutableMap<UUID,Int>  = mutableMapOf())
    {
        this.matchID                = matchID
        this.tournamentID           = tournamentID
        this.roundID                = roundID
        this.roundIndex             = roundIndex
        this.tournamentMatchIndex   = tournamentMatchIndex
        this.roundMatchIndex        = roundMatchIndex
        this.matchStatus            = matchStatus
        this.victorID               = victorID
        this.playerMap.putAll( playerMap )
        this.connections.setFromConnections( connections )
        registerObservable( connections.getChangeObservable() )
    }

    override val instance = this
    override val helper = MatchPropertiesHelper

    val name get() = "Match $roundMatchIndex ($tournamentMatchIndex)"

    var matchID: UUID = UUID.randomUUID()
        set( value ) { field = value; emitChange() }

    var tournamentID: UUID = UUID.randomUUID()
        set( value ) { field = value; emitChange() }

    var roundID: UUID = UUID.randomUUID()
        set( value ) { field = value; emitChange() }

    var roundIndex = DEFAULT_ROUND_INDEX
        set( value ) { field = value; emitChange() }

    var tournamentMatchIndex = DEFAULT_TOURNAMENT_MATCH_INDEX
        set( value ) { field = value; emitChange() }

    var roundMatchIndex = DEFAULT_ROUND_MATCH_INDEX
        set( value ) { field = value; emitChange() }

    /** exposed publicly as val, so observable can be set once & updated by the connections observable */
    val connections = MatchConnections()

    var matchStatus = DEFAULT_MATCH_STATUS
        set( value ) { field = value; emitChange() }

    var victorID: UUID? = DEFAULT_VICTOR_ID
        set( value ) { field = value; emitChange() }

    var playerMap = mutableMapOf <UUID,Int>()
        set( value ) { field = value; emitChange() }

    private val observables = mutableListOf <Observable <*>>()
    val anyChangeObservable = SimpleObservable <MatchProperties>()

    private fun emitChange() = anyChangeObservable.emit( values = arrayOf( this ) )
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    private fun registerObservable(
        observable: Observable <*>
    ) : Observable <*>
    {
        observables.add( observable )
        observable.subscribe { anyChangeObservable.emit( values = arrayOf( this ) ) }
        return observable
    }

}
