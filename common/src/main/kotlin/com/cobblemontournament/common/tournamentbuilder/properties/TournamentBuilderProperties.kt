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
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class TournamentBuilderProperties : Properties <TournamentBuilderProperties>
{
    companion object {
        val HELPER = TournamentBuilderPropertiesHelper
        fun loadFromNBT( nbt: CompoundTag ) = HELPER.loadFromNBTHelper( nbt )
    }

    constructor( uuid: UUID = UUID.randomUUID() ) : this ( tournamentBuilderID = uuid )

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
        players                 : MutableSet<PlayerProperties>   = mutableSetOf() )
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
        this.players.addAll( players )

        for ( player in this.players ) {
            registerObservable( player.getChangeObservable() )
        }
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

    protected var players = mutableSetOf <PlayerProperties>()
        set( value ) { field = value; emitChange() }

    fun getPlayersDeepCopy(): MutableSet <PlayerProperties> {
        val playersCopy = mutableSetOf <PlayerProperties>()
        for ( player in this.players ) {
            playersCopy.add( player.deepCopy() )
        }
        return playersCopy
    }

    fun containsPlayerID( playerID: UUID ) = players.firstOrNull { it.playerID == playerID } != null
    fun containsPlayerName( name: String ) = players.firstOrNull { it.name == name } != null

    fun getPlayersSize()        = players.size
    /** Returns an iterator over all elements of [TournamentBuilderProperties.players] */
    fun getPlayersIterator()    = players.iterator()
    /** Returns a list containing all player properties of [TournamentBuilderProperties.players] */
    fun getPlayers()            = players.toList()
    /** Returns a list containing all elements of [TournamentBuilderProperties.players] with a seed > 0 */
    fun getSeededPlayers()      = players.filter { it.seed > 0 }.toList()
    /** Returns a list containing all elements of [TournamentBuilderProperties.players] with a seed < 1 */
    fun getUnseededPlayers()    = players.filter { it.seed < 1 }.toList()

    fun getPlayer( playerID: UUID ) = players.firstOrNull { it.playerID == playerID }
    fun getPlayer( name: String )   = players.firstOrNull { it.name == name }

    fun addPlayer(
        playerProps: PlayerProperties
    ): Boolean {
        return if ( players.add( playerProps ) ) {
            registerObservable( playerProps.getChangeObservable() )
            emitChange()
        } else false
    }

    fun removePlayer(
        playerProps: PlayerProperties
    ): Boolean {
        return if ( players.remove( playerProps ) ) {
            emitChange()
        } else false
    }

    fun removePlayer(
        playerID: UUID
    ): Boolean {
        return if ( players.removeIf { it.playerID == playerID } ) {
            emitChange()
        } else false
    }

    fun removePlayer(
        name: String
    ): Boolean {
        return if ( players.removeIf { it.name == name } ) {
            emitChange()
        } else false
    }

    fun <T : Comparable<T>> getPlayersSortedBy(
        predicate   : (PlayerProperties) -> Boolean = { _ -> true },
        selector    : (PlayerProperties) -> T
    ): List <PlayerProperties>
    {
        val list = mutableListOf <PlayerProperties>()
        for ( playerProps in players ) {
            if ( predicate( playerProps ) ) {
                list.add( playerProps )
            }
        }
        return list.sortedBy { selector( it ) }
    }

    private val observables = mutableListOf <Observable <*>>()
    private val anyChangeObservable = SimpleObservable <TournamentBuilderProperties>()

    private fun emitChange() = anyChangeObservable.emit( values = arrayOf(this) ) == Unit
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    fun getPlayerChangeObservables(): Set <Observable <PlayerProperties>> {
        val observables = mutableSetOf<Observable <PlayerProperties>>()
        for (playerProps in players) {
            observables.add( playerProps.getChangeObservable() )
        }
        return observables
    }

    protected fun registerObservable(
        observable: Observable <*>
    ) : Observable <*>
    {
        observables.add( observable )
        observable.subscribe { anyChangeObservable.emit( values = arrayOf( this ) ) }
        return observable
    }

    fun displayInChatSlim( player: ServerPlayer ) {
        helper.displayInChatSlimHelper( properties = this, player = player )
    }

}
