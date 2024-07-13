package com.cobblemontournament.common.api

import com.cobblemon.mod.common.api.Priority
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.api.storage.DataKeys
import com.google.gson.GsonBuilder
import com.someguy.storage.StoreManager
import com.someguy.storage.adapter.flatfile.NBTStoreAdapter
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.factory.FileBackedStoreFactory
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

    fun <C: ClassStored,St: DefaultStore<C>> getInstance(
        storeClass  : Class<St>,
        storeID     : UUID,
        instanceID  : UUID
    ): C? {
        val store =  getStore( storeClass, storeID ) ?: return null
        return store[instanceID]
    }

    fun <C: ClassStored,St: DefaultStore<C>> getStoreIterator(
        storeClass      : Class<out St>,
        storeID         : UUID
    ): Iterator <C> {
        val store =  getStore( storeClass, storeID ) ?: return emptySequence <C>().iterator()
        return store.iterator()
    }

    /**
     * This is meant to filter [ClassStored] by any [predicate] input.
     * If the predicate == true then the [action] will be performed on the ClassStored instance.
     * If the action output is not null it will be added to the returned set.
     *
     * [T] is whatever type the action outputs.
     *
     *      If the action output is nullable it will still be safe to assert not null.
     *      This function will not add any null values to the Set<T> being returned.
     */
    fun <T, C: ClassStored, St: DefaultStore<C>> getValuesFromStore(
        storeClass: Class<St>,
        storeID: UUID,
        predicate: (C) -> Boolean = { _ -> true },
        action: (C) -> T,
    ): Set<T>
    {
        val set = mutableSetOf<T>()
        for ( instance in getStoreIterator( storeClass, storeID ) ) {
            if ( predicate( instance ) ) {
                val value = action( instance ) ?: continue
                set.add( value )
            }
        }
        return set
    }

    /**
     * [ClassStored], [instance], [DefaultStore]
     *
     *      if successfully added [instance] to an empty [DefaultStore] < [ClassStored] > position ->
     *          returns null
     *      if successfully added instance to an occupied DefaultStore position ->
     *          returns the replaced ClassStored
     *      if failed to add b/c DefaultStore was null or store was full ->
     *          * returns ClassStored parameter
     *
     *      * - ( message will specify if store was null or store was full )
     */
    fun <C: ClassStored,St: DefaultStore<C>> addInstance(
        storeClass  : Class<St>,
        storeID     : UUID,
        instance    : C
    ):  Pair <Boolean,String> {
        val store = getStore( storeClass, storeID ) ?: return Pair( true, FAILED_TO_GET_STORE )
        return addInstance( store, instance )
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
     * [ClassStored], [name], [String], [DefaultStore], [UUID]
     *
     *      if a [DefaultStore] with [storeID] does not exist
     *          or
     *      if [ClassStored] with [name] does not exist in the [DefaultStore] with [storeID]
     *          returns null & [String] with explanation
     *
     *      if [ClassStored] with [name] exists
     *          returns [ClassStored] & empty [String]
     */
    fun <C: ClassStored,St: DefaultStore<C>> getInstanceByName(
        name: String,
        storeClass: Class<St>,
        storeID: UUID
    ) : Pair <C?,String>
    {
        for ( instance in getStoreIterator( storeClass, storeID ) ) {
            if ( instance.name == name ) {
                return Pair( instance, "" )
            }
        }
        return Pair( null, NO_INSTANCE_WITH_NAME )
    }

    fun <C: ClassStored,St: DefaultStore<C>> deactivateInstance(
        storeClass  : Class<St>,
        instance    : C,
        predicate   : (C) -> Boolean = { _ -> true }
    ): Boolean
    {
        if ( !predicate( instance ) ) {
            return false // TODO log?
        }
        getStore( storeClass, activeStoreKey )?.remove( instance )
        val inactiveStore = getStore( storeClass, inactiveStoreKey )
            ?: return false // TODO log?
        return inactiveStore.add( instance )
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
        val containsPlayerID: ( TournamentBuilder ) -> Boolean = if ( playerID != null ) {
            { instance ->  instance.containsPlayerID( playerID ) }
        } else { _ -> true }

        return getValuesFromStore(
            storeClass  = TournamentBuilderStore::class.java,
            storeID     = storeID,
            predicate   = containsPlayerID,
            action      = { instance ->  instance.name } )
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
        val containsPlayerID: ( Tournament ) -> Boolean = if ( playerID != null ) {
            { instance ->  instance.containsPlayerID( playerID ) }
        } else { _ -> true }

        return getValuesFromStore(
            storeClass  = TournamentStore::class.java,
            storeID     = storeID,
            predicate   = containsPlayerID,
            action      = { instance ->  instance.name } )
    }

}
