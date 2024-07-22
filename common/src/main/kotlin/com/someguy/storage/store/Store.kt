package com.someguy.storage.store

// From the Cobblemon Mod this script is based on
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
 * package com.cobblemon.mod.common.api.storage
 * abstract class PokemonStore<T : StorePosition> : Iterable<Pokemon>
 */

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.reactive.Observable
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.StorePosition
import com.someguy.storage.factory.StoreFactory
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

// Eight's implementation
abstract class Store<P: StorePosition,C: ClassStored>(id: UUID = UUID.randomUUID()) : Iterable<C>
{
    /** The [UUID] of the store */
    var storeID: UUID = id
        protected set

    /**
     * &#9888; IMPORTANT: ALWAYS iterate & set each [ClassStored.storeCoordinates] to this store &#9888;
     *
     * &#9888; If not done data will d-sync &#9888;
     *
     * Runs initialization logic for this [Store], knowing that it has just been constructed in a [StoreFactory].
     */
    abstract fun initialize()

    abstract fun instanceNames(): Set<String>

    /** Returns true if the given position is pointing to a legitimate location in this store. */
    abstract fun isValidPosition(position: P): Boolean

    abstract fun getFirstAvailablePosition(): P?

    /** Adds the given [ClassStored] to the first available space. Returns false if there is no space. */
    abstract fun add(instance: C): Boolean

    /**
     * Sets the specified position to the specified [ClassStored]. If there is already a [ClassStored] in that slot,
     * it will be removed from the store entirely.
     *
     * This method will also notify any observing players about the changes.
     */
    abstract operator fun set(position: P,instance: C)

    /**
     * Sets the given position with the given [ClassStored], which can be null. This is for internal use only because
     * other, more public methods will additionally send updates to the client, and for logical reasons this means
     * there must be an internal and external set method.
     */
    protected abstract fun setAtPosition(position: P, instance: C?)

    abstract operator fun get(position: StorePosition): C?

    operator fun get(uuid: UUID) = find { it.uuid == uuid }

    /** Swaps the [ClassStored] at the specified positions. If one of the spaces is empty, it will simply move the not-null one to that space. */
    open fun swap(
        position1: P,
        position2: P
    )
    {
        val instance1: C? = get(position1)
        val instance2: C? = get(position2)
        setAtPosition(position1, instance2)
        setAtPosition(position2, instance1)
        instance1?.storeCoordinates?.set(StoreCoordinates(this,position2))
        instance2?.storeCoordinates?.set(StoreCoordinates(this,position1))
    }

    /**
     * Moves the specified [ClassStored] to the specified space. This will do nothing if the [ClassStored] is not in this store.
     *
     * This is a shortcut to running [Store.swap]
     */
    fun move(
        instance: C,
        position: P
    )
    {
        val currentPosition = instance.storeCoordinates.get()?: return
        if (currentPosition.store != this) {
            return
        }

        @Suppress("UNCHECKED_CAST") // 'safe'
        swap(currentPosition.position as P,position)
    }

    /** Removes any [ClassStored] that may be at the specified spot. Returns true if there was a [ClassStored] to remove. */
    open fun remove(
        position: P
    ): Boolean
    {
        val instance = get(position)
        return if (instance == null) {
            false
        } else {
            return remove(instance)
        }
    }

    /** Removes the specified [ClassStored] from this store. Returns true if the [ClassStored] was in this store and was successfully removed. */

    open fun remove(
        instance: C
    ): Boolean
    {
        val currentPosition = instance.storeCoordinates.get() ?: return false
        if (currentPosition.store != this) {
            return false
        }
        if (get(currentPosition.position) != instance) {
            return false
        }
        instance.storeCoordinates.set(null)

        @Suppress("UNCHECKED_CAST") // 'safe'
        setAtPosition(currentPosition.position as P,null)
        return true
    }


    /**
     * Returns an [Observable] that emits Unit whenever there is a change to this store. This includes any save-worthy
     * change to a [ClassStored] contained in the store. You can access an Observable in each ClassStored that emits Unit for
     * each change, accessed by [ClassStored.getChangeObservable].
     */
    // Important. Don't change to unit, b/c it breaks the factory tracking.
    // - It may be b/c emitting void breaks the cast, but I am not exactly sure.
    // - I do know keeping it as 'Store<*,*>' works as intended
    abstract fun getAnyChangeObservable(): Observable<Store<*,*>>

    /** Gets an iterable of all [ServerPlayer]s that should be notified of any changes in this store. */
    abstract fun getObservingPlayers(): Iterable<ServerPlayer>

    /** Sends the contents of this [Store] to a player as if they've never seen it before. This initializes the store then sends each contained Pokémon. */
    abstract fun sendTo(player: ServerPlayer)

    /** Sends the given packet to all observing players. */
    open fun sendPacketToObservers(packet: NetworkPacket<*>) = getObservingPlayers().forEach { it.sendPacket(packet) }


    abstract fun saveToNBT(nbt: CompoundTag): CompoundTag

    /** &#9888; IMPORTANT: ALWAYS call [Store.initialize] when loading &#9888;
     *
     * If not done data will d-sync
     */
    abstract fun loadFromNbt(nbt: CompoundTag): Store<P,C>

    abstract fun saveToJSON(json: JsonObject): JsonObject

    /** &#9888; IMPORTANT: ALWAYS call [Store.initialize] when loading &#9888;
     *
     * If not done data will d-sync
     */
    abstract fun loadFromJSON(json: JsonObject): Store<P,C>


}
