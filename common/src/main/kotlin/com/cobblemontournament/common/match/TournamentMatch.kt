package com.cobblemontournament.common.match

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.match.properties.MatchProperties
import com.cobblemontournament.common.match.properties.MutableMatchProperties
import com.cobblemontournament.common.util.TournamentDataKeys
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.classstored.extension.ClassStoredExtension.defaultStoreCoords
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.util.UUID

// Important: (UUID) constructor is needed for serialization method
open class TournamentMatch(uuid: UUID) : ClassStored
{
    constructor() : this (UUID.randomUUID())

    constructor (
        properties: MatchProperties
    ) : this (properties.matchID) {
        this.properties.setFromProperties(properties)
    }

    constructor (
        mutableProperties: MutableMatchProperties
    ) : this (mutableProperties.matchID) {
        this.properties = mutableProperties
    }

    protected var properties = MutableMatchProperties()

    override val name
        get() = "Match $tournamentMatchIndex [${matchStatus.name} status]"

    override var uuid
        get() = properties.matchID
        protected set(value) { properties.matchID = value }

    override var storeCoordinates = defaultStoreCoords()

    val tournamentID            get() = properties.tournamentID
    val roundID                 get() = properties.roundID
    val tournamentMatchIndex    get() = properties.tournamentMatchIndex
    val roundMatchIndex         get() = properties.roundMatchIndex

    var victorID                get() = properties.victorID
        protected set(value) {
            if (properties.victorID != value) {
                properties.victorID = value
            }
        }

    var matchStatus get() = getUpdatedStatus()
        protected set(value) {
            if (properties.matchStatus != value) {
                properties.matchStatus = value
            }
        }

    /**
     * Don't expose [MutableMatchProperties.playerMap] publicly.
     * Use [playerEntrySet] instead.
     */
    protected val playerMap get() = properties.playerMap

    override fun printProperties() = getUpdatedProperties().printProperties()

    fun getProperties() = getUpdatedProperties().deepCopy()

    private fun getUpdatedProperties(): MutableMatchProperties {
        getUpdatedStatus()
        return properties
    }

    fun playerEntrySet() = playerMap.entries.toSet()

    fun playerCount() = playerMap.size

    override fun initialize() : TournamentMatch {
        registerObservable(properties.anyChangeObservable)
        return this
    }

    private fun getUpdatedStatus() : MatchStatus
    {
        if (playerMap.isEmpty()) {
            properties.matchStatus = MatchStatus.EMPTY
        } else if (playerMap.size == 1) {
            properties.matchStatus = MatchStatus.NOT_READY
        } else {
            var team : Int? = null
            playerMap.firstNotNullOf { (_,t) -> team = t }
            // TODO add check for other match pre-reqs here (like 2v2 etc)
            if (team != null && playerMap.any { (_,t) -> team != t }) {
                properties.matchStatus = MatchStatus.READY
            } else {
                properties.matchStatus = MatchStatus.NOT_READY
            }
        }
        return properties.matchStatus
    }

    fun trySetPlayer(
        playerID: UUID,
        team: Int
    ): Boolean
    {
        return if (!playerMap.containsKey( playerID)) {
            playerMap[playerID] = team
            true
        } else false
    }

    fun updatePlayer(
        playerID: UUID,
        team: Int
    ): Boolean
    {
        return if (playerMap.containsKey( playerID) && playerMap[playerID] != team) {
            val original = playerMap.remove( playerID)
            playerMap[playerID] = team
            original != null
        } else false
    }

    fun removePlayer(
        playerID: UUID
    ): Pair<UUID,Int>?
    {
        val teamIndex = playerMap.remove( playerID)
        return if (teamIndex != null) {
            getUpdatedStatus()
            return Pair( playerID, teamIndex)
        } else null
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        nbt.put(TournamentDataKeys.MATCH_PROPERTIES,properties.saveToNBT(CompoundTag()))
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ): TournamentMatch {
        properties.setFromNBT( nbt.getCompound( TournamentDataKeys.MATCH_PROPERTIES))
        return this
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not yet implemented") }

    override fun loadFromJSON(json: JsonObject): ClassStored { TODO("Not yet implemented") }

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<TournamentMatch>()

    fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable(): Observable<TournamentMatch> = anyChangeObservable

    protected fun <T> registerObservable(
        observable: SimpleObservable<T>
    ) : SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

}
