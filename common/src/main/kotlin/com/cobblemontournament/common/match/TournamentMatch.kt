package com.cobblemontournament.common.match

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.PlayerManager
import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.match.properties.MatchProperties
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.api.storage.MatchStore
import com.cobblemontournament.common.api.storage.PlayerStore
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.util.ChatUtil
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.classstored.ClassStored
import com.google.gson.JsonObject
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import org.slf4j.helpers.Util
import java.util.UUID

/** &#9888; (UUID) constructor is needed for serialization method */
open class TournamentMatch : ClassStored
{
    companion object {
        /** &#9888; Observables will be broken if [initialize] is not called after construction */
        fun loadFromNBT( nbt: CompoundTag ): TournamentMatch {
            return TournamentMatch( MatchProperties.loadFromNBT( nbt.getCompound( DataKeys.MATCH_PROPERTIES ) ) )
        }
    }

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    constructor ( uuid: UUID = UUID.randomUUID() ) : this ( MatchProperties( uuid ) )
    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    constructor ( properties: MatchProperties) {
        this.properties = properties
    }

    protected val properties: MatchProperties

    override val name get() = properties.name

    override var uuid
        get() = properties.matchID
        protected set(value) { properties.matchID = value }

    override var storeCoordinates: SettableObservable <StoreCoordinates <*,*>? > = SettableObservable( value = null )

    val tournamentID                get() = properties.tournamentID
    val roundID                     get() = properties.roundID
    val roundIndex                  get() = properties.roundIndex
    val tournamentMatchIndex        get() = properties.tournamentMatchIndex
    private val roundMatchIndex     get() = properties.roundMatchIndex
    private val matchConnections    get() = properties.connections

    /**
     * Setting victorID will:
     * 1. Update the match [properties].[MatchStatus]
     * 2. Cascade the results to all connected [TournamentMatch]
     * 3. Update all impacted [TournamentPlayer]
     */
    var victorID
        get() = properties.victorID
        private set(value) { properties.victorID = value }

    var matchStatus get() = getUpdatedMatchStatus()
        protected set(value) {
            if (properties.matchStatus != value) {
                properties.matchStatus = value
            }
        }

    /** Don't expose [MatchProperties.playerMap] publicly. Use [playerEntrySet] instead. */
    private val playerMap get() = properties.playerMap

    override fun printProperties() = getUpdatedProperties().logDebug()

    fun getConnectionsCopy() = properties.connections.deepCopy()

    private fun getUpdatedProperties(): MatchProperties {
        getUpdatedMatchStatus()
        return properties
    }

    fun playerEntrySet() = TournamentUtil.shallowCopy( playerMap )

    fun playerCount() = playerMap.size

    /**
     *  Initializes & returns a reference to itself
     *
     * &#9888; Observables will be broken if [initialize] is not called after construction
     */
    override fun initialize() : TournamentMatch {
        registerObservable( properties.anyChangeObservable )
        return this
    }

    private fun getUpdatedMatchStatus(): MatchStatus
    {
        if ( victorID != null ) {
            if (properties.matchStatus != MatchStatus.FINALIZED) {
                properties.matchStatus = MatchStatus.FINALIZED
                // nothing to do b/c should be controlled by when victorID is set
            }
        } else  if ( playerMap.isEmpty() ) {
            properties.matchStatus = MatchStatus.EMPTY
        } else if ( playerMap.size == 1 ) {
            properties.matchStatus = MatchStatus.NOT_READY
        } else {
            var team : Int? = null
            playerMap.firstNotNullOf { ( _ , t ) -> team = t }
            // TODO add check for other match pre-reqs here (like 2v2 etc)
            if ( team != null && playerMap.any { ( _ , t ) -> team != t } ) {
                properties.matchStatus = MatchStatus.READY
            } else {
                properties.matchStatus = MatchStatus.NOT_READY
            }
        }
        return properties.matchStatus
    }

