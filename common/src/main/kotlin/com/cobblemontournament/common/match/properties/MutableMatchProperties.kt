package com.cobblemontournament.common.match.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_MATCH_STATUS
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_ROUND_MATCH_INDEX
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_TOURNAMENT_MATCH_INDEX
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_VICTOR_ID
import com.someguy.storage.properties.MutableProperties
import com.someguy.storage.properties.PropertiesCompanion
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MutableMatchProperties: MatchPropertyFields, MutableProperties<MatchPropertyFields,MatchProperties,MutableMatchProperties>
{
    companion object: PropertiesCompanion <MatchPropertyFields, MatchProperties, MutableMatchProperties> {
        override val helper = MatchPropertiesHelper
    }

    constructor(
        matchID                 : UUID,
        tournamentID            : UUID,
        roundID                 : UUID,
        tournamentMatchIndex    : Int,
        roundMatchIndex         : Int,
        matchStatus             : MatchStatus = DEFAULT_MATCH_STATUS,
        victorID                : UUID?                 = DEFAULT_VICTOR_ID,
        playerMap               : MutableMap<UUID,Int>  = mutableMapOf(),
    )
    {
        this.matchID                = matchID
        this.tournamentID           = tournamentID
        this.roundID                = roundID
        this.tournamentMatchIndex   = tournamentMatchIndex
        this.roundMatchIndex        = roundMatchIndex
        this.matchStatus            = matchStatus
        this.victorID               = victorID
        this.playerMap.putAll( playerMap)
    }

    constructor() : this(
        matchID                 = UUID.randomUUID(),
        tournamentID            = UUID.randomUUID(),
        roundID                 = UUID.randomUUID(),
        tournamentMatchIndex    = DEFAULT_TOURNAMENT_MATCH_INDEX,
        roundMatchIndex         = DEFAULT_ROUND_MATCH_INDEX
    )

    override var matchID        : UUID = UUID.randomUUID()
        set(value) { field = value; emitChange() }

    override var tournamentID   : UUID = UUID.randomUUID()
        set(value) { field = value; emitChange() }

    override var roundID        : UUID = UUID.randomUUID()
        set(value) { field = value; emitChange() }

    override var tournamentMatchIndex = DEFAULT_TOURNAMENT_MATCH_INDEX
        set(value) { field = value; emitChange() }

    override var roundMatchIndex = DEFAULT_ROUND_MATCH_INDEX
        set(value) { field = value; emitChange() }

    override var matchStatus = DEFAULT_MATCH_STATUS
        set(value) { field = value; emitChange() }

    override var victorID: UUID? = DEFAULT_VICTOR_ID
        set(value) { field = value; emitChange() }

    override var playerMap = mutableMapOf<UUID,Int>()
        set(value) { field = value; emitChange() }

    override fun getHelper() = helper

    override fun deepCopy() = helper.deepCopyHelper( properties = this)

    override fun deepMutableCopy() = helper.deepMutableCopyHelper( properties = this)

    override fun setFromNBT(
        nbt: CompoundTag
    ): MutableMatchProperties {
        return helper.setFromNBTHelper( mutable = this, nbt = nbt)
    }

    override fun setFromProperties(
        from: MatchPropertyFields
    ): MutableMatchProperties {
        return helper.setFromPropertiesHelper(mutable = this, from = from)
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        return helper.saveToNBTHelper( properties = this, nbt = nbt)
    }

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<MutableMatchProperties>()

    private fun emitChange() = anyChangeObservable.emit(this)
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable(): Observable<MutableMatchProperties> = anyChangeObservable

    private fun <T> registerObservable(
        observable: SimpleObservable<T>
    ): SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }
}
