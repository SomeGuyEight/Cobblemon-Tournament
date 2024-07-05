package com.cobblemontournament.common

import com.cobblemon.mod.common.api.Priority
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.match.MatchStore
import com.cobblemontournament.common.player.PlayerStore
import com.cobblemontournament.common.round.RoundStore
import com.cobblemontournament.common.tournament.TournamentStore
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.player.properties.MutablePlayerProperties
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilderStore
import com.cobblemontournament.common.tournament.TournamentData
import com.cobblemontournament.common.util.TournamentDataKeys
import com.google.gson.GsonBuilder
import com.someguy.storage.adapter.flatfile.NBTStoreAdapter
import com.someguy.storage.adapter.flatfile.OneToOneFileStoreAdapter
import com.someguy.storage.factory.FileBackedStoreFactory
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.util.StoreUtil.getUuidKey
import com.someguy.storage.util.StoreUtil.getFile
import com.someguy.storage.StoreManager
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.store.DefaultStore
import com.someguy.storage.store.Store
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import java.io.File
import java.nio.file.Path
import java.util.UUID

@Suppress("MemberVisibilityCanBePrivate")
object TournamentManager: StoreManager()
{
    private const val ROOT_DIR_NAME         = "tournament"
    private const val STORE_DIR_NAME        = "stores"
    private const val KEY_DIR_NAME          = "keys"
    private const val KEY_FILE_NAME         = "store-keys"
    private const val USE_NESTED_FOLDERS    : Boolean   = true
    private const val FOLDER_PER_CLASS      : Boolean   = true
    private const val CREATE_IF_MISSING     : Boolean   = true
    private const val TOURNAMENT_KEY        : String    = TournamentDataKeys.TOURNAMENT_STORE_KEY

    private const val FAILED_TO_GET_STORE   = "Failed to get store"
    private const val FAILED_INSIDE_STORE   = "Failed inside store"
    private const val INVALID_POSITION      = "Invalid position"
    private const val NO_INSTANCE_WITH_NAME = "No instance with name"

    var serverStoreKey                  : UUID              = UUID.randomUUID()
    private var server                      : MinecraftServer?  = null
    private var savePath                    : Path?             = null
    private var factory : FileBackedStoreFactory<CompoundTag>?  = null

    private fun savePath() :Path {
        if (savePath == null){
            throw NullPointerException("savePath is null")
        } else {
            return savePath!!
        }
    }

    private fun keyDir():File   = getFile( savePath(), ROOT_DIR_NAME,KEY_DIR_NAME)
    private fun storeDir():File = getFile( savePath(), ROOT_DIR_NAME,STORE_DIR_NAME)

    private fun factory(): FileBackedStoreFactory<CompoundTag>
    {
        if (factory == null) {
            factory = FileBackedStoreFactory(nbtStoreAdapter(storeDir()), CREATE_IF_MISSING)
        }
        return factory!!
    }

    /* !! keep return value as "OneToOneFileStoreAdapter<CompoundTag>"
     *  - issues if passed as "NBTStoreAdapter" b/c 'class name erased' ?? IDK exactly
     */
    private fun nbtStoreAdapter(dir: File): OneToOneFileStoreAdapter<CompoundTag> {
        return NBTStoreAdapter(dir.toString(), USE_NESTED_FOLDERS, FOLDER_PER_CLASS)
    }

    fun getTournamentBuilderStore(storeKey: UUID? = null): TournamentBuilderStore? {
        return getStore(TournamentBuilderStore::class.java, uuid = storeKey?: serverStoreKey)
    }
    fun getTournamentStore(storeKey: UUID? = null): TournamentStore? {
        return getStore(TournamentStore::class.java, uuid = storeKey?: serverStoreKey)
    }
    private fun getRoundStore(tournamentID: UUID): RoundStore? {
        return getStore(RoundStore::class.java, tournamentID)
    }
    private fun getMatchStore(tournamentID: UUID): MatchStore? {
        return getStore(MatchStore::class.java, tournamentID)
    }
    private fun getPlayerStore(tournamentID: UUID): PlayerStore? {
        return getStore(PlayerStore::class.java, tournamentID)
    }