    fun updateVictorID( newVictorID: UUID? )
    {
        if ( properties.victorID == newVictorID ) return

        properties.victorID = newVictorID
        if ( newVictorID == null ) {
            // TODO add resetting match after victory update already applied
            getUpdatedMatchStatus()
            return
        }

        val tournament = TournamentStoreManager.getInstance(
            storeClass  = TournamentStore::class.java,
            storeID     = TournamentStoreManager.activeStoreKey,
            instanceID  = tournamentID )
        val tournamentInsert = if ( tournament != null ) {
            " in \"${tournament.name}\""
        } else ""

        val victorTeamIndex = playerMap[victorID]
        val victorNextMatch = if ( matchConnections.victorNextMatch != null ) {
            TournamentStoreManager.getInstance(
                storeClass  = MatchStore::class.java,
                storeID     = tournamentID,
                instanceID  = matchConnections.victorNextMatch!! )
        } else null

        val defeatedNextMatch = if ( matchConnections.defeatedNextMatch != null ) {
            TournamentStoreManager.getInstance(
                storeClass  = MatchStore::class.java,
                storeID     = tournamentID,
                instanceID  = matchConnections.defeatedNextMatch!! )
        } else null

        for ( ( playerID, team ) in playerMap ) {
            val player = TournamentStoreManager.getInstance(
                storeClass  = PlayerStore::class.java,
                storeID     = tournamentID,
                instanceID  = playerID )
            if ( player == null ) {
                Util.report( "Player was null when trying to set the match VictorID." )
                continue
            }

            val serverPlayer = PlayerManager.getServerPlayer( playerID )
            if ( team == victorTeamIndex ) {
                if ( victorNextMatch == null ) {
                    player.finalPlacement = 1
                    if ( serverPlayer != null ) {
                        ChatUtil.displayInPlayerChat(
                            player  = serverPlayer,
                            text    = "Congratulations Trainer ${player.name}! You won first place$tournamentInsert!",
                            color   = ChatUtil.green )
                    } else {
                        Util.report( "Player ${player.name} won first place!" )
                    }
                    tournament?.checkComplete() ?: TODO() // log?
                }
                player.currentMatchID = victorNextMatch?.uuid
                continue
            }
            if ( defeatedNextMatch == null ) {
                if ( tournament != null ) {
                    player.finalPlacement = tournament.getFinalPlacement( player = player, finalMatch = this )
                }
                if (serverPlayer != null) {
                    ChatUtil.displayInPlayerChat(
                        player  = serverPlayer,
                        text    = "Congratulations Trainer ${player.name}! You finished in ${player.finalPlacement} place$tournamentInsert!",
                        color   = ChatUtil.green )
                } else {
                    Util.report( "Tournament was null when trying to set ${player.name}'s final placement." )
                }
            }
            player.currentMatchID = defeatedNextMatch?.uuid
        }
        getUpdatedMatchStatus()
    }

    fun containsPlayer( id: UUID) = playerMap.containsKey(id)

    fun trySetPlayer(
        playerID: UUID,
        team: Int
    ): Boolean
    {
        return if ( !playerMap.containsKey( playerID ) ) {
            playerMap[playerID] = team
            emitChange()
        } else false
    }

    fun getPlayer( playerID: UUID ) = playerMap[playerID]

    fun removePlayer(
        playerID: UUID
    ): Pair <UUID,Int>?
    {
        val teamIndex = playerMap.remove( playerID )
        return if ( teamIndex != null ) {
            getUpdatedMatchStatus()
            emitChange()
            return Pair( playerID, teamIndex )
        } else null
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        nbt.put( DataKeys.MATCH_PROPERTIES, properties.saveToNBT( CompoundTag() ) )
        return nbt
    }

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    override fun loadFromNBT(
        nbt: CompoundTag
    ): TournamentMatch {
        properties.setFromNBT( nbt.getCompound( DataKeys.MATCH_PROPERTIES ) )
        return this
    }

    override fun saveToJSON( json: JsonObject ): JsonObject { TODO("Not yet implemented") }

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    override fun loadFromJSON( json: JsonObject ): TournamentMatch { TODO("Not yet implemented") }

    private val observables = mutableListOf <Observable <*>>()
    val anyChangeObservable = SimpleObservable <TournamentMatch>()

    private fun emitChange() = anyChangeObservable.emit( values = arrayOf( this ) ) == Unit
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

}
