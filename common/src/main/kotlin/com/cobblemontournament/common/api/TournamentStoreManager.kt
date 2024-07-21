package com.cobblemontournament.common.api

import com.cobblemon.mod.common.api.Priority
import com.cobblemontournament.common.api.storage.DataKeys
import com.google.gson.GsonBuilder
import com.someguy.storage.StoreManager
import com.someguy.storage.adapter.flatfile.NBTStoreAdapter
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.factory.FileBackedStoreFactory
import com.someguy.storage.position.StorePosition
import com.someguy.storage.store.DefaultStore
import com.someguy.storage.store.Store
import com.someguy.storage.store.StoreUtil.getFile
import com.someguy.storage.store.StoreUtil.getUuidKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import java.io.File
import java.nio.file.Path
import java.util.UUID

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

    fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getInstance(
        storeClass  : Class<St>,
        storeID     : UUID,
        instanceID  : UUID
    ): C? {
        val store =  getStore( storeClass, storeID ) ?: return null
        return store[instanceID]
    }

    fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getStoreIterator(
        storeClass      : Class<out St>,
        storeID         : UUID
    ): Iterator <C> {
        val store =  getStore( storeClass, storeID ) ?: return emptySequence <C>().iterator()
        return store.iterator()
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
    fun <P: StorePosition,C: ClassStored,St: Store<P,C>> addInstance(
        storeClass  : Class<St>,
        storeID     : UUID,
        instance    : C
    ):  Pair <Boolean,String> {
        val store = getStore( storeClass, storeID ) ?: return Pair( true, FAILED_TO_GET_STORE )
        return addInstance( store, instance )
    }

    private fun <P: StorePosition,C: ClassStored,St: Store<P,C>> addInstance(
        store: St,
        value: C
    ): Pair <Boolean,String> {
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
    fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getInstanceByName(
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

    fun <P: StorePosition,C: ClassStored,St: Store<P,C>> transferInstance(
        storeClass  : Class<St>,
        instance    : C,
        storeID     : UUID,
        newStoreID  : UUID,
        predicate   : ( C ) -> Boolean = { _ -> true }
    ): Boolean
    {
        if ( !predicate( instance ) ) {
            return false // TODO log?
        }
        getStore( storeClass, storeID ) ?.remove( instance )
        val inactiveStore = getStore( storeClass, newStoreID )
            ?: return false // TODO log?
        return inactiveStore.add( instance )
    }

    fun <P: StorePosition,C: ClassStored,St: Store<P,C>> deleteInstance(
        storeClass  : Class<St>,
        storeID     : UUID,
        instance    : C
    ): Boolean {
        return getStore( storeClass, storeID ) ?.remove( instance ) == true
    }

    /**
     * This is meant to filter [ClassStored] by any [predicate] input.
     *
     *      if the predicate.invoke( instance: C ) == true
     *          the [action] will be performed on the instance to get the value of [T]
     *          * Note: If the output value is null -> it will not be added to the set.
     *          *       It is safe to assert not null on the returned [Set] & it's values
     *      else the instance is skipped
     *
     * @param T The class of the [action] output & class contained in the returned [Set].
     * @param action The block of code performed on each [C] instance
     * @return Set of [T]
     */
    fun <T, P: StorePosition,C: ClassStored,St: Store<P,C>>  getValuesFromStore(
        storeClass  : Class<St>,
        storeID     : UUID,
        predicate   : (C) -> Boolean = { _ -> true },
        action      : (C) -> T,
    ): Set<T>
    {
        val set = mutableSetOf <T>()
        for ( instance in getStoreIterator( storeClass, storeID ) ) {
            if ( predicate( instance ) ) {
                val value = action( instance ) ?: continue
                set.add( value )
            }
        }
        return set
    }

    /**
     * @param storeClass The store class holding the targeted [ClassStored] type
     * @param predicate If not specified predicate will always be true
     * @return [ClassStored] names that pass the [predicate] from the [storeID]
     */
    fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getInstanceNames(
        storeClass  : Class <out St>,
        storeID     : UUID,
        predicate   : ( C ) -> Boolean = { _ -> true }
    ): Set <String>
    {
        return getValuesFromStore(
            storeClass  = storeClass,
            storeID     = storeID,
            predicate   = predicate,
            action      = { instance ->  instance.name } )
    }

    fun <C: ClassStored> getNameWithIndex(
        currentInstance: C
    ): String? {
        val store = currentInstance.storeCoordinates.get()?.store ?: return null
        val currentName = currentInstance.name
        if ( store.firstOrNull { it.name == currentName } != null ) {
            return currentName
        }
        val getNewName: (Int) -> String = { "$currentName ($it)" }
        return run loop@ {
            // just hard coding to 100 for now... TODO ?? is the 'magic' 100 really that bad here ??
            for (index in 0 until 100) {
                val indexedName = getNewName( index )
                if ( store.firstOrNull { it.name == indexedName } != null ) {
                    return@loop indexedName
                }
            }
            return@loop null
        }
    }
}
