package com.cobblemontournament.common.tournament

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.someguy.storage.classstored.ClassStored
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.match.MatchStatus
import com.google.gson.JsonObject
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

/** &#9888; (UUID) constructor is needed for serialization method */
open class Tournament : ClassStored
{
    companion object {
        /** &#9888; Observables will be broken if [initialize] is not called after construction */
        fun loadFromNBT( nbt: CompoundTag ): Tournament {
            return Tournament( TournamentProperties.loadFromNBT( nbt.getCompound( DataKeys.TOURNAMENT_PROPERTIES ) ) )
        }
    }

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    constructor ( uuid: UUID = UUID.randomUUID() ) : this ( TournamentProperties( uuid ) )
    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    constructor ( properties: TournamentProperties ) {
        this.properties = properties
    }

    protected val properties: TournamentProperties

    override val name
        get() = properties.name

    override var uuid
        get() = properties.tournamentID
        protected set( value ) { properties.tournamentID = value }

    override var storeCoordinates: SettableObservable <StoreCoordinates <*,*>? > = SettableObservable( value = null )

    val tournamentID                get() = properties.tournamentID
    private var tournamentStatus    get() = properties.tournamentStatus
        set( value ) { properties.tournamentStatus = value }
    private val tournamentType      get() = properties.tournamentType
    val challengeFormat             get() = properties.challengeFormat
    val maxParticipants             get() = properties.maxParticipants
    val teamSize                    get() = properties.teamSize
    val groupSize                   get() = properties.groupSize
    val minLevel                    get() = properties.minLevel
    val maxLevel                    get() = properties.maxLevel
    val showPreview                 get() = properties.showPreview
    val totalRounds                 get() = properties.rounds.size
    val totalMatches                get() = properties.matches.size
    val totalPlayers                get() = properties.players.size
    private val rounds            get() = properties.rounds
    private val matches           get() = properties.matches
    protected val players           get() = properties.players

    override fun printProperties() = properties.logDebug()
    fun displayOverviewInChat(player: ServerPlayer ) = properties.displayInChat( player )
    fun displayResultsInChat( player: ServerPlayer ) {
        properties.displayResultsInChat( player = player )
    }

    fun copyProperties() = properties.deepCopy()

    fun containsPlayerID( playerID: UUID ) = players.contains(playerID)
    fun containsPlayerName( name: String ) = players.firstNotNullOfOrNull { it.value.name == name } != null
    fun getPlayerSet()      = players.values.toSet()

    /**
     *  Initializes & returns a reference to itself
     *
     * &#9888; Observables will be broken if [initialize] is not called after construction
     */
    override fun initialize(): Tournament {
        registerObservable( properties.anyChangeObservable )
        return this
    }

    fun getCurrentMatch(
        playerID: UUID
    ): TournamentMatch? {
        val player = players[playerID] ?: return null
        return if ( player.tournamentID == tournamentID && players[player.uuid] != null ) {
            matches[player.currentMatchID]
        } else null
    }

    fun checkComplete(): Boolean
    {
        return if ( tournamentStatus == TournamentStatus.FINALIZED ) {
            true
        } else when ( tournamentType ) {
            TournamentType.SINGLE_ELIMINATION -> {
                val lastRoundIndex = rounds.size - 1
                val lastRound = rounds.values.firstOrNull { it.roundIndex == lastRoundIndex }
                    ?: return false // TODO log?
                val lastMatchID = lastRound.getMatchID( roundMatchIndex = 0 )
                    ?: return false // TODO log?
                val lastMatch = matches[lastMatchID]
                    ?: return false // TODO log?
                return if ( lastMatch.matchStatus == MatchStatus.FINALIZED ) {
                    finalize()
                    return true
                } else false
            }
            else -> false
            // TODO add other tournament types
        }
    }

    private fun finalize() {
        tournamentStatus = TournamentStatus.FINALIZED
        TournamentStoreManager.transferInstance(
            storeClass  = TournamentStore::class.java,
            storeID     = TournamentStoreManager.activeStoreKey,
            newStoreID  = TournamentStoreManager.inactiveStoreKey,
            instance    = this )
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        nbt.put( DataKeys.TOURNAMENT_PROPERTIES, properties.saveToNBT( CompoundTag() ) )
        return nbt
    }

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    override fun loadFromNBT(
        nbt: CompoundTag
    ): Tournament {
        properties.setFromNBT( nbt.getCompound( DataKeys.TOURNAMENT_PROPERTIES ) )
        return this
    }

    override fun saveToJSON( json: JsonObject ): JsonObject { TODO("Not yet implemented") }

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    override fun loadFromJSON( json: JsonObject ): ClassStored { TODO("Not yet implemented") }

    private val observables = mutableListOf <Observable <*>>()
    val anyChangeObservable = SimpleObservable <Tournament>()

    fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    private fun registerObservable(
        observable: Observable <*>
    ): Observable <*>
    {
        observables.add( observable )
        observable.subscribe { anyChangeObservable.emit( values = arrayOf( this ) ) }
        return observable
    }

    fun getFinalPlacement(
        player      : TournamentPlayer,
        finalMatch  : TournamentMatch
    ): Int
    {
        // TODO add switch for other tournament types
        // this is for single elimination
        // - if the player won their last match, they should be #1 and never get here...
        if ( player.uuid == finalMatch.victorID ) { return 1 }
        return rounds[finalMatch.roundID]?.matchMapSize?.plus( other = 1 ) ?: -69420 // lolz
    }

}
