package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.config.TournamentConfig
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.tournamentbuilder.properties.TournamentBuilderPropertiesHelper.DEFAULT_TOURNAMENT_BUILDER_NAME
import com.someguy.storage.properties.Properties
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class TournamentBuilderProperties(
    name: String = DEFAULT_TOURNAMENT_BUILDER_NAME,
    tournamentBuilderID: UUID = UUID.randomUUID(),
    tournamentType: TournamentType = TournamentConfig.defaultTournamentType(),
    challengeFormat: ChallengeFormat = TournamentConfig.defaultChallengeFormat(),
    maxParticipants: Int = TournamentConfig.defaultMaxParticipants(),
    teamSize: Int = TournamentConfig.defaultTeamSize(),
    groupSize: Int = TournamentConfig.defaultGroupSize(),
    minLevel: Int = TournamentConfig.defaultMinLevel(),
    maxLevel: Int = TournamentConfig.defaultMaxLevel(),
    showPreview: Boolean = TournamentConfig.defaultShowPreview(),
    players: MutableSet<PlayerProperties> = mutableSetOf(),
) : Properties<TournamentBuilderProperties> {

    override val instance = this
    var name = name
        set(value) {
            field = value
            emitChange()
        }
    var tournamentBuilderID: UUID = tournamentBuilderID
        set(value) {
            field = value
            emitChange()
        }
    private var players: MutableSet<PlayerProperties> = players.toMutableSet()
        set(value) {
            field = value
            emitChange()
        }
    // TODO make getters & setters for properties needed
    val tournamentProperties = TournamentProperties(
        tournamentID = tournamentBuilderID,
        tournamentType = tournamentType,
        challengeFormat = challengeFormat,
        maxParticipants = maxParticipants,
        teamSize = teamSize,
        groupSize = groupSize,
        minLevel = minLevel,
        maxLevel = maxLevel,
        showPreview = showPreview,
    )

    override val helper = TournamentBuilderPropertiesHelper
    private val observables = mutableListOf <Observable <*>>()
    private val anyChangeObservable = SimpleObservable <TournamentBuilderProperties>()

    init {
        for (player in this.players) {
            registerObservable(observable = player.getChangeObservable() )
        }
        registerObservable(observable = tournamentProperties.getChangeObservable() )
    }

    constructor(uuid: UUID = UUID.randomUUID()) : this(tournamentBuilderID = uuid)

    fun containsPlayerID( playerID: UUID ) = players.firstOrNull { it.playerID == playerID } != null

    fun getPlayer(playerID: UUID) = players.firstOrNull { it.playerID == playerID }

    fun getPlayer(name: String) = players.firstOrNull { it.name == name }

    fun getPlayersSize() = players.size

    /** Returns an iterator over all elements of [TournamentBuilderProperties.players] */
    fun getPlayersIterator() = players.iterator()

    /** Returns a list containing all elements of [TournamentBuilderProperties.players] with a seed > 0 */
    fun getSeededPlayers() = players.filter { it.seed > 0 }.toList()

    /** Returns a list containing all elements of [TournamentBuilderProperties.players] with a seed < 1 */
    fun getUnseededPlayers() = players.filter { it.seed < 1 }.toList()

    fun getPlayersDeepCopy(): MutableSet<PlayerProperties> {
        val playersCopy = mutableSetOf<PlayerProperties>()
        for (player in this.players) {
            playersCopy.add(player.deepCopy())
        }
        return playersCopy
    }

    fun addPlayer(playerProps: PlayerProperties): Boolean {
        return if (players.add(playerProps)) {
            registerObservable(observable = playerProps.getChangeObservable())
            emitChange()
            true
        } else {
            false
        }
    }

    fun removePlayer(playerID: UUID): Boolean {
        return if (players.removeIf { it.playerID == playerID }) {
            emitChange()
            true
        } else {
            false
        }
    }

    fun removePlayer(name: String): Boolean {
        return if (players.removeIf { it.name == name }) {
            emitChange()
            true
        } else {
            false
        }
    }

    fun <T : Comparable<T>> getPlayersSortedBy(
        predicate: (PlayerProperties) -> Boolean = { true },
        selector: (PlayerProperties) -> T,
    ): List<PlayerProperties> {
        val list = mutableListOf<PlayerProperties>()
        for (playerProps in players) {
            if (predicate(playerProps)) {
                list.add(playerProps)
            }
        }
        return list.sortedBy { selector(it) }
    }

    private fun registerObservable(observable: Observable<*>): Observable<*> {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit((this)) }
        return observable
    }

    private fun emitChange() = anyChangeObservable.emit((this))
    override fun getAllObservables() = observables.asIterable()
    override fun getChangeObservable() = anyChangeObservable

    fun displayInChatSlim(player: ServerPlayer) {
        helper.displayInChatSlimHelper(properties = this, player = player)
    }

    companion object {
        private val HELPER = TournamentBuilderPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNBTHelper(nbt = nbt)
    }

}
