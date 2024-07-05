package com.cobblemontournament.common.tournament.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.config.Config
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentPropertiesHelper.DEFAULT_TOURNAMENT_NAME
import com.someguy.storage.properties.MutableProperties
import com.someguy.storage.properties.PropertiesCompanion
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MutableTournamentProperties : MutableProperties <TournamentPropertyFields, TournamentProperties,MutableTournamentProperties>,
    TournamentPropertyFields
{
    constructor(
        name            : String            = DEFAULT_TOURNAMENT_NAME,
        tournamentID    : UUID              = UUID.randomUUID(),
        tournamentType  : TournamentType    = Config.defaultTournamentType(),
        challengeFormat : ChallengeFormat   = Config.defaultChallengeFormat(),
        maxParticipants : Int               = Config.defaultMaxParticipants(),
        teamSize        : Int               = Config.defaultTeamSize(),
        groupSize       : Int               = Config.defaultGroupSize(),
        minLevel        : Int               = Config.defaultMinLevel(),
        maxLevel        : Int               = Config.defaultMaxLevel(),
        showPreview     : Boolean           = Config.defaultShowPreview(),
        totalRounds     : Int               = -1, // leave as -1 until I am sure this ever makes it out
        totalMatches    : Int               = -1, // leave as -1 until I am sure this ever makes it out
        totalPlayers    : Int               = -1, // leave as -1 until I am sure this ever makes it out
        players         : MutableMap<UUID,String> = mutableMapOf(),
    ) : super()
    {
        this.name               = name
        this.tournamentID       = tournamentID
        this.tournamentType     = tournamentType
        this.challengeFormat    = challengeFormat
        this.maxParticipants    = maxParticipants
        this.teamSize           = teamSize
        this.groupSize          = groupSize
        this.minLevel           = minLevel
        this.maxLevel           = maxLevel
        this.showPreview        = showPreview
        this.totalRounds        = totalRounds
        this.totalMatches       = totalMatches
        this.totalPlayers       = totalPlayers
        this.players.putAll( players)
    }

    override var name = DEFAULT_TOURNAMENT_NAME
        set(value) { field = value; emitChange() }

    override var tournamentID: UUID = UUID.randomUUID()
        set(value) { field = value; emitChange() }

    override var tournamentType = Config.defaultTournamentType()
        set(value) { field = value; emitChange() }

    override var challengeFormat = Config.defaultChallengeFormat()
        set(value) { field = value; emitChange() }

    override var maxParticipants = Config.defaultMaxParticipants()
        set(value) { field = value; emitChange() }

    override var teamSize = Config.defaultTeamSize()
        set(value) { field = value; emitChange() }

    override var groupSize = Config.defaultGroupSize()
        set(value) { field = value; emitChange() }

    override var minLevel = Config.defaultMinLevel()
        set(value) { field = value; emitChange() }

    override var maxLevel = Config.defaultMaxLevel()
        set(value) { field = value; emitChange() }

    override var showPreview = Config.defaultShowPreview()
        set(value) { field = value; emitChange() }

    override var totalRounds = -1 // leave as -1 until I am sure this ever makes it out
        set(value) { field = value; emitChange() }

    override var totalMatches = -1 // leave as -1 until I am sure this ever makes it out
        set(value) { field = value; emitChange() }

    override var totalPlayers = -1 // leave as -1 until I am sure this ever makes it out
        set(value) { field = value; emitChange() }

    override var players = mutableMapOf<UUID, String>()
        set(value) { field = value; emitChange() }

    companion object
        : PropertiesCompanion <TournamentPropertyFields, TournamentProperties, MutableTournamentProperties>
    {
        override val helper = TournamentPropertiesHelper
    }

    override fun getHelper() = TournamentPropertiesHelper

    override fun deepCopy() = helper.deepCopyHelper(properties = this)

    override fun deepMutableCopy() = helper.deepMutableCopyHelper(properties = this)

    override fun setFromNBT(
        nbt: CompoundTag
    ): MutableTournamentProperties {
        return helper.setFromNBTHelper( mutable = this, nbt = nbt)
    }

    override fun setFromProperties(
        from: TournamentPropertyFields
    ): MutableTournamentProperties {
        return helper.setFromPropertiesHelper(mutable = this, from = from)
    }

    override fun saveToNBT(
        nbt: CompoundTag
    ): CompoundTag {
        return helper.saveToNBTHelper( properties = this, nbt = nbt)
    }

    private val observables = mutableListOf<Observable<*>>()
    val anyChangeObservable = SimpleObservable<MutableTournamentProperties>()

    private fun emitChange() = anyChangeObservable.emit(this)
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable(): Observable<MutableTournamentProperties> = anyChangeObservable

    private fun <T> registerObservable(
        observable: SimpleObservable<T>
    ): SimpleObservable<T>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

}
