package com.cobblemontournament.common.match.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_ROUND_MATCH_INDEX
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_TOURNAMENT_MATCH_INDEX
import com.cobblemontournament.common.match.properties.MatchPropertiesHelper.DEFAULT_VICTOR_ID
import com.someguy.storage.properties.Properties
import com.someguy.storage.properties.PropertiesCompanion
import net.minecraft.nbt.CompoundTag
import java.util.UUID

data class MatchProperties(
    override val matchID                : UUID,
    override val tournamentID           : UUID,
    override val roundID                : UUID,
    override val tournamentMatchIndex   : Int,
    override val roundMatchIndex        : Int,
    override val matchStatus            : MatchStatus   = MatchStatus.UNKNOWN,
    override val victorID               : UUID?         = DEFAULT_VICTOR_ID,
    override val playerMap              : Map<UUID,Int> = hashMapOf(),
) : Properties <MatchPropertyFields,MatchProperties,MutableMatchProperties>,
    MatchPropertyFields
{
    companion object
        : PropertiesCompanion <MatchPropertyFields, MatchProperties, MutableMatchProperties>
    {
        override val helper = MatchPropertiesHelper
    }

    constructor() : this(
        matchID                 = UUID.randomUUID(),
        tournamentID            = UUID.randomUUID(),
        roundID                 = UUID.randomUUID(),
        tournamentMatchIndex    = DEFAULT_TOURNAMENT_MATCH_INDEX,
        roundMatchIndex         = DEFAULT_ROUND_MATCH_INDEX,
    )

    override fun getHelper() = MatchPropertiesHelper

    override fun deepCopy() = helper.deepCopyHelper( properties = this)

    override fun deepMutableCopy() = helper.deepMutableCopyHelper( properties = this)

    override fun saveToNBT(nbt: CompoundTag) = helper.saveToNBTHelper( properties = this, nbt = nbt)

    // match properties are immutable so empty & a placeholder is fine
    override fun getAllObservables(): Iterable<Observable<*>> = emptyList()
    override fun getChangeObservable(): Observable<MatchProperties> = SimpleObservable()

}
