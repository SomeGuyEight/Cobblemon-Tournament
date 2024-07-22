package com.cobblemontournament.common.api

import com.cobblemon.mod.common.api.Priority
import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.storage.TournamentDataKeys
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.someguy.storage.StoreManager
import com.someguy.storage.adapter.flatfile.NbtStoreAdapter
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

object TournamentStoreManager : StoreManager() {

    private const val ROOT_DIR_NAME = "tournament"
    private const val KEY_FILE_NAME = "store-keys"

    private var server: MinecraftServer? = null

    private var savePath: Path? = null

    private val GSON: Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()
    }

    val ACTIVE_STORE_ID: UUID by lazy {
        getUuidKey(
            root = savePath(),
            keyDir = keyDir(),
            name = KEY_FILE_NAME,
            key = TournamentDataKeys.ACTIVE_STORE_KEY,
            gson = GSON,
            )
    }

    val INACTIVE_STORE_ID: UUID by lazy {
        getUuidKey(
            root = savePath(),
            keyDir = keyDir(),
            name = KEY_FILE_NAME,
            key = TournamentDataKeys.INACTIVE_STORE_KEY,
            gson = GSON,
            )
    }

    private fun savePath(): Path  {
        return savePath
            ?: try {
                // '!!' should work & trigger exception? TODO confirm
                savePath = server!!.getWorldPath(LevelResource.ROOT)
                savePath!!
            } catch (e: Exception) {
                CobblemonTournament.LOGGER.error(e.message)
                e.printStackTrace()
                throw NullPointerException("SavePath was null")
            }
    }

    private fun keyDir():File {
        return getFile(
            savePath = savePath(),
            dirName = ROOT_DIR_NAME,
            subDirName = "keys",
            )
    }

    private fun storeDir():File {
        return getFile(
            savePath = savePath(),
            dirName = ROOT_DIR_NAME,
            subDirName = "stores",
            )
    }

    fun initialize(server: MinecraftServer) {
        this.server = server
        val adapter = NbtStoreAdapter(
            rootFolder = storeDir().toString(),
            useNestedFolders = true,
            folderPerClass = true,
            )
        registerFactory(
            Priority.NORMAL,
            FileBackedStoreFactory(
                adapter = adapter,
                createIfMissing = true,
                ),
            )
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> getInstance(
        storeClass: Class<out St>,
        storeID: UUID,
        instanceID: UUID,
    ): C? {
        val store = getStore(storeClass, storeID)
            ?: return null
        return store[instanceID]
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> getStoreIterator(
        storeClass: Class<out St>,
        storeID: UUID,
    ): Iterator<C> {
        val store = getStore(storeClass, storeID)
            ?: return emptySequence<C>().iterator()
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
    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> addInstance(
        storeClass: Class<out St>,
        storeID: UUID,
        instance: C,
    ): Pair<Boolean, String> {
        val store = getStore(storeClass, storeID)
            ?: return Pair(false, "Failed to get store")
        return addInstance(store, instance)
    }

    private fun <P : StorePosition, C : ClassStored, St : Store<P, C>> addInstance(
        store: St,
        value: C,
    ): Pair<Boolean, String> {
        return if (store.add( value )) {
            Pair(true, "")
        } else {
            Pair(false, "Failed inside store")
        }
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
    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> getInstanceByName(
        storeClass: Class<out St>,
        name: String,
        storeID: UUID,
    ): Pair<C?, String> {
        for (instance in getStoreIterator(storeClass, storeID)) {
            if (instance.name == name) {
                return Pair(instance, "")
            }
        }
        return Pair(null, "No instance with name")
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> transferInstance(
        storeClass: Class<out St>,
        instance: C,
        storeID: UUID,
        newStoreID: UUID,
        predicate: (C) -> Boolean = { true },
    ): Boolean {
        return if (predicate(instance)) {
            getStore(storeClass, storeID)?.remove(instance)
            val inactiveStore = getStore(storeClass, newStoreID)
                ?: return false // TODO log?
            return inactiveStore.add(instance)
        } else {
            false // TODO log?
        }
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> deleteInstance(
        storeClass: Class<out St>,
        storeID: UUID,
        instance: C,
    ): Boolean {
        return getStore(storeClass, storeID)?.remove(instance) == true
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
    fun <T, P : StorePosition, C : ClassStored, St : Store<P, C>>  getValuesFromStore(
        storeClass: Class<out St>,
        storeID: UUID,
        predicate: (C) -> Boolean = { true },
        action: (C) -> T,
    ): Set<T> {
        val set = mutableSetOf<T>()
        for (instance in getStoreIterator(storeClass, storeID)) {
            if (predicate(instance)) {
                val value = action(instance) ?: continue
                set.add(value)
            }
        }
        return set
    }

    /**
     * @param storeClass The store class holding the targeted [ClassStored] type
     * @param predicate If not specified predicate will always be true
     * @return [ClassStored] names that pass the [predicate] from the [storeID]
     */
    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> getInstanceNames(
        storeClass: Class<out St>,
        storeID: UUID,
        predicate: (C) -> Boolean = { true },
    ): Set<String> {
        return getValuesFromStore(
            storeClass = storeClass,
            storeID = storeID,
            predicate = predicate,
            action = { instance -> instance.name },
            )
    }

    fun <C : ClassStored> getNameWithIndex(currentInstance: C): String? {
        val store = currentInstance.storeCoordinates.get()?.store
            ?: return null
        val currentName = currentInstance.name
        if (store.firstOrNull { it.name == currentName } != null) {
            return currentName
        }
        val getNameAction: (Int) -> String = { "$currentName ($it)" }
        return run loop@ {
            // just hard coding to 100 for now... TODO ?? is the 'magic' 100 really that bad here ??
            for (index in 0 until 100) {
                val indexedName = getNameAction(index)
                if (store.firstOrNull { it.name == indexedName } != null) {
                    return@loop indexedName
                }
            }
            return null
        }
    }

}