    fun initialize(server: MinecraftServer)
    {
        this.server = server
        savePath = server.getWorldPath(LevelResource.ROOT)
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

        registerFactory(Priority.NORMAL, factory())

        val keyDir = keyDir()
        serverStoreKey  = getUuidKey(savePath(), keyDir, KEY_FILE_NAME, TOURNAMENT_KEY, gson)
    }

    fun getTournamentBuilder( builderID: UUID): TournamentBuilder? {
        return getTournamentBuilderStore()?.get(UuidPosition(builderID))
    }
    fun getTournament( tournamentID: UUID): Tournament? {
        return getTournamentStore()?.get(UuidPosition(tournamentID))
    }
    fun getRound( tournamentID: UUID, roundID: UUID): TournamentRound? {
        return getRoundStore(tournamentID)?.get(UuidPosition(roundID))
    }
    fun getMatch( tournamentID: UUID, matchID: UUID): TournamentMatch? {
        return getMatchStore(tournamentID)?.get(UuidPosition(matchID))
    }
    fun getPlayer( tournamentID: UUID, playerID: UUID): TournamentPlayer? {
        return getPlayerStore(tournamentID)?.get(UuidPosition(playerID))
    }

    fun saveStore( store: Store<*,*>) = factory().save( store)

    fun saveAllFactories() = factory().saveAll()

    fun addTournamentData(
        data: TournamentData
    ):  Pair<Boolean,String>
    {
        val result = addTournament(data.tournament, saveStore = false)
        if (!result.first) {
            return result
        }
        data.rounds.forEach  { addRound (it, saveStore = false) }
        data.matches.forEach { addMatch (it, saveStore = false) }
        data.players.forEach { addPlayer(it, saveStore = false) }
        return result
    }

    /**
     * [builder], [TournamentBuilder], [TournamentBuilderStore]
     *
     *      if successfully added [builder] to an empty [TournamentBuilderStore] position ->
     *          returns null
     *      if successfully added [builder] to an occupied [TournamentBuilderStore] position ->
     *          returns the replaced [TournamentBuilder]
     *      if failed to add b/c [TournamentBuilderStore] was null or store was full ->
     *          * returns [builder] parameter
     *
     *      * - ( message will specify if store was null or store was full )
     */
    fun addTournamentBuilder(
        builder: TournamentBuilder,
        saveStore: Boolean
    ):  Pair<Boolean,String>
    {
        val tournamentBuilderStore = getTournamentBuilderStore() ?: return Pair(true,FAILED_TO_GET_STORE)
        return addInstance(tournamentBuilderStore,builder,saveStore)
    }

    /**
     * [tournament], [Tournament], [TournamentStore]
     *
     *      if successfully added [tournament] to an empty [TournamentStore] position ->
     *          returns null
     *      if successfully added [tournament] to an occupied [TournamentStore] position ->
     *          returns the replaced [Tournament]
     *      if failed to add b/c [TournamentStore] was null or store was full ->
     *          * returns [tournament] parameter
     *
     *      * ( message will specify if store was null or store was full )
     */
    fun addTournament(
        tournament: Tournament,
        saveStore: Boolean
    ):  Pair<Boolean,String>
    {
        val tournamentStore = getTournamentStore() ?: return Pair(false,FAILED_TO_GET_STORE)
        return addInstance(tournamentStore,tournament,saveStore)
    }

    /**
     * [round], [TournamentRound], [RoundStore]
     *
     *      - if successfully added [round] to an empty [RoundStore] position ->
     *          returns null
     *      - if successfully added [round] to an occupied [RoundStore] position ->
     *          returns the replaced [TournamentRound]
     *      - if failed to add b/c [RoundStore] was null or store was full ->
     *          * returns [round] parameter
     *
     *      * - ( message will specify if store was null or store was full )
     */
    fun addRound(
        round: TournamentRound,
        saveStore: Boolean
    ):  Pair<Boolean,String>
    {
        val roundStore = getRoundStore(round.tournamentID)?: return Pair(false,FAILED_TO_GET_STORE)
        return addInstance(roundStore,round,saveStore)
    }

