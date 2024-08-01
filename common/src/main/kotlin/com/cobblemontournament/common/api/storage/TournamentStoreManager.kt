package com.cobblemontournament.common.api.storage

import com.cobblemon.mod.common.api.Priority
import com.cobblemontournament.common.CobblemonTournament
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sg8.storage.*
import com.sg8.storage.adapter.flatfile.NbtStoreAdapter
import com.sg8.storage.factory.FileBackedStoreFactory
import com.sg8.storage.util.*
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import java.io.File
import java.nio.file.Path
import java.util.UUID

object TournamentStoreManager : StoreManager() {

    private var savePath: Path? = null

    private val GSON: Gson =
        GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    val ACTIVE_STORE_ID: UUID =
        StoreUtil.getUuidKey(savePath(), keyDir(), "store-keys", ACTIVE_STORE_KEY, GSON)

    val INACTIVE_STORE_ID: UUID =
        StoreUtil.getUuidKey(savePath(), keyDir(),"store-keys", INACTIVE_STORE_KEY, GSON)

    val server: MinecraftServer? get() = CobblemonTournament.server

    private fun keyDir(): File {
        return StoreUtil.getFile(savePath(), dirName = "tournament", subDirName = "keys")
    }

    private fun storeDir(): File {
        return StoreUtil.getFile(savePath(), dirName = "tournament", subDirName = "stores")
    }

    private fun savePath(): Path {
        return savePath ?: try {
            savePath = server?.getWorldPath(LevelResource.ROOT)
            savePath!!
        } catch (e: Exception) {
            CobblemonTournament.LOGGER.error(e.message)
            e.printStackTrace()
            throw NullPointerException("SavePath & Server were null.")
        }
    }

    fun initialize() {
        val adapter = NbtStoreAdapter(
            rootFolder = storeDir().toString(),
            useNestedFolders = true,
            folderPerClass = true,
        )
        registerFactory(Priority.NORMAL, FileBackedStoreFactory(adapter, createIfMissing = true))
    }

    fun <P : StorePosition, T : TypeStored, S : Store<P, T>> addInstance(
        storeClass: Class<out S>,
        storeID: UUID,
        instance: T,
    ): Boolean {
        val store = getStore(storeClass, storeID) ?: return false
        return addInstance(store, instance)
    }

//    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> addInstance(
//        storeID: UUID,
//        instance: T,
//    ): Boolean {
//        val store = getStore<P, T, S>(storeID) ?: return false
//        return addInstance(store, instance)
//    }

    fun <P : StorePosition, T : TypeStored, S : Store<P, T>> addInstance(
        store: S,
        instance: T,
    ): Boolean {
        return store.add(instance)
    }

    fun <P : StorePosition, T : TypeStored, S : Store<P, T>> getStoreIterator(
        storeClass: Class<out S>,
        storeID: UUID,
    ): Iterator<T> {
        val store = getStore(storeClass, storeID) ?: return emptySequence<T>().iterator()
        return store.iterator()
    }

//    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> getStoreIterator(
//        storeID: UUID,
//    ): Iterator<T> {
//        val store = getStore<P, T, S>(storeID) ?: return emptySequence<T>().iterator()
//        return store.iterator()
//    }

    fun <P : StorePosition, T : TypeStored, S : Store<P, T>> getInstance(
        storeClass: Class<out S>,
        storeID: UUID,
        instanceID: UUID,
    ): T? {
        val store = getStore(storeClass, storeID) ?: return null
        return store[instanceID]
    }

//    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> getInstance(
//        storeID: UUID,
//        instanceID: UUID,
//    ): T? {
//        val store = getStore<P, T, S>(storeID) ?: return null
//        return store[instanceID]
//    }

    fun <P : StorePosition, T : TypeStored, S : Store<P, T>> getInstances(
        storeClass: Class<out S>,
        storeID: UUID,
        predicate: (T) -> Boolean = { true },
    ): Set<T> {
        val instances = mutableSetOf<T>()
        getStore(storeClass, storeID)?.let { store ->
            store.forEach { if (predicate(it)) instances.add(it) }
        }
        return instances
    }

//    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> getInstances(
//        storeID: UUID,
//        predicate: (T) -> Boolean = { true },
//    ): Set<T> {
//        val instances = mutableSetOf<T>()
//        getStore<P, T, S>(storeID)?.let { store ->
//            store.forEach { if (predicate(it)) instances.add(it) }
//        }
//        return instances
//    }

