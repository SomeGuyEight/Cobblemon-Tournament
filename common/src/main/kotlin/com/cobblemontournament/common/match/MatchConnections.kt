package com.cobblemontournament.common.match

import com.cobblemon.mod.common.api.reactive.*
import com.cobblemontournament.common.api.storage.*
import com.sg8.util.getUuidOrNull
import com.sg8.util.putIfNotNull
import com.sg8.collections.reactive.map.*
import com.sg8.properties.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID
import kotlin.collections.Map.Entry

typealias PreviousMatchesMap = ObservableMap<Int, UUID>
typealias MutablePreviousMatchesMap = MutableObservableMap<Int, UUID>

class MatchConnections(properties: MatchConnectionsProperties? = null) {

    val observable = SimpleObservable<MatchConnections>()

    private val properties = properties?.deepCopy() ?: MatchConnectionsProperties()

    var victorNextMatch: UUID?
        get() = properties.victorNextMatch
        set(value) { properties.victorNextMatch = value }
    var defeatedNextMatch: UUID?
        get() = properties.defeatedNextMatch
        set(value) { properties.defeatedNextMatch = value }
    val previousMatchesMap: PreviousMatchesMap get() = properties.previousMatchMap

    init {
        registerObservable(this.properties.observable)
    }

    private fun <T, O : Observable<T>> registerObservable(observable: O): O {
        observable.subscribe { emitChange() }
        return observable
    }

    private fun emitChange() = observable.emit(this)

    fun getObservable(): Observable<MatchConnections> = observable

    fun addPreviousMatch(matchIndex: Int?, matchID: UUID?): Boolean {
        if (matchIndex != null && matchID != null) {
            properties.previousMatchMap[matchIndex] = matchID
            return true
        }
        return false
    }

    fun setFromNbt(nbt: CompoundTag): MatchConnections {
        properties.setFromNbt(nbt)
        return this
    }

    fun saveToNbt(nbt: CompoundTag) = properties.saveToNbt(nbt = nbt)

    fun deepCopy() = MatchConnections(properties)

    companion object {
        fun loadFromNbt(nbt: CompoundTag) = MatchConnections(
            MatchConnectionsProperties.loadFromNbt(
                nbt.getCompound(MATCH_CONNECTIONS_PROPERTIES_KEY)
            )
        )
    }

}

class MatchConnectionsProperties(
    victorNextMatch: UUID? = null,
    defeatedNextMatch: UUID? = null,
    previousMatchMap: MutablePreviousMatchesMap? = null,
) : DefaultProperties<MatchConnectionsProperties> {

    override val instance = this
    override val helper = MatchConnectionsHelper
    override val observable = SimpleObservable<MatchConnectionsProperties>()

    private val _victorNextMatch = SettableObservable(victorNextMatch).subscribe()
    private val _defeatedNextMatch = SettableObservable(defeatedNextMatch).subscribe()
    private val _previousMatchMap = previousMatchMap?.mutableCopy() ?: observableMapOf()

    var victorNextMatch: UUID?
        get() = _victorNextMatch.get()
        set(value) { _victorNextMatch.set(value) }
    var defeatedNextMatch: UUID?
        get() = _defeatedNextMatch.get()
        set(value) { _defeatedNextMatch.set(value) }
    val previousMatchMap get() = _previousMatchMap

    init {
        _previousMatchMap.subscribe()
    }

    private fun <T, O : Observable<T>> O.subscribe(): O {
        this.subscribe { emitChange() }
        return this
    }

    override fun emitChange() = observable.emit(this)

    companion object {
        private val HELPER = MatchConnectionsHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbt(nbt)
    }

}

object MatchConnectionsHelper : PropertiesHelper<MatchConnectionsProperties> {

    override fun saveToNbt(
        properties: MatchConnectionsProperties,
        nbt: CompoundTag,
    ): CompoundTag {
        nbt.putIfNotNull(VICTOR_NEXT_MATCH_KEY, properties.victorNextMatch)
        nbt.putIfNotNull(DEFEATED_NEXT_MATCH_KEY, properties.defeatedNextMatch)
        nbt.putPreviousMatchMap(properties)
        return nbt
    }

    override fun loadFromNbt(nbt: CompoundTag): MatchConnectionsProperties {
        return MatchConnectionsProperties(
            victorNextMatch = nbt.getUuidOrNull(VICTOR_NEXT_MATCH_KEY),
            defeatedNextMatch = nbt.getUuidOrNull(DEFEATED_NEXT_MATCH_KEY),
            previousMatchMap = nbt.getPreviousMatchMap(),
        )
    }

    override fun setFromNbt(
        mutable: MatchConnectionsProperties,
        nbt: CompoundTag,
    ): MatchConnectionsProperties {
        mutable.victorNextMatch = nbt.getUuidOrNull(VICTOR_NEXT_MATCH_KEY)
        mutable.defeatedNextMatch = nbt.getUuidOrNull(DEFEATED_NEXT_MATCH_KEY)
        mutable.previousMatchMap.clear()
        mutable.previousMatchMap.putAll(nbt.getPreviousMatchMap())
        return mutable
    }

    private fun CompoundTag.putPreviousMatchMap(properties: MatchConnectionsProperties) {
        val entryHandler = { (index, matchID): Entry<Int, UUID> ->
            CompoundTag().also { nbt ->
                nbt.putInt(TEAM_INDEX_KEY, index)
                nbt.putUUID(MATCH_ID_KEY, matchID)
            }
        }
        val mapNbt = properties.previousMatchMap.saveToNbt(entryHandler)
        this.put(PREVIOUS_MATCH_MAP_KEY, mapNbt)
    }

    private fun CompoundTag.getPreviousMatchMap(): MutablePreviousMatchesMap {
        val mapNbt = this.getCompound(PREVIOUS_MATCH_MAP_KEY)
        val entryHandler = { nbt: CompoundTag ->
            nbt.getInt(TEAM_INDEX_KEY) to nbt.getUUID(MATCH_ID_KEY)
        }
        return mapNbt.loadObservableMapOf(entryHandler)
    }

    override fun deepCopy(properties: MatchConnectionsProperties) = copy(properties)

    override fun copy(properties: MatchConnectionsProperties): MatchConnectionsProperties {
        return MatchConnectionsProperties(
            victorNextMatch = properties.victorNextMatch,
            defeatedNextMatch = properties.defeatedNextMatch,
            previousMatchMap = properties.previousMatchMap,
        )
    }

    override fun printDebug(properties: MatchConnectionsProperties) {
        TODO("Not yet implemented")
    }

    override fun displayInChat(properties: MatchConnectionsProperties, player: ServerPlayer) {
        TODO("Not yet implemented")
    }

}
