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
import com.cobblemon.mod.common.util.subscribeOnServer
import com.cobblemontournament.common.CobblemonTournament.LOGGER
import com.cobblemontournament.common.config.TournamentConfig
import com.someguy.storage.store.Store
import com.someguy.storage.position.StorePosition
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.adapter.SerializedStore
import com.someguy.storage.adapter.flatfile.FileStoreAdapter
import dev.architectury.event.events.common.TickEvent
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A [StoreFactory] that is backed by a file. This implementation will now handle persistence and scheduling
 * for saving, as well as simple map cache.
 */
// Eight's implementation
open class FileBackedStoreFactory<Ser> (
    private val adapter: FileStoreAdapter<Ser>,
    private val createIfMissing: Boolean
): StoreFactory
{
    private val saveIntervalTicks = TournamentConfig.saveIntervalSeconds() * 20
    private var passedTicks = 0

    @Suppress("unused")
    private val saveSubscription = TickEvent.Server.SERVER_PRE.register {
        passedTicks++
        if ( passedTicks > saveIntervalTicks ) {
            saveAll()
            passedTicks = 0
        }
    }

    private var saveExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val storeCaches = mutableMapOf <Class<out Store<*,*>>, StoreCache <*,*,*>>()
    private val dirtyStores = mutableSetOf <Store<*,*>>()

    protected inner class StoreCache <P: StorePosition,C: ClassStored,St: Store<P,C>> {
        val cacheMap = mutableMapOf <UUID,St>()
    }

    private fun <P: StorePosition,C: ClassStored> isCached(
        store: Store<P,C>
    ): Boolean {
        return storeCaches[store::class.java]?.cacheMap?.containsKey( store.storeID ) == true
    }

    private fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getStoreCache(
        storeClass: Class<out St>
    ): StoreCache<P,C,St>
    {
        val cache: StoreCache<*,*,*> = storeCaches.getOrPut( storeClass ) {
            StoreCache<P,C,St>()
        }
        // 'safe' unchecked cast b/c types confirmed by type parameters & getOrPut
        @Suppress("UNCHECKED_CAST")
        return cache as StoreCache<P,C,St>
    }

    override fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getStore(
        storeClass: Class<St>,
        storeID: UUID
    ): St? {
        return getStoreInner( storeClass, storeID )
    }

    private fun <P: StorePosition,C: ClassStored,St: Store<P,C>> getStoreInner(
        storeClass: Class<out St>,
        storeID: UUID,
        constructor: ( ( UUID ) -> St ) = {
            storeClass.getConstructor( UUID::class.java ).newInstance( it )
        }
    ): St?
    {
        val cache = getStoreCache( storeClass )
        val cached = cache.cacheMap[storeID]
        if (cached != null) {
            return cached
        } else {
            val loaded = adapter.load( storeClass, storeID )
                ?: run {
                    if ( createIfMissing ) {
                        return@run constructor( storeID )
                    } else {
                        return@run null
                    }
                }
                ?: return null
            loaded.initialize()
            track(loaded)
            cache.cacheMap[storeID] = loaded
            return loaded
        }
    }

    private fun track(store: Store <*,*> ) {
        store.getAnyChangeObservable()
            .pipe( emitWhile { isCached( store ) } )
            .subscribeOnServer { dirtyStores.add( store ) }
    }

    fun save( store: Store <*,*> )
    {
        val serialized = SerializedStore(
            storeClass      = store::class.java,
            uuid            = store.storeID,
            serializedForm  = adapter.serialize( store ) )
        saveExecutor.submit {
            adapter.save(
                storeClass  = serialized.storeClass,
                uuid        = serialized.uuid,
                serialized  = serialized.serializedForm )
        }
        dirtyStores.remove( store )
    }

    private fun saveAll()
    {
        LOGGER.debug( "Serializing ${ dirtyStores.size } stores." )
        val serializedStores = dirtyStores.map {
            SerializedStore(
                storeClass      = it.javaClass,
                uuid            = it.storeID,
                serializedForm  = adapter.serialize( it ) )
        }
        dirtyStores.clear()

        LOGGER.debug( "Queueing save." )
        saveExecutor.submit {
            serializedStores.forEach {
                adapter.save(
                    storeClass  = it.storeClass,
                    uuid        = it.uuid,
                    serialized  = it.serializedForm )
            }
            LOGGER.debug( "Saved ${ serializedStores.size } stores." )
        }
    }

    override fun shutdown() {
        saveAll()
        saveExecutor.shutdown()
    }

}
