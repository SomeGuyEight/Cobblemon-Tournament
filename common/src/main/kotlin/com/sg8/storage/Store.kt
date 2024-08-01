package com.sg8.storage

// From the Cobblemon Mod this script is based on
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
 * package com.cobblemon.mod.common.api.storage
 * abstract class PokemonStore<T: StorePosition> : Iterable<Pokemon>
 */

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.sg8.storage.factory.StoreFactory
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

// Eight's implementation
abstract class Store<P: StorePosition, T: TypeStored> (
    uuid: UUID = UUID.randomUUID()
) : Iterable<T> {

    var uuid: UUID = uuid
        protected set

    /**
     * &#9888; IMPORTANT: ALWAYS iterate & set each [TypeStored.storeCoordinates] to this store &#9888;
     *
     * &#9888; If not done, data will d-sync &#9888;
     *
     * Runs initialization logic for this [Store],
     * knowing that it has just been constructed in a [StoreFactory].
     */
    abstract fun initialize()

    abstract fun instanceNames(): NameSet

    /** Returns true if the given position is pointing to a legitimate location in this store. */
    abstract fun isValidPosition(position: P): Boolean

    abstract fun getFirstAvailablePosition(): P?

    /**
     * Adds the given [TypeStored] to the first available space.
     * Returns false if there is no space.
     */
    abstract fun add(instance: T): Boolean

    /**
     * Sets the specified position to the specified [TypeStored].
     * If there is already a [TypeStored] in that slot,
     * it will be removed from the store entirely.
     *
     * This method will also notify any observing players about the changes.
     */
    abstract operator fun set(position: P, instance: T)

    /**
     * Sets the given position with the given [TypeStored], which can be null.
     * This is for internal use only because other, more public methods will
     * additionally send updates to the client, and for logical reasons this means
     * there must be an internal and external set method.
     */
    protected abstract fun setAtPosition(position: P, instance: T?)

    abstract operator fun get(position: StorePosition): T?

    operator fun get(UUID: UUID) = find { it.uuid == UUID }

    /**
     * Swaps the [TypeStored] at the specified positions.
     * If one of the spaces is empty, it will simply move the not-null one to that space.
     */
    open fun swap(positionOne: P, positionTwo: P) {
        val instanceOne: T? = get(position = positionOne)
        val instanceTwo: T? = get(position = positionTwo)
        setAtPosition(position = positionOne, instance = instanceTwo)
        setAtPosition(position = positionTwo, instance = instanceOne)
        instanceOne?.storeCoordinates?.set(StoreCoordinates(store = this, position = positionTwo))
        instanceTwo?.storeCoordinates?.set(StoreCoordinates(store = this, position = positionOne))
    }

    /**
     * Moves the specified [TypeStored] to the specified space.
     * This will do nothing if the [TypeStored] is not in this store.
     *
     * This is a shortcut to running [Store.swap]
     */
    fun move(instance: T, position: P) {
        val currentPosition = instance.storeCoordinates.get() ?: return
        if (currentPosition.store != this) {
            return
        }
        @Suppress(("UNCHECKED_CAST")) // 'safe'
        swap(positionOne = currentPosition.position as P, positionTwo = position)
    }

    /**
     * Removes any [TypeStored] that may be at the specified spot.
     * @return `true` if there was a [TypeStored] removed.
     */
    open fun remove(position: P): Boolean {
        val instance = get(position = position)
        return instance?.let { remove(instance = instance) } ?: false
    }

    /**
     * Removes the specified [TypeStored] from this store.
     * @return `true` if the [TypeStored] was in this store && was successfully removed.
     */
    open fun remove(instance: T): Boolean {
        val currentPosition = instance.storeCoordinates.get() ?: return false
        if (currentPosition.store != this) {
            return false
        }
        if (get(currentPosition.position) != instance) {
            return false
        }
        instance.storeCoordinates.set(newValue = null)
        @Suppress(("UNCHECKED_CAST")) // 'safe'
        setAtPosition(position = currentPosition.position as P, instance = null)
        return true
    }

    /**
     * Sends the contents of this [Store] to a player as if they've never seen it before.
     * This initializes the store then sends each contained Pokémon.
     */
    abstract fun sendTo(player: ServerPlayer)

    /**
     * Gets an iterable of all [ServerPlayer] that should be
     * notified of any changes in this store.
     */
    abstract fun getObservingPlayers(): Iterable<ServerPlayer>

    /** Sends the given packet to all observing players. */
    open fun sendPacketToObservers(packet: NetworkPacket<*>) {
        getObservingPlayers().forEach { it.sendPacket(packet) }
    }

    /**
     * Returns an [Observable] that emits Unit whenever there is a change to this store.
     * This includes any save-worthy
     * change to a [TypeStored] contained in the store.
     * You can access an Observable in each ClassStored that emits Unit for
     * each change, accessed by [TypeStored.getObservable].
     */
    abstract fun getStoreChangeObservable(): SimpleObservable<AnyStore>

    /**
     * &#9888; ALWAYS call [Store.initialize] when loading.
     * If not done, data will d-sync
     */
    abstract fun loadFromNbt(nbt: CompoundTag): Store<P, T>

    /**
     * &#9888; ALWAYS call [Store.initialize] when loading.
     * If not done, data will d-sync
     */
    abstract fun loadFromJson(json: JsonObject): Store<P, T>

    abstract fun saveToNbt(nbt: CompoundTag): CompoundTag

    abstract fun saveToJson(json: JsonObject): JsonObject

}
