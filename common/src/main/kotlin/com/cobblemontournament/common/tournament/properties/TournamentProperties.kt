package com.cobblemontournament.common.tournament.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.config.Config
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentPropertiesHelper.DEFAULT_TOURNAMENT_NAME
import com.someguy.storage.properties.Properties
import com.someguy.storage.properties.PropertiesCompanion
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat
import net.minecraft.nbt.CompoundTag
import java.util.UUID

data class TournamentProperties(
    override val name               : String            = DEFAULT_TOURNAMENT_NAME,
    override var tournamentID       : UUID              = UUID.randomUUID(),
    override val tournamentType     : TournamentType    = Config.defaultTournamentType(),
    override val challengeFormat    : ChallengeFormat   = Config.defaultChallengeFormat(),
    override val maxParticipants    : Int               = Config.defaultMaxParticipants(),
    override val teamSize           : Int               = Config.defaultTeamSize(),
    override val groupSize          : Int               = Config.defaultGroupSize(),
    override val minLevel           : Int               = Config.defaultMinLevel(),
    override val maxLevel           : Int               = Config.defaultMaxLevel(),
    override val showPreview        : Boolean           = Config.defaultShowPreview(),
    override val totalRounds        : Int               = 0,
    override val totalMatches       : Int               = 0,
    override val totalPlayers       : Int               = 0,
    override val players            : Map<UUID,String>  = mapOf(),
) : Properties<TournamentPropertyFields,TournamentProperties,MutableTournamentProperties>,TournamentPropertyFields
{
    companion object
        : PropertiesCompanion <TournamentPropertyFields, TournamentProperties, MutableTournamentProperties>
    {
        override val helper = TournamentPropertiesHelper
    }

    override fun getHelper() = TournamentPropertiesHelper

    override fun deepCopy() = helper.deepCopyHelper(properties = this)

    override fun deepMutableCopy() = helper.deepMutableCopyHelper( properties = this)

    override fun saveToNBT(nbt: CompoundTag) = helper.saveToNBTHelper(properties = this,nbt = nbt)

    // tournament properties are immutable so empty & a placeholder is fine
    override fun getAllObservables(): Iterable<Observable<*>> = emptyList()
    override fun getChangeObservable(): Observable<TournamentProperties> = SimpleObservable()

}