package com.cobblemontournament.common.api

import com.cobblemon.mod.common.api.Priority
import com.cobblemontournament.common.api.storage.MatchStore
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.api.storage.PlayerStore
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.api.tournament.TournamentData
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.tournament.TournamentStatus
import com.google.gson.GsonBuilder
import com.someguy.storage.StoreManager
import com.someguy.storage.adapter.flatfile.NBTStoreAdapter
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.factory.FileBackedStoreFactory
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import com.someguy.storage.util.StoreUtil.getFile
import com.someguy.storage.util.StoreUtil.getUuidKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import java.io.File
import java.nio.file.Path
import java.util.UUID

@Suppress("MemberVisibilityCanBePrivate")
object TournamentStoreManager: StoreManager()
{
    private const val ROOT_DIR_NAME         = "tournament"
    private const val STORE_DIR_NAME        = "stores"
    private const val KEY_DIR_NAME          = "keys"
    private const val KEY_FILE_NAME         = "store-keys"
    private const val USE_NESTED_FOLDERS    : Boolean   = true
    private const val FOLDER_PER_CLASS      : Boolean   = true
    private const val CREATE_IF_MISSING     : Boolean   = true

    private const val FAILED_TO_GET_STORE   = "Failed to get store"
    private const val FAILED_INSIDE_STORE   = "Failed inside store"
    private const val NO_INSTANCE_WITH_NAME = "No instance with name"

    var activeStoreKey      : UUID              = UUID.randomUUID()
    var inactiveStoreKey    : UUID              = UUID.randomUUID()
    private var server      : MinecraftServer?  = null
    private var savePath    : Path?             = null

    private fun savePath()      = savePath ?: throw NullPointerException( "SavePath was null" )
    private fun keyDir():File   = getFile( savePath(), ROOT_DIR_NAME, KEY_DIR_NAME)
    private fun storeDir():File = getFile( savePath(), ROOT_DIR_NAME, STORE_DIR_NAME)

    fun initialize( server: MinecraftServer )
    {
        TournamentStoreManager.server = server
        savePath = server.getWorldPath( LevelResource.ROOT )
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        val keyDir = keyDir()
        activeStoreKey = getUuidKey( savePath(), keyDir, KEY_FILE_NAME, DataKeys.ACTIVE_STORE_KEY, gson)
        inactiveStoreKey = getUuidKey( savePath(), keyDir, KEY_FILE_NAME, DataKeys.INACTIVE_STORE_KEY, gson)

        val adapter = NBTStoreAdapter( storeDir().toString(), USE_NESTED_FOLDERS, FOLDER_PER_CLASS)
        registerFactory( Priority.NORMAL, FileBackedStoreFactory( adapter, CREATE_IF_MISSING ) )
    }

    fun getTournamentBuilderStore(
        storeKey: UUID = activeStoreKey,
    ): TournamentBuilderStore? {
        return getStore( TournamentBuilderStore::class.java, uuid = storeKey )
    }

    fun getTournamentStore(
        storeKey: UUID = activeStoreKey
    ): TournamentStore? {
        return getStore( TournamentStore::class.java, uuid = storeKey )
    }

    private fun getMatchStore(
        tournamentID: UUID,
    ): MatchStore? {
        return getStore( MatchStore::class.java, tournamentID )
    }

    private fun getPlayerStore(
        tournamentID: UUID,
    ): PlayerStore? {
        return getStore( PlayerStore::class.java, tournamentID )
    }

    fun getTournamentBuilder(
        builderID: UUID,
    ): TournamentBuilder? {
        return getTournamentBuilderStore()?.get( UuidPosition( builderID ) )
    }

    fun getTournament(
        tournamentID: UUID,
    ): Tournament? {
        return getTournamentStore()?.get( UuidPosition( tournamentID ) )
    }

    fun getMatch(
        tournamentID    : UUID,
        matchID         : UUID,
    ): TournamentMatch? {
        return getMatchStore( tournamentID )?.get( UuidPosition( matchID ) )
    }

    fun getPlayer(
        tournamentID    : UUID,
        playerID        : UUID,
    ): TournamentPlayer? {
        return getPlayerStore( tournamentID )?.get( UuidPosition( playerID ) )
    }

