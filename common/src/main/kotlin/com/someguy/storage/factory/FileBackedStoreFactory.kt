package com.someguy.storage.factory

/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * @author Hiroku
 * @since November 29th, 2021
 *
 * package com.cobblemon.mod.common.api.storage.factory
 * open class FileBackedPokemonStoreFactory<S>(
 *     protected val adapter: FileStoreAdapter<S>,
 *     protected val createIfMissing: Boolean,
 *     val partyConstructor: (UUID) -> PlayerPartyStore = { PlayerPartyStore(it) },
 *     val pcConstructor: (UUID) -> PCStore = { PCStore(it) }
 * ) : PokemonStoreFactory
 */

import com.cobblemon.mod.common.api.reactive.Observable.Companion.emitWhile
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.util.subscribeOnServer
import com.cobblemontournament.common.CobblemonTournament.LOGGER
import com.cobblemontournament.common.config.Config
import com.someguy.storage.store.Store
import com.someguy.storage.position.StorePosition
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.adapter.SerializedStore
import com.someguy.storage.adapter.flatfile.FileStoreAdapter
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A [StoreFactory] that is backed by a file. This implementation will now handle persistence and scheduling
 * for saving, as well as simple map cache.
 */
// Eight's implementation
@Suppress("MemberVisibilityCanBePrivate")
open class FileBackedStoreFactory<Ser> (
    protected val adapter: FileStoreAdapter<Ser>,
    protected val createIfMissing: Boolean
): StoreFactory
{
    var passedTicks = 0
    protected val saveSubscription = PlatformEvents.SERVER_TICK_PRE.subscribe {
        passedTicks++
        if (passedTicks > 20 * Config.saveIntervalSeconds()) {
            saveAll()
            passedTicks = 0
        }
    }

    protected var saveExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    protected val storeCaches = mutableMapOf<Class<out Store<*, *>>, StoreCache<*, *, *>>()
    private val dirtyStores = mutableSetOf<Store<*,*>>()

    protected inner class StoreCache<P: StorePosition,C: ClassStored,St: Store<P, C>> {
        val cacheMap = mutableMapOf<UUID,St>()
    }

    fun isCached(
        store: Store<*,*>
    ): Boolean {
        return storeCaches[store::class.java]?.cacheMap?.containsKey(store.storeID) == true
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getStoreCache(
        storeClass: Class<out St>
    ) : StoreCache<P,C,St>
    {
        val cache: StoreCache<*,*,*>  = storeCaches.getOrPut(storeClass) { StoreCache<P,C,St>() }
        // 'safe' unchecked cast b/c types confirmed by type parameters & getOrPut
        return cache as StoreCache<P,C,St>
    }

    override fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getStore(
        storeClass: Class<St>,
        storeID: UUID
    ): St? {
        return getStoreInner(storeClass,storeID)
    }

    protected fun <P: StorePosition,C: ClassStored,St: Store<P, C>> getStoreInner(
        storeClass: Class<St>,
        storeID: UUID,
        constructor: ((UUID) -> St) = { storeClass.getConstructor(UUID::class.java).newInstance(it) }
    ): St?
    {
        val cache = getStoreCache(storeClass).cacheMap
        val cached = cache[storeID]
        if (cached != null) {
            return cached
        } else {
            val loaded = adapter.load(storeClass, storeID)
                ?: run {
                    if (createIfMissing) {
                        return@run constructor(storeID)
                    } else {
                        return@run null
                    }
                }?: return null
            loaded.initialize()
            track(loaded)
            cache[storeID] = loaded
            return loaded
        }
    }

    fun track(
        store: Store<*, *>
    ) {
        store.getAnyChangeObservable()
            .pipe(emitWhile { isCached(store) })
            .subscribeOnServer { dirtyStores.add(store) }
    }

    /**
     * [Store], [storeClass], [storeID], [dirtyStores]
     *
     *      if [storeClass] with [storeID] exists & is successfully added to [dirtyStores]
     *          returns true
     *      else [storeClass] does not exist or failed to add existing store to [dirtyStores]
     *          returns false
     */
    override fun <P: StorePosition,C: ClassStored,St: Store<P,C>> markStoreDirty(
        storeClass: Class<St>,
        storeID: UUID
    ): Boolean
    {
        val store = getStore(storeClass,storeID)
        return if (store != null) {
            dirtyStores.add(store)
        } else false
    }

    fun save(
        store: Store<*,*>
    )
    {
        val serialized = SerializedStore(store::class.java, store.storeID, adapter.serialize(store))
        dirtyStores.remove(store)
        saveExecutor.submit { adapter.save(serialized.storeClass, serialized.uuid, serialized.serializedForm) }
    }

    fun saveAll()
    {
        LOGGER.debug("Serializing ${dirtyStores.size} stores.")
        val serializedStores = dirtyStores.map { SerializedStore(it.javaClass, it.storeID, adapter.serialize(it)) }
        dirtyStores.clear()
        LOGGER.debug("Queueing save.")
        saveExecutor.submit {
            serializedStores.forEach { adapter.save(it.storeClass, it.uuid, it.serializedForm) }
            LOGGER.debug("Saved ${serializedStores.size} stores.")
        }
    }

    override fun shutdown()
    {
        saveSubscription.unsubscribe()
        saveAll()
        saveExecutor.shutdown()
    }

}
