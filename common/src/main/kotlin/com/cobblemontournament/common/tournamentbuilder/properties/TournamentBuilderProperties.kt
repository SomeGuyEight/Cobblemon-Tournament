package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.sg8.collections.reactive.set.MutableObservableSet
import com.sg8.collections.reactive.set.ObservableSet
import com.sg8.collections.reactive.set.mutableObservableSetOf
import com.sg8.properties.DefaultProperties
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID


typealias PlayersSet = ObservableSet<PlayerProperties>
typealias MutablePlayersSet = MutableObservableSet<PlayerProperties>


class TournamentBuilderProperties(
    name: String = DEFAULT_TOURNAMENT_BUILDER_NAME,
    uuid: UUID = UUID.randomUUID(),
    tournamentProperties: TournamentProperties? = null,
    playerSet: PlayersSet? = null,
) : DefaultProperties<TournamentBuilderProperties> {

    override val instance = this
    override val helper = TournamentBuilderPropertiesHelper
    override val observable = SimpleObservable<TournamentBuilderProperties>()
    private val subscriptionsMap: MutableMap<Observable<*>, ObservableSubscription<*>> = mutableMapOf()

    private val _name = SettableObservable(name).subscribe()
    private val _uuid = SettableObservable(uuid).subscribe()
    private var _tournamentProperties = tournamentProperties?.deepCopy() ?: TournamentProperties()
    private var _playerSet = playerSet?.mutableCopy() ?: mutableObservableSetOf()

    var name: String
        get() = _name.get()
        set(value) { _name.set(value) }
    var uuid: UUID
        get() = _uuid.get()
        set(value) { _uuid.set(value) }
    var tournamentProperties: TournamentProperties
        get() = _tournamentProperties
        set(value) {
            replaceSubscription(_tournamentProperties.observable, value.observable)
            _tournamentProperties = value
        }
    var playerSet: MutablePlayersSet
        get() = _playerSet
        set(value) { _playerSet = replaceSubscription(_playerSet, value) }


    init {
        _tournamentProperties.observable.subscribe()
        _playerSet.subscribe()
    }


    private fun <T, O : Observable<T>> replaceSubscription(old: O, new: O): O {
        old.unsubscribe()
        return new.subscribe()
    }

    private fun <T, O : Observable<T>> O.subscribe(): O {
        subscriptionsMap[this] = this.subscribe { emitChange() }
        return this
    }

    private fun <T, O : Observable<T>> O.unsubscribe(): O {
        subscriptionsMap[this]?.unsubscribe()
        return this
    }

    override fun emitChange() = observable.emit(this)

    fun getPlayersDeepCopy(): MutablePlayersSet {
        val playersCopy: MutablePlayersSet = mutableObservableSetOf()
        for (player in this.playerSet) {
            playersCopy.add(player.deepCopy())
        }
        return playersCopy
    }

    fun getSeededPlayers() = playerSet.filter { it.seed > 0 }.toList()

    fun getUnseededPlayers() = playerSet.filter { it.seed < 1 }.toList()

    fun <T : Comparable<T>> getPlayersSortedBy(
        selector: (PlayerProperties) -> T,
    ) = playerSet.elements.sortedBy { selector(it) }

    fun displayShortenedInChat(player: ServerPlayer) {
        helper.displayShortenedInChatHelper(properties = this, player = player)
    }

    fun displayTournamentPropertiesInChat(player: ServerPlayer) {
        tournamentProperties.displaySlimInChat(player = player)
    }

    fun printTournamentProperties() = tournamentProperties.printDebug()

    companion object {
        private val HELPER = TournamentBuilderPropertiesHelper
        fun loadFromNbt(nbt: CompoundTag) = HELPER.loadFromNbt(nbt = nbt)
    }

}
