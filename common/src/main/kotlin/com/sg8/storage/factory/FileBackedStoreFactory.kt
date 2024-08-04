package com.sg8.storage.factory

/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can get one at https://mozilla.org/MPL/2.0/.
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
import com.cobblemon.mod.common.util.subscribeOnServer
import com.cobblemontournament.common.CobblemonTournament.LOGGER
import com.cobblemontournament.common.config.TournamentConfig
import com.sg8.storage.AnyStore
import com.sg8.storage.Store
import com.sg8.storage.StorePosition
import com.sg8.storage.TypeStored
import com.sg8.storage.adapter.SerializedStore
import com.sg8.storage.adapter.flatfile.FileStoreAdapter
import dev.architectury.event.events.common.TickEvent
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private typealias StoreSet = MutableSet<AnyStore>
private typealias StoreCacheMap = MutableMap<Class<out AnyStore>, StoreCache<*, *, *>>

private data class StoreCache <P : StorePosition, T : TypeStored, S : Store<P, T>> (
    val cacheMap: MutableMap<UUID, S> = mutableMapOf()
)

/**
 * A [StoreFactory] that is backed by a file. This implementation will now handle
 * persistence and scheduling for saving, as well as simple map cache.
 */
// Eight's implementation
open class FileBackedStoreFactory<Ser> (
    private val adapter: FileStoreAdapter<Ser>,
    private val createIfMissing: Boolean,
): StoreFactory {

    private var saveExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val storeCaches: StoreCacheMap = mutableMapOf()
    private val dirtyStores: StoreSet = mutableSetOf()
    private val saveIntervalTicks = (TournamentConfig.saveIntervalSeconds() * 20)
    private var passedTicks = 0

    init {
        TickEvent.Server.SERVER_PRE.register { _ ->
            passedTicks++
            if (passedTicks > saveIntervalTicks) {
                saveAll()
                passedTicks = 0
            }
        }
    }

    private fun <P : StorePosition, T : TypeStored> isCached(store: Store<P, T>): Boolean {
        return storeCaches[store::class.java]
            ?.cacheMap
            ?.containsKey(store.uuid) == true
    }

    private fun <P : StorePosition, T : TypeStored, S : Store<P, T>> getStoreCache(
        storeClass: Class<out S>,
    ): StoreCache<P, T, S> {
        val cache = storeCaches.getOrPut(storeClass) { StoreCache<P, T, S>() }
        // 'safe' unchecked cast b/c types confirmed by type parameters & getOrPut
        @Suppress("UNCHECKED_CAST")
        return cache as StoreCache<P, T, S>
    }

    override fun <P : StorePosition, T : TypeStored,S : Store<P, T>> getStore(
        storeClass: Class<out S>,
        storeID: UUID,
    ): S? {
        return getStoreInner(storeClass = storeClass,storeID = storeID)
    }

    private fun <P : StorePosition, T : TypeStored,S : Store<P, T>> getStoreInner(
        storeClass: Class<out S>,
        storeID: UUID,
        constructor: (UUID) -> S = { storeClass.getConstructor(UUID::class.java).newInstance(it) },
    ): S? {
        val cache = getStoreCache(storeClass = storeClass)
        val cached = cache.cacheMap[storeID]
        if (cached != null) {
            return cached
        } else {
            val loaded = adapter
                .load(storeClass = storeClass,uuid = storeID)
                ?: if (createIfMissing) constructor(storeID) else null
                ?: return null

            loaded.initialize()
            track(store = loaded)
            cache.cacheMap[storeID] = loaded
            return loaded
        }
    }

    private fun track(store: AnyStore) {
        store.getStoreChangeObservable()
            .pipe(transform = emitWhile { isCached(store = store) })
            .subscribeOnServer { dirtyStores.add(store) }
    }

    fun save(store: AnyStore) {
        val serialized = SerializedStore(
            storeClass = store::class.java,
            uuid = store.uuid,
            serializedForm = adapter.serialize(store = store),
        )
        saveExecutor.submit {
            adapter.save(
                storeClass = serialized.storeClass,
                uuid = serialized.uuid,
                serialized = serialized.serializedForm,
            )
        }
        dirtyStores.remove(store)
    }

    private fun saveAll() {
        LOGGER.debug("Serializing ${dirtyStores.size} stores.")
        val serializedStores = dirtyStores.map { store ->
            SerializedStore(
                storeClass = store.javaClass,
                uuid = store.uuid,
                serializedForm = adapter.serialize(store = store),
            )
        }
        dirtyStores.clear()

        LOGGER.debug("Queueing save.")
        saveExecutor.submit {
            serializedStores.forEach { store ->
                adapter.save(
                    storeClass = store.storeClass,
                    uuid = store.uuid,
                    serialized = store.serializedForm,
                )
            }
            LOGGER.debug("Saved ${serializedStores.size} stores.")
        }
    }

    override fun shutdown() {
        saveAll()
        saveExecutor.shutdown()
    }

}
