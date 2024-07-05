package com.cobblemontournament.common.round.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.round.properties.RoundPropertiesHelper.DEFAULT_ROUND_INDEX
import com.cobblemontournament.common.round.properties.RoundPropertiesHelper.DEFAULT_ROUND_TYPE
import com.someguy.storage.properties.Properties
import com.someguy.storage.properties.PropertiesCompanion
import net.minecraft.nbt.CompoundTag
import java.util.UUID

data class RoundProperties(
    override val roundID             : UUID,
    override val tournamentID        : UUID,
    override val roundIndex          : Int,
    override val roundType           : RoundType        = DEFAULT_ROUND_TYPE,
    override val indexedMatchMap     : Map<Int,UUID>    = mutableMapOf()
): RoundPropertyFields, Properties<RoundPropertyFields, RoundProperties, MutableRoundProperties>
{
    companion object: PropertiesCompanion <RoundPropertyFields, RoundProperties, MutableRoundProperties>
    {
        override val helper = RoundPropertiesHelper

    }

    constructor() : this(
        roundID         = UUID.randomUUID(),
        tournamentID    = UUID.randomUUID(),
        roundIndex      = DEFAULT_ROUND_INDEX,
    )

    override fun getHelper() = RoundPropertiesHelper

    override fun deepCopy() = helper.deepCopyHelper( properties = this)

    override fun deepMutableCopy() = helper.deepMutableCopyHelper( properties = this)

    override fun saveToNBT(nbt: CompoundTag) = helper.saveToNBTHelper( properties = this, nbt = nbt)

    // round properties are immutable so empty & a placeholder is fine
    override fun getAllObservables(): Iterable<Observable<*>> = emptyList()
    override fun getChangeObservable(): Observable<RoundProperties> = SimpleObservable()

}