    fun addTournamentData(
        data: TournamentData
    ):  Pair <Boolean,String>
    {
        val result = addTournament( data.tournament )
        if ( !result.first ) {
            return result
        }
        data.matches.forEach { addMatch ( it ) }
        data.players.forEach { addPlayer( it ) }
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
        builder: TournamentBuilder
    ):  Pair <Boolean,String> {
        val tournamentBuilderStore = getTournamentBuilderStore() ?: return Pair( true, FAILED_TO_GET_STORE )
        return addInstance( tournamentBuilderStore, builder )
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
        tournament: Tournament
    ):  Pair <Boolean,String> {
        val tournamentStore = getTournamentStore() ?: return Pair( false, FAILED_TO_GET_STORE )
        return addInstance( tournamentStore, tournament )
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
        match: TournamentMatch
    ): Pair <Boolean,String> {
        val matchStore = getMatchStore( match.tournamentID ) ?: return Pair( false, FAILED_TO_GET_STORE )
        return addInstance( matchStore, match )
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
        player: TournamentPlayer
    ):  Pair <Boolean,String> {
        val playerStore = getPlayerStore( player.tournamentID ) ?: return Pair( false, FAILED_TO_GET_STORE )
        return addInstance( playerStore, player )
    }

    private fun <C: ClassStored> addInstance(
        store: DefaultStore<C>,
        value: C
    ): Pair <Boolean,String>
    {
        return if ( store.add( value ) ) {
            Pair( true, "" )
        } else Pair( false, FAILED_INSIDE_STORE )
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
        name: String,
    ) : Pair <TournamentBuilder?,String>
    {
        val store = getTournamentBuilderStore() ?: return Pair( null, FAILED_TO_GET_STORE )
        for ( b in store.iterator() ) {
            if ( b.name == name ) {
                return Pair( b, "" )
            }
        }
        return Pair( null, NO_INSTANCE_WITH_NAME )
    }

    /**
     * [TournamentBuilder], [storeID], [playerID]
     *
     *      if [storeID] != null
     *          returns builder names from the storeID
     *      if [storeID] == null
     *          returns builder names from the server store key
     *      if [playerID] != null
     *          returns only builders with the playerID registered
     *      if [playerID] == null
     *          returns all builder names
     */
    fun getTournamentBuilderNames(
        storeID     : UUID = activeStoreKey,
        playerID    : UUID? = null,
    ): Set <String>
    {
        val names = mutableSetOf <String>()
        val store = getTournamentBuilderStore( storeID ) ?: return names
        for ( instance in store.iterator() ) {
            if ( playerID != null && !instance.containsPlayerID( playerID ) ) {
                continue
            }
            names.add( instance.name )
        }
        return names
    }

    /**
     * [TournamentBuilder], [storeID], [playerID]
     *
     *      if [storeID] != null
     *          returns tournament names from the storeID
     *      if [storeID] == null
     *          returns tournament names from the server store key
     *      if [playerID] != null
     *          returns only tournaments with the playerID registered
     *      if [playerID] == null
     *          returns all tournament names
     */
    fun getTournamentNames(
        storeID     : UUID = activeStoreKey,
        playerID    : UUID? = null,
    ): Set <String>
    {
        val names = mutableSetOf <String>()
        val store = getTournamentStore( storeID ) ?: return names
        if ( playerID != null ) {
            for ( instance in store.iterator() ) {
                if ( instance.containsPlayerID( playerID ) ) {
                    names.add( instance.name )
                }
            }
        } else {
            for ( instance in store.iterator() ) {
                names.add( instance.name )
            }
        }
        return names
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
        name    : String,
        storeID : UUID = activeStoreKey
    ): Pair <Tournament?,String>
    {
        val store = getTournamentStore( storeID ) ?: return Pair( null, FAILED_TO_GET_STORE )
        for ( b in store.iterator() ) {
            if ( b.name == name ) {
                return Pair( b, "" )
            }
        }
        return Pair( null, NO_INSTANCE_WITH_NAME )
    }

    fun deactivateTournament( tournament: Tournament )
    {
        if ( tournament.tournamentStatus != TournamentStatus.FINALIZED ) return // TODO log?
        getTournamentStore(activeStoreKey)?.remove(tournament)
        val inactiveStore = getTournamentStore(activeStoreKey) ?: TODO() // log?
        inactiveStore.add( tournament )
    }

}