    fun <P : StorePosition, T : TypeStored, S : Store<P, T>> getInstanceByName(
        storeClass: Class<out S>,
        name: String,
        storeID: UUID,
    ): T? {
        for (instance in getStoreIterator(storeClass, storeID)) {
            if (instance.name == name) {
                return instance
            }
        }
        return null
    }

//    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> getInstanceByName(
//        name: String,
//        storeID: UUID,
//    ): T? {
//        for (instance in getStoreIterator<P, T, S>(storeID)) {
//            if (instance.name == name) {
//                return instance
//            }
//        }
//        return null
//    }

    fun <P : StorePosition, T : TypeStored, S : Store<P, T>> getInstanceNames(
        storeClass: Class<out S>,
        storeID: UUID,
        predicate: (T) -> Boolean = { true },
    ): NameSet {
        return getValuesFromInstances(
            storeClass = storeClass,
            storeID = storeID,
            predicate = predicate,
            handler = { instance -> instance.name },
        )
    }

//    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> getInstanceNames(
//        storeID: UUID,
//        predicate: (T) -> Boolean = { true },
//    ): NameSet {
//        return getValuesFromInstances<String, P, T, S>(
//            storeID = storeID,
//            predicate = predicate,
//            handler = { instance -> instance.name },
//        )
//    }

    fun <V, P : StorePosition, T : TypeStored, S : Store<P, T>> getValuesFromInstances(
        storeClass: Class<out S>,
        storeID: UUID,
        predicate: (T) -> Boolean = { true },
        handler: (T) -> V,
    ): Set<V> {
        val set = mutableSetOf<V>()
        for (instance in getStoreIterator(storeClass, storeID)) {
            if (predicate(instance)) {
                val value = handler.invoke(instance) ?: continue
                set.add(value)
            }
        }
        return set
    }

//    inline fun <V, P : StorePosition, T : TypeStored, reified S : Store<P, T>>
//            getValuesFromInstances(
//        storeID: UUID,
//        predicate: (T) -> Boolean = { true },
//        handler: (T) -> V,
//    ): Set<V> {
//        val set = mutableSetOf<V>()
//        for (instance in getStoreIterator<P, T, S>(storeID)) {
//            if (predicate(instance)) {
//                val value = handler.invoke(instance) ?: continue
//                set.add(value)
//            }
//        }
//        return set
//    }

    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> transferInstance(
        storeClass: Class<out S>,
        instance: T,
        currentStoreID: UUID,
        newStoreID: UUID,
        transferPredicate: (T) -> Boolean = { true },
    ): Boolean {
        return if (transferPredicate.invoke(instance)) {
            getStore(storeClass, currentStoreID)?.remove(instance)
            getStore(storeClass, newStoreID)?.add(instance) ?: false
        } else {
            false
        }
    }

//    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> transferInstance(
//        instance: T,
//        currentStoreID: UUID,
//        newStoreID: UUID,
//        transferPredicate: (T) -> Boolean = { true },
//    ): Boolean {
//        return if (transferPredicate.invoke(instance)) {
//            getStore<P, T, S>(uuid = currentStoreID)?.remove(instance)
//            getStore<P, T, S>(uuid = newStoreID)?.add(instance) ?: false
//        } else {
//            false
//        }
//    }

    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> deleteInstance(
        storeClass: Class<out S>,
        storeID: UUID,
        instance: T,
    ): Boolean {
        return getStore(storeClass, storeID)?.remove(instance) == true
    }

//    inline fun <P : StorePosition, T : TypeStored, reified S : Store<P, T>> deleteInstance(
//        storeID: UUID,
//        instance: T,
//    ): Boolean {
//        return getStore<P, T, S>(storeID)?.remove(instance) == true
//    }

}
