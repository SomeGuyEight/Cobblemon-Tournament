package com.cobblemontournament.common.tournamentbuilder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemontournament.common.api.TournamentData
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.generator.TournamentGenerator
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper
import com.cobblemontournament.common.tournament.TournamentType
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.tournamentbuilder.properties.TournamentBuilderProperties
import com.cobblemontournament.common.util.*
import com.google.gson.JsonObject
import com.someguy.storage.ClassStored
import com.someguy.storage.StoreCoordinates
import com.someguy.storage.util.NameSet
import com.someguy.storage.util.PlayerID
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

open class TournamentBuilder(protected val properties: TournamentBuilderProperties) : ClassStored {

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable(value = null)

    private val anyChangeObservable = SimpleObservable<TournamentBuilder>()

    override var name: String
        get() = properties.name
        set(value) { properties.name = value }
    override var uuid: TournamentBuilderID
        get() = properties.tournamentBuilderID
        protected set (value) { properties.tournamentBuilderID = value }
    var tournamentType: TournamentType
        get() = properties.tournamentType
        set(value) { properties.tournamentType = value }
    var challengeFormat: ChallengeFormat
        get() = properties.challengeFormat
        set(value) { properties.challengeFormat = value }
    var maxParticipants: Int
        get() = properties.maxParticipants
        set(value) { properties.maxParticipants = value }
    var teamSize: Int
        get() = properties.teamSize
        set(value) { properties.teamSize = value }
    var groupSize: Int
        get() = properties.groupSize
        set(value) { properties.groupSize = value }
    var minLevel: Int
        get() = properties.minLevel
        set(value) { properties.minLevel = value }
    var maxLevel: Int
        get() = properties.maxLevel
        set(value) { properties.maxLevel = value }
    var showPreview: Boolean
        get() = properties.showPreview
        set (value) { properties.showPreview = value }

    init {
        properties.getChangeObservable().subscribe { emitChange() }
    }

    /** &#9888; (UUID) constructor is needed for serialization method */
    constructor(uuid: TournamentBuilderID = UUID.randomUUID()) :
            this(TournamentBuilderProperties(tournamentBuilderID = uuid))

    override fun initialize() = this

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getChangeObservable() = anyChangeObservable

    fun getTournamentProperties(
        name: String,
        tournamentID: TournamentID = UUID.randomUUID(),
    ): TournamentProperties {
        return properties.getTournamentProperties(name, tournamentID)
    }

    fun containsPlayer(playerID: PlayerID) = properties.containsPlayer(playerID)

    fun containsPlayer(name: String) = properties.containsPlayer(name)

    fun getPlayersNames(): NameSet {
        val names = mutableSetOf<String>()
        for (playerProps in properties.getPlayersIterator()) {
            names.add(playerProps.name)
        }
        return names
    }

    fun getPlayer(playerID: UUID) = properties.getPlayer(playerID = playerID)

    fun getPlayer(name: String) = properties.getPlayer(name = name)

    fun getPlayersSize() = properties.getPlayersSize()

    fun getSeededPlayers() = properties.getSeededPlayers()

    fun getUnseededPlayers() = properties.getUnseededPlayers()

    fun addPlayer(
        playerID: UUID,
        playerName: String,
        actorType: ActorType? = null,
        seed: Int? = null,
    ): Boolean {
        return if (!properties.containsPlayer(playerID = playerID)) {
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

    fun removePlayer(playerID: UUID) = properties.removePlayer(playerID = playerID)

    fun removePlayer(name: String) = properties.removePlayer(name = name)

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
        properties.setFromNbt(nbt = nbt.getCompound(TOURNAMENT_BUILDER_PROPERTIES_KEY))
        return this
    }

    override fun loadFromJSON(json: JsonObject): TournamentBuilder { TODO() }

    override fun printProperties() = properties.logDebug()

    fun displayPropertiesInChat(player: ServerPlayer) = properties.displayInChat(player = player)

    fun displayPropertiesInChatSlim(player: ServerPlayer) {
        properties.displayShortenedInChat(player)
    }

    fun printPlayerInfo() {
        properties.getPlayersSortedBy { it.seed }.forEach { it.logDebug() }
    }

    fun displayPlayersInChat(
        player: ServerPlayer,
        padStart: Int = 0,
        displaySeed: Boolean = false,
        displayPokemon: Boolean = false,
        displayCurrentMatch: Boolean = false,
        displayPlacement: Boolean = false
    ) {
        if (properties.getPlayersSize() != 0) {
            player.displayInChat(
                text = "Players for Tournament Builder \"$name\":", ChatUtil.YELLOW_FORMAT,
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
