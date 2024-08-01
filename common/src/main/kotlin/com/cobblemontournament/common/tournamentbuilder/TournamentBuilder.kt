package com.cobblemontournament.common.tournamentbuilder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.storage.TOURNAMENT_BUILDER_PROPERTIES_KEY
import com.cobblemontournament.common.api.tournament.TournamentData
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.generator.TournamentGenerator
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.tournamentbuilder.properties.MutablePlayersSet
import com.cobblemontournament.common.tournamentbuilder.properties.TournamentBuilderProperties
import com.google.gson.JsonObject
import com.sg8.util.YELLOW_FORMAT
import com.sg8.storage.TypeStored
import com.sg8.storage.StoreCoordinates
import com.sg8.storage.NameSet
import com.sg8.util.displayInChat
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

open class TournamentBuilder(protected val properties: TournamentBuilderProperties) : TypeStored {

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable(value = null)

    private val anyChangeObservable = SimpleObservable<TournamentBuilder>()

    override var name: String
        get() = properties.name
        set(value) { properties.name = value }
    override var uuid: UUID
        get() = properties.uuid
        protected set (value) { properties.uuid = value }
    private val tournamentProperties get() = properties.tournamentProperties
    private val players: MutablePlayersSet get() = properties.playerSet

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
        properties.observable.subscribe { emitChange() }
    }


    /** &#9888; (UUID) constructor is needed for serialization method */
    constructor(uuid: UUID = UUID.randomUUID()) :
            this(TournamentBuilderProperties(uuid = uuid))

    override fun initialize() = this

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getObservable(): Observable<TournamentBuilder> = anyChangeObservable

    fun getTournamentProperties(
        name: String,
        tournamentID: UUID = UUID.randomUUID(),
    ): TournamentProperties {
        val copy = tournamentProperties.deepCopy()
        copy.name = name
        copy.uuid = tournamentID
        return copy
    }

    fun containsPlayer(playerID: UUID) = players.firstOrNull { it.uuid == playerID } != null

    fun containsPlayer(name: String) = players.firstOrNull { it.name == name } != null

    fun getPlayersSize() = players.size

    fun getPlayer(playerID: UUID) = players.firstOrNull { it.uuid == playerID }

    fun getPlayer(name: String) = players.firstOrNull { it.name == name }

    fun getPlayersIterator() = players.iterator()

    fun getSeededPlayers() = properties.getSeededPlayers()

    fun getUnseededPlayers() = properties.getUnseededPlayers()

    fun getPlayersNames(): NameSet {
        val names = mutableSetOf<String>()
        for (playerProps in players.iterator()) {
            names.add(playerProps.name)
        }
        return names
    }

    fun addPlayer(
        playerID: UUID,
        playerName: String,
        actorType: ActorType? = null,
        seed: Int? = null,
    ): Boolean {
        if (!containsPlayer(playerID = playerID)) {
            return players.add(
                PlayerProperties(
                    name = playerName,
                    actorType = actorType ?: ActorType.PLAYER,
                    uuid = playerID,
                    tournamentID = uuid,
                    seed = seed ?: -1,
                )
            )
        }
        return false
    }

    fun removePlayer(playerID: UUID) = players.removeIf { it.uuid == playerID }

    fun removePlayer(name: String) = players.removeIf { it.name == name }

    private fun toTournament(name: String): TournamentData? {
        return TournamentGenerator.toTournament(name = name, builder = this)
    }

    fun toTournamentAndSave(name: String): TournamentData? {
        val data = toTournament(name = name)
        data?.saveAll()
        return data
    }

    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        nbt.put(TOURNAMENT_BUILDER_PROPERTIES_KEY, properties.saveToNbt(nbt = CompoundTag()))
        return nbt
    }

    override fun saveToJSON(json: JsonObject): JsonObject { TODO() }

    override fun loadFromNBT(nbt: CompoundTag): TournamentBuilder {
        properties.setFromNbt(nbt.getCompound(TOURNAMENT_BUILDER_PROPERTIES_KEY))
        return this
    }

    override fun loadFromJSON(json: JsonObject): TournamentBuilder { TODO() }

    fun deepCopy() = TournamentBuilder(properties.deepCopy())

    fun copy() = TournamentBuilder(properties.copy())

    override fun printProperties() = properties.printDebug()

    fun displayPropertiesInChat(player: ServerPlayer) = properties.displayInChat(player = player)

    fun displayPropertiesInChatSlim(player: ServerPlayer) {
        properties.displayShortenedInChat(player)
    }

    fun printPlayerInfo() {
        properties.getPlayersSortedBy { it.seed }.forEach { it.printDebug() }
    }

    fun displayPlayersInChat(
        player: ServerPlayer,
        padStart: Int = 0,
        displaySeed: Boolean = false,
        displayPokemon: Boolean = false,
        displayCurrentMatch: Boolean = false,
        displayPlacement: Boolean = false
    ) {
        if (properties.playerSet.size != 0) {
            player.displayInChat(
                text = "Players for Tournament Builder \"$name\":", color = YELLOW_FORMAT,
                bold = true,
            )
            for ( playerProps in properties.getPlayersSortedBy { it.seed } ) {
                PlayerPropertiesHelper.displayInChatHelper(
                    properties = playerProps,
                    player = player,
                    padStart = padStart,
                    displaySeed = displaySeed,
                    displayPokemon = displayPokemon,
                    displayCurrentMatch = displayCurrentMatch,
                    displayPlacement = displayPlacement,
                )
            }
        } else {
            player.displayInChat(text = "No players registered for Tournament Builder \"$name\".")
        }
    }

    companion object {
        fun loadFromNbt(nbt: CompoundTag): TournamentBuilder {
            return TournamentBuilder(
                TournamentBuilderProperties.loadFromNbt(
                    nbt = nbt.getCompound(TOURNAMENT_BUILDER_PROPERTIES_KEY),
                )
            )
        }
    }

}
