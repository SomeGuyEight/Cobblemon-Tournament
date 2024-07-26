package com.cobblemontournament.common.api.storage

import com.cobblemon.mod.common.api.Priority
import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.util.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.someguy.storage.adapter.flatfile.NbtStoreAdapter
import com.someguy.storage.ClassStored
import com.someguy.storage.factory.FileBackedStoreFactory
import com.someguy.storage.Store
import com.someguy.storage.StoreManager
import com.someguy.storage.StorePosition
import com.someguy.storage.util.*
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import java.io.File
import java.nio.file.Path
import java.util.UUID

object TournamentStoreManager : StoreManager() {

    private var server: MinecraftServer? = null

    private var savePath: Path? = null

    private val GSON: Gson by lazy {
        GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    }

    val ACTIVE_STORE_ID: UUID by lazy {
        StoreUtil.getUuidKey(savePath(), keyDir(), "store-keys", ACTIVE_STORE_KEY, GSON)
    }

    val INACTIVE_STORE_ID: UUID by lazy {
        StoreUtil.getUuidKey(savePath(), keyDir(),"store-keys", INACTIVE_STORE_KEY, GSON)
    }

    private fun keyDir(): File {
        return StoreUtil.getFile(savePath(), dirName = "tournament", subDirName = "keys")
    }

    private fun storeDir(): File {
        return StoreUtil.getFile(savePath(), dirName = "tournament", subDirName = "stores")
    }

    private fun savePath(): Path {
        return savePath ?: try {
            savePath = server!!.getWorldPath(LevelResource.ROOT)
            savePath!!
        } catch (e: Exception) {
            CobblemonTournament.LOGGER.error(e.message)
            e.printStackTrace()
            throw NullPointerException("SavePath was null")
        }
    }

    fun initialize(server: MinecraftServer) {
        TournamentStoreManager.server = server
        val adapter = NbtStoreAdapter(
            rootFolder = storeDir().toString(),
            useNestedFolders = true,
            folderPerClass = true,
        )
        registerFactory(
            Priority.NORMAL,
            FileBackedStoreFactory(adapter, createIfMissing = true),
        )
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> addInstance(
        storeClass: Class<out St>,
        storeID: StoreID,
        instance: C,
    ): Boolean {
        val store = getStore(storeClass, storeID) ?: return false
        return addInstance(store, instance)
    }

    private fun <P : StorePosition, C : ClassStored, St : Store<P, C>> addInstance(
        store: St,
        instance: C,
    ): Boolean {
        return store.add(instance)
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> getStoreIterator(
        storeClass: Class<out St>,
        storeID: StoreID,
    ): Iterator<C> {
        val store = getStore(storeClass, storeID) ?: return emptySequence<C>().iterator()
        return store.iterator()
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> getInstance(
        storeClass: Class<out St>,
        storeID: StoreID,
        instanceID: InstanceID,
    ): C? {
        val store = getStore(storeClass, storeID) ?: return null
        return store[instanceID]
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> getInstanceByName(
        storeClass: Class<out St>,
        name: String,
        storeID: StoreID,
    ): C? {
        for (instance in getStoreIterator(storeClass, storeID)) {
            if (instance.name == name) {
                return instance
            }
        }
        return null
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> getInstanceNames(
        storeClass: Class<out St>,
        storeID: StoreID,
        predicate: (C) -> Boolean = { true },
    ): NameSet {
        return getValuesFromInstances(storeClass, storeID, predicate) { instance -> instance.name }
    }

    fun <T, P : StorePosition, C : ClassStored, St : Store<P, C>>  getValuesFromInstances(
        storeClass: Class<out St>,
        storeID: StoreID,
        predicate: (C) -> Boolean = { true },
        handler: (C) -> T,
    ): Set<T> {
        val set = mutableSetOf<T>()
        for (instance in (getStoreIterator(storeClass, storeID))) {
            if (predicate(instance)) {
                val value = handler.invoke(instance) ?: continue
                set.add(value)
            }
        }
        return set
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> transferInstance(
        storeClass: Class<out St>,
        instance: C,
        storeID: StoreID,
        newStoreID: StoreID,
        transferPredicate: (C) -> Boolean = { true },
    ): Boolean {
        return if (transferPredicate.invoke(instance)) {
            getStore(storeClass, uuid = storeID)?.remove(instance)
            getStore(storeClass, uuid = newStoreID)?.add(instance) ?: false
        } else {
            false
        }
    }

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> deleteInstance(
        storeClass: Class<out St>,
        storeID: StoreID,
        instance: C,
    ): Boolean {
        return getStore(storeClass,storeID)?.remove(instance) == true
    }

}
