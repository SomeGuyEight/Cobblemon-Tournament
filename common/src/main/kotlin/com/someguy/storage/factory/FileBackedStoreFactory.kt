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
import java.util.concurrent.Executors

/**
 * A [StoreFactory] that is backed by a file. This implementation will now handle persistence and scheduling
 * for saving, as well as simple map cache.
 */
// Eight's implementation
@Suppress("MemberVisibilityCanBePrivate")
open class FileBackedStoreFactory<Ser> (
    protected val adapter: FileStoreAdapter<Ser>,
    protected val createIfMissing: Boolean,
) : StoreFactory {
    var passedTicks = 0
    protected val saveSubscription = PlatformEvents.SERVER_TICK_PRE.subscribe {
        passedTicks++
        if (passedTicks > 20 * Config.saveIntervalSeconds()) {
            saveAll()
            passedTicks = 0
        }
    }

    protected var saveExecutor = Executors.newSingleThreadExecutor()
    protected val storeCaches = mutableMapOf<Class<out Store<*, *>>, StoreCache<*, *, *>>()
    private val dirtyStores = mutableSetOf<Store<*, *>>()

    protected inner class StoreCache<P: StorePosition,C: ClassStored,St: Store<P, C>>
    {
        val cacheMap = mutableMapOf<UUID,St>()
    }

    fun isCached(store: Store<*,*>) = storeCaches[store::class.java]?.cacheMap?.containsKey(store.storeID) == true

    protected fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getStoreCache(storeClass: Class<St>): StoreCache<P,C,St>
    {
        val cache = storeCaches.getOrPut(storeClass) { StoreCache<P,C,St>() }
        // 'safe' unchecked cast b/c types confirmed by type parameters & getOrPut
        @Suppress("UNCHECKED_CAST")
        return cache as StoreCache<P,C,St>
    }

    override fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getStore(
        storeClass: Class<St>,
        uuid: UUID
    ): St? {
        return getStoreInner(storeClass,uuid)
    }

    protected fun <P: StorePosition,C: ClassStored,St: Store<P, C>> getStoreInner(
        storeClass: Class<St>,
        uuid: UUID,
        constructor: ((UUID) -> St) = { storeClass.getConstructor(UUID::class.java).newInstance(it) }
    ): St? {
        val cache = getStoreCache(storeClass).cacheMap
        val cached = cache[uuid]
        if (cached != null) {
            return cached
        } else {
            val loaded = adapter.load(storeClass, uuid)
                ?: run {
                    if (createIfMissing) {
                        return@run constructor(uuid)
                    } else {
                        return@run null
                    }
                }
                ?: return null

            loaded.initialize()
            track(loaded)
            cache[uuid] = loaded
            return loaded
        }
    }

    fun save(store: Store<*,*>)
    {
        val serialized = SerializedStore(store::class.java, store.storeID, adapter.serialize(store))
        dirtyStores.remove(store)
        saveExecutor.submit { adapter.save(serialized.storeClass, serialized.uuid, serialized.serializedForm) }
    }

    fun saveAll()
    {
        LOGGER.debug("Serializing ${dirtyStores.size} stores.")
        val serializedStores = dirtyStores.map { SerializedStore(it::class.java, it.storeID, adapter.serialize(it)) }
        dirtyStores.clear()
        LOGGER.debug("Queueing save.")
        saveExecutor.submit {
            serializedStores.forEach { adapter.save(it.storeClass, it.uuid, it.serializedForm) }
            LOGGER.debug("Saved ${serializedStores.size} stores.")
        }
    }

    fun track(store: Store<*, *>)
    {
        store.getAnyChangeObservable()
            .pipe(emitWhile { isCached(store) })
            .subscribeOnServer { dirtyStores.add(store) }
    }

    override fun shutdown()
    {
        saveSubscription.unsubscribe()
        saveAll()
        saveExecutor.shutdown()
    }

}
