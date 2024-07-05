package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.player.properties.MutablePlayerProperties
import com.cobblemontournament.common.tournamentbuilder.properties.TournamentBuilderPropertiesHelper.DEFAULT_TOURNAMENT_BUILDER_NAME
import com.cobblemontournament.common.tournament.properties.MutableTournamentProperties
import com.someguy.storage.properties.Properties
import com.someguy.storage.properties.PropertiesCompanion
import net.minecraft.nbt.CompoundTag
import java.util.UUID

data class TournamentBuilderProperties (
    override val name                    : String,
    override var tournamentBuilderID     : UUID,
    override val tournamentProperties    : MutableTournamentProperties           = MutableTournamentProperties(),
    override val seededPlayers           : MutableSet<MutablePlayerProperties>   = mutableSetOf(),
    override val unseededPlayers         : MutableSet<MutablePlayerProperties>   = mutableSetOf(),
) : Properties<TournamentBuilderPropertyFields, TournamentBuilderProperties, MutableTournamentBuilderProperties>,
    TournamentBuilderPropertyFields

{
    companion object: PropertiesCompanion<TournamentBuilderPropertyFields, TournamentBuilderProperties, MutableTournamentBuilderProperties> {
        override val helper = TournamentBuilderPropertiesHelper
    }

    constructor() : this (
        name                    = DEFAULT_TOURNAMENT_BUILDER_NAME,
        tournamentBuilderID     = UUID.randomUUID()
    )

    override fun getHelper() = TournamentBuilderPropertiesHelper

    override fun deepCopy() = TournamentBuilderPropertiesHelper.deepCopyHelper(properties = this)

    override fun deepMutableCopy() = TournamentBuilderPropertiesHelper.deepMutableCopyHelper(properties = this)

    override fun saveToNBT(nbt: CompoundTag) = TournamentBuilderPropertiesHelper.saveToNBTHelper(properties = this, nbt)

    // tournament builder properties are immutable so empty & a placeholder is fine
    override fun getAllObservables(): Iterable<Observable<*>> = emptyList()
    override fun getChangeObservable(): Observable<TournamentBuilderProperties> = SimpleObservable()

}
