package com.cobblemontournament.common.tournamentbuilder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.generator.TournamentGenerator
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.tournamentbuilder.properties.TournamentBuilderProperties
import com.cobblemontournament.common.api.tournament.TournamentData
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.api.storage.TournamentDataKeys.TOURNAMENT_BUILDER_PROPERTIES_KEY
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.cobblemontournament.common.util.ChatUtil
import com.google.gson.JsonObject
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.coordinates.StoreCoordinates
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

/** &#9888; (UUID) constructor is needed for serialization method */
open class TournamentBuilder(
    protected val properties: TournamentBuilderProperties
) : ClassStored {

    override var name
        get() = properties.name
        set(value) { properties.name = value }
    override var uuid
        get() = properties.tournamentBuilderID
        protected set (value) { properties.tournamentBuilderID = value }
    var tournamentType
        get() = properties.tournamentProperties.tournamentType
        set(value) { properties.tournamentProperties.tournamentType = value }
    var challengeFormat
        get() = properties.tournamentProperties.challengeFormat
        set(value) { properties.tournamentProperties.challengeFormat = value }
    var maxParticipants
        get() = properties.tournamentProperties.maxParticipants
        set(value) { properties.tournamentProperties.maxParticipants = value }
    var teamSize
        get() = properties.tournamentProperties.teamSize
        set(value) { properties.tournamentProperties.teamSize = value }
    var groupSize
        get() = properties.tournamentProperties.groupSize
        set(value) { properties.tournamentProperties.groupSize = value }
    var minLevel
        get() = properties.tournamentProperties.minLevel
        set(value) { properties.tournamentProperties.minLevel = value }
    var maxLevel
        get() = properties.tournamentProperties.maxLevel
        set(value) { properties.tournamentProperties.maxLevel = value }
    var showPreview
        get() = properties.tournamentProperties.showPreview
        set (value) { properties.tournamentProperties.showPreview = value }

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> = SettableObservable(value = null)
    private val observables = mutableListOf <Observable<*>>()
    val anyChangeObservable = SimpleObservable<TournamentBuilder>()

    constructor(uuid: UUID = UUID.randomUUID()) : this(TournamentBuilderProperties(tournamentBuilderID = uuid))

    override fun initialize(): TournamentBuilder {
        registerObservable(observable = properties.getChangeObservable() )
        return this
    }

    fun getTournamentProperties(
        name: String,
        tournamentID: UUID = UUID.randomUUID(),
    ): TournamentProperties {
        val copy = properties.tournamentProperties.deepCopy()
        copy.name = name
        copy.tournamentID = tournamentID
        return copy
    }

    fun getPlayer(playerID: UUID) = properties.getPlayer(playerID = playerID)

    fun getPlayer(name: String) = properties.getPlayer(name = name)

    fun getPlayersSize() = properties.getPlayersSize()

    fun getPlayersNames(): Set<String> {
        val names = mutableSetOf<String>()
        for (playerProps in properties.getPlayersIterator()) {
            names.add(playerProps.name)
        }
        return names
    }

    fun getSeededPlayers() = properties.getSeededPlayers()

    fun getUnseededPlayers() = properties.getUnseededPlayers()

    fun addPlayer(
        playerID: UUID,
        playerName: String,
        actorType: ActorType? = null,
        seed: Int? = null,
    ): Boolean {
        return if (!properties.containsPlayerID(playerID = playerID)) {
            properties.addPlayer(
                playerProps = PlayerProperties(
                    name = playerName,
                    actorType = (actorType ?: ActorType.PLAYER),
                    playerID = playerID,
                    tournamentID = uuid,
                    seed = (seed ?: -1),
                )
            )
        } else {
            false
        }
    }

    fun updatePlayer(playerID: UUID, actorType: ActorType?, seed: Int?): Boolean {
        val playerProps = getPlayer(playerID = playerID)
            ?: return false

        val updated = if (actorType != null) {
            playerProps.actorType = actorType
            true
        } else {
            false
        }

        return if (seed != null && seed != playerProps.seed) {
            playerProps.seed = seed
            playerProps.originalSeed = seed
            true
        } else {
            updated
        }
    }

    fun removePlayer(playerID: UUID) = properties.removePlayer(playerID = playerID)

    fun removePlayerByName(name: String) = properties.removePlayer(name = name)

    fun toTournament(name: String): TournamentData? {
        val tournamentData = TournamentGenerator.toTournament(name = name, builder = this)
        tournamentData?.sendAllToManager()
        return tournamentData
    }

    private fun registerObservable(observable: Observable<*>): Observable<*> {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit((this)) }
        return observable
    }

    fun getAllObservables() = observables.asIterable()

    override fun getChangeObservable() = anyChangeObservable

    override fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.put(TOURNAMENT_BUILDER_PROPERTIES_KEY, properties.saveToNBT(nbt = CompoundTag()))
        return nbt
    }
    override fun loadFromNBT(nbt: CompoundTag): TournamentBuilder {
        properties.setFromNBT(nbt = nbt.getCompound(TOURNAMENT_BUILDER_PROPERTIES_KEY))
        return this
    }
    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not implemented") }
    override fun loadFromJSON(json: JsonObject): TournamentBuilder { TODO("Not implemented") }

    override fun printProperties() = properties.logDebug()

    fun displayPropertiesInChat(player: ServerPlayer) = properties.displayInChat(player = player)

    fun displayPropertiesInChatSlim(player: ServerPlayer) = properties.displayInChatSlim(player = player)

    fun printPlayerInfo() {
        for ( player in properties.getPlayersSortedBy { it.seed } ) {
            player.logDebug()
        }
    }

    fun displayPlayerInfoInChat(
        player: ServerPlayer,
        spacing: String = "",
        displaySeed: Boolean = false,
        displayPokemon: Boolean = false,
        displayCurrentMatch: Boolean = false,
        displayPlacement: Boolean = false
    ) {
        if (properties.getPlayersSize() != 0) {
            ChatUtil.displayInPlayerChat(
                player  = player,
                text    = "Players for Tournament Builder \"$name\":", ChatUtil.yellow,
                bold    = true )
            for ( playerProps in properties.getPlayersSortedBy { it.seed } ) {
                PlayerPropertiesHelper.displayInChatOptionalHelper(
                    properties          = playerProps,
                    player              = player,
                    spacing             = spacing,
                    displaySeed         = displaySeed,
                    displayPokemon      = displayPokemon,
                    displayCurrentMatch = displayCurrentMatch,
                    displayPlacement    = displayPlacement )
            }
        } else {
            ChatUtil.displayInPlayerChat( player, "No players registered for Tournament Builder \"$name\"." )
        }
    }

    companion object {
        /** &#9888; Observables will be broken if [initialize] is not called after construction */
        fun loadFromNbt(nbt: CompoundTag): TournamentBuilder {
            return TournamentBuilder(
                TournamentBuilderProperties.loadFromNbt(nbt = nbt.getCompound(TOURNAMENT_BUILDER_PROPERTIES_KEY))
            )
        }
    }

}