    /**
     * [match], [TournamentMatch], [MatchStore]
     *
     *      if successfully added [match] to an empty [MatchStore] position ->
     *          returns null
     *      if successfully added [match] to an occupied [MatchStore] position ->
     *          returns the replaced [TournamentMatch]
     *      if failed to add b/c [MatchStore] was null or store was full ->
     *          * returns [match] parameter
     *
     *      * - ( message will specify if store was null or store was full )
    */
    fun addMatch(
        match: TournamentMatch,
        saveStore: Boolean
    ): Pair<Boolean,String>
    {
        val matchStore = getMatchStore(match.tournamentID)?: return Pair(false,FAILED_TO_GET_STORE)
        return addInstance(matchStore,match,saveStore)
    }

    /**
     * [player], [TournamentPlayer], [PlayerStore]
     *
     *      if successfully added [player] to an empty [PlayerStore] position ->
     *          returns null
     *      if successfully added [player] to an occupied [PlayerStore] position ->
     *          returns the replaced [TournamentPlayer]
     *      if failed to add b/c [PlayerStore] was null or store was full ->
     *          * returns [player] parameter
     *
     *      * - ( message will specify if store was null or store was full )
     */
    fun addPlayer(
        player: TournamentPlayer,
        saveStore: Boolean
    ):  Pair<Boolean,String>
    {
        val playerStore = getPlayerStore(player.tournamentID)?: return Pair(false,FAILED_TO_GET_STORE)
        return addInstance(playerStore,player,saveStore)
    }

    private fun <C: ClassStored> addInstance(
        store: DefaultStore<C>,
        value: C,
        saveStore: Boolean
    ): Pair<Boolean,String>
    {
        val position = UuidPosition(value.uuid)
        if (!store.isValidPosition(position)){
            return Pair(false,INVALID_POSITION)
        }
        val success = store.add(value)
        return if (success) {
            if (saveStore) {
                factory().save(store)
            } else {
                factory().markStoreDirty(store::class.java,store.storeID)
            }
            Pair(true,"")
        } else {
            Pair(false,FAILED_INSIDE_STORE)
        }
    }

    fun builderStoreContainsName(
        name: String
    ) : Boolean
    {
        val store = getTournamentBuilderStore()?: return false
        for (b in store.iterator()) {
            if (b.name == name) {
                return true
            }
        }
        return false
    }

    /**
     * [TournamentBuilder], [name], [String]
     *
     *      if [TournamentBuilder] with [name] exists
     *          returns builder & empty [String]
     *      if [TournamentBuilder] with [name] exists
     *          returns null & [String] with explanation
     */
    fun getTournamentBuilderByName(
        name: String
    ) : Pair<TournamentBuilder?,String>
    {
        val store = getTournamentBuilderStore()?: return Pair(null,FAILED_TO_GET_STORE)
        for (b in store.iterator()) {
            if (b.name == name) {
                return Pair(b,"")
            }
        }
        return Pair(null, NO_INSTANCE_WITH_NAME)
    }

    /**
     * [TournamentBuilder], [builderID], [MutablePlayerProperties], [String]
     *
     * [TournamentBuilder.seededPlayers], [TournamentBuilder.unseededPlayers]
     *
     *      if [TournamentBuilder] with [builderID] exists
     *          returns [Set] with all [MutablePlayerProperties] names & empty [String]
     *      if [TournamentBuilder] with [name] exists
     *          returns empty [Set] & [String] with explanation
     */
    fun getTournamentBuilderPlayerNames(
        builderID: UUID
    ) : Pair<Set<String>?,String>
    {
        val builder = getTournamentBuilder(builderID)?: return Pair(null,FAILED_TO_GET_STORE)
        val set = builder.getPlayerNames()
        return if (set.isNotEmpty()) {
            Pair(set,"")
        } else {
            return Pair(null, NO_INSTANCE_WITH_NAME)
        }
    }

    /**
     * [Tournament], [name], [String]
     *
     *      if [Tournament] with [name] exists
     *          returns builder & empty [String]
     *      if [Tournament] with [name] exists
     *          returns null & [String] with explanation
     */
    fun getTournamentByName(
        name: String
    ) : Pair<Tournament?,String>
    {
        val store = getTournamentStore()?: return Pair(null,FAILED_TO_GET_STORE)
        for (b in store.iterator()) {
            if (b.name == name) {
                return Pair(b,"")
            }
        }
        return Pair(null, NO_INSTANCE_WITH_NAME)
    }

}
