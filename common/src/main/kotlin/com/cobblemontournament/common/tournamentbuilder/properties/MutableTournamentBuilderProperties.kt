package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.player.properties.MutablePlayerProperties
import com.cobblemontournament.common.tournamentbuilder.properties.TournamentBuilderPropertiesHelper.DEFAULT_TOURNAMENT_BUILDER_NAME
import com.cobblemontournament.common.tournament.properties.MutableTournamentProperties
import com.someguy.storage.properties.MutableProperties
import com.someguy.storage.properties.PropertiesCompanion
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MutableTournamentBuilderProperties: TournamentBuilderPropertyFields, MutableProperties <TournamentBuilderPropertyFields, TournamentBuilderProperties, MutableTournamentBuilderProperties>
{
    companion object: PropertiesCompanion <TournamentBuilderPropertyFields, TournamentBuilderProperties, MutableTournamentBuilderProperties> {
        override val helper = TournamentBuilderPropertiesHelper
    }

    constructor(
        name                    : String                        = DEFAULT_TOURNAMENT_BUILDER_NAME,
        tournamentBuilderID     : UUID                          = UUID.randomUUID(),
        tournamentProperties    : MutableTournamentProperties   = MutableTournamentProperties(),
        seededPlayers           : MutableSet<MutablePlayerProperties>   = mutableSetOf(),
        unseededPlayers         : MutableSet<MutablePlayerProperties>   = mutableSetOf(),
    )
    {
        this.name                   = name
        this.tournamentBuilderID    = tournamentBuilderID
        this.tournamentProperties   = tournamentProperties
        this.seededPlayers.addAll( seededPlayers)
        this.unseededPlayers.addAll( unseededPlayers)
    }

    constructor() : this(DEFAULT_TOURNAMENT_BUILDER_NAME)

    override var name = DEFAULT_TOURNAMENT_BUILDER_NAME
        set(value) { field = value; emitChange() }

    override var tournamentBuilderID: UUID = UUID.randomUUID()
        set(value) { field = value; emitChange() }

    override var tournamentProperties = MutableTournamentProperties()
        set(value) { field = value; emitChange() }

    override var seededPlayers = mutableSetOf<MutablePlayerProperties>()
        set(value) { field = value; emitChange() }

    override var unseededPlayers  = mutableSetOf<MutablePlayerProperties>()
        set(value) { field = value; emitChange() }

    override fun getHelper() = helper

    override fun deepCopy() = helper.deepCopyHelper( properties = this)

    override fun deepMutableCopy() = helper.deepMutableCopyHelper( properties = this)

    override fun setFromNBT(
        nbt: CompoundTag
    ): MutableTournamentBuilderProperties {
        return helper.setFromNBTHelper( mutable = this, nbt = nbt)
    }

    override fun setFromProperties(
        from: TournamentBuilderPropertyFields
    ): MutableTournamentBuilderProperties {
        return helper.setFromPropertiesHelper(mutable = this, from = from)
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        return helper.saveToNBTHelper( properties = this, nbt = nbt)
    }

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<MutableTournamentBuilderProperties>()

    private fun emitChange() = anyChangeObservable.emit(this)
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable(): Observable<MutableTournamentBuilderProperties> = anyChangeObservable

    private fun <T> registerObservable(
        observable: SimpleObservable<T>
    ): SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

}
