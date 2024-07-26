package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.util.*
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.someguy.storage.Properties
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class TournamentBuilderProperties(
    name: String = DEFAULT_TOURNAMENT_BUILDER_NAME,
    tournamentBuilderID: TournamentBuilderID = UUID.randomUUID(),
    tournamentProperties: TournamentProperties? = null,
    players: MutableSet<PlayerProperties>? = null,
) : Properties<TournamentBuilderProperties> {

    override val instance = this
    override val helper = TournamentBuilderPropertiesHelper

    private val players: MutableSet<PlayerProperties> = players?.toMutableSet() ?: mutableSetOf()
    private val tournamentProperties = tournamentProperties?.deepCopy() ?: TournamentProperties()

    private val anyChangeObservable = SimpleObservable<TournamentBuilderProperties>()

    private val nameObservable = subscribeTo(SettableObservable(name))
    private val builderIDObservable = subscribeTo(SettableObservable(tournamentBuilderID))

    var name: String
        get() = nameObservable.get()
        set(value) { nameObservable.set(value) }
    var tournamentBuilderID: TournamentBuilderID
        get() = builderIDObservable.get()
        set(value) { builderIDObservable.set(value) }
    var tournamentType: TournamentType
        get() = tournamentProperties.tournamentType
        set(value) { tournamentProperties.tournamentType = value }
    var challengeFormat: ChallengeFormat
        get() = tournamentProperties.challengeFormat
        set(value) { tournamentProperties.challengeFormat = value }
    var maxParticipants: Int
        get() = tournamentProperties.maxParticipants
        set(value) { tournamentProperties.maxParticipants = value }
    var teamSize: Int
        get() = tournamentProperties.teamSize
        set(value) { tournamentProperties.teamSize = value }
    var groupSize: Int
        get() = tournamentProperties.groupSize
        set(value) { tournamentProperties.groupSize = value }
    var minLevel: Int
        get() = tournamentProperties.minLevel
        set(value) { tournamentProperties.minLevel = value }
    var maxLevel: Int
        get() = tournamentProperties.maxLevel
        set(value) { tournamentProperties.maxLevel = value }
    var showPreview: Boolean
        get() = tournamentProperties.showPreview
        set (value) { tournamentProperties.showPreview = value }

    init {
        this.players.forEach { it.getChangeObservable().subscribe { emitChange() } }
        this.tournamentProperties.getChangeObservable().subscribe { emitChange() }
    }

    private fun <T, O : Observable<T>> subscribeTo(observable: O): O {
        observable.subscribe { emitChange() }
        return observable
    }

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getChangeObservable() = anyChangeObservable

    fun getTournamentProperties(
        name: String,
        tournamentID: TournamentID = UUID.randomUUID(),
    ): TournamentProperties {
        val copy = tournamentProperties.deepCopy()
        copy.name = name
        copy.tournamentID = tournamentID
        return copy
    }

    fun containsPlayer(playerID: UUID) = players.firstOrNull { it.playerID == playerID } != null

    fun containsPlayer(name: String) = players.firstOrNull { it.name == name } != null

    fun getPlayersSize() = players.size

    fun getPlayer(playerID: UUID) = players.firstOrNull { it.playerID == playerID }

    fun getPlayer(name: String) = players.firstOrNull { it.name == name }

    /** Returns an iterator over all elements of [TournamentBuilderProperties.players] */
    fun getPlayersIterator() = players.iterator()

    /**
     * Returns a list containing all elements of [TournamentBuilderProperties.players]
     * with a seed > 0
     */
    fun getSeededPlayers() = players.filter { it.seed > 0 }.toList()

    /**
     * Returns a list containing all elements of [TournamentBuilderProperties.players]
     * with a seed < 1
     */
    fun getUnseededPlayers() = players.filter { it.seed < 1 }.toList()

    fun getPlayersDeepCopy(): MutableSet<PlayerProperties> {
        val playersCopy = mutableSetOf<PlayerProperties>()
        for (player in this.players) {
            playersCopy.add(player.deepCopy())
        }
        return playersCopy
    }

    fun addPlayer(playerProps: PlayerProperties): Boolean {
        if (players.add(playerProps)) {
            playerProps.getChangeObservable().subscribe { emitChange() }
            return true
        }
        return false
    }

    fun removePlayer(playerID: UUID): Boolean {
        if (players.removeIf { it.playerID == playerID }) {
            emitChange()
            return true
        }
        return false
    }

    fun removePlayer(name: String): Boolean {
        if (players.removeIf { it.name == name }) {
            emitChange()
            return true
        }
        return false
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

    fun displayShortenedInChat(player: ServerPlayer) {
        helper.displayShortenedInChatHelper(properties = this, player = player)
    }

    fun displayTournamentPropertiesInChat(player: ServerPlayer) {
        tournamentProperties.displaySlimInChat(player = player)
    }

    fun printTournamentProperties() = tournamentProperties.logDebug()

    companion object {
        private val HELPER = TournamentBuilderPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbtHelper(nbt = nbt)
    }

}
