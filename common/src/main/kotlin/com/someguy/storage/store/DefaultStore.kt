package com.someguy.storage.store

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.getPlayer
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.StorePosition
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.util.StoreDataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

abstract class DefaultStore<C: ClassStored>(id: UUID): Store<UuidPosition, C>(id)
{
    protected var instances = mutableMapOf<UUID,C>()

    protected val observingUUIDs = mutableSetOf<UUID>()

    override fun iterator(): Iterator<C> = instances.values.iterator()

    override fun instanceNames(): Set<String>
    {
        val names = mutableSetOf<String>()
        for (i in instances){
            names.add(i.value.name)
        }
        return names
    }

    final override fun initialize()
    {
        for(entry in instances.toMap()) {
            val instance = entry.value
            val coords = instance.storeCoordinates.get()
            val current: ClassStored? = instances[instance.uuid]
            if (current == null) {
                set(UuidPosition(instance.uuid),instance)
            } else if (coords == null || coords.store != this) {
                instance.storeCoordinates.set(StoreCoordinates(this,UuidPosition(instance.uuid)))
            }
            registerObservable(instance.getChangeObservable())
        }
        initializeSubclass()
    }

    /** Called after [DefaultStore.initialize] by Default Store*/
    abstract fun initializeSubclass()

    override fun isValidPosition(
        position: UuidPosition
    ): Boolean {
        return !instances.containsKey(position.uuid)
    }

    override fun getFirstAvailablePosition(): UuidPosition?
    {
        for (i in 0 until 10) {
            val id = UUID.randomUUID()
            if (!instances.containsKey(id)) {
                return UuidPosition(id)
            }
        }
        return null
    }

    override fun add(
        instance: C
    ): Boolean
    {
        val position = UuidPosition(instance.uuid)
        setAtPosition(position,instance)
        return instances[position.uuid] == instance
    }

    override operator fun set(position: UuidPosition,instance: C) = setAtPosition(position,instance)

    override fun setAtPosition(
        position: UuidPosition,
        instance: C?
    )
    {
        val existing = instances[position.uuid]
        if (existing == instance) {
            return
        }
        if (existing != null) {
            instances.remove(existing.uuid)
        }
        if (instance == null) {
            return
        }
        instance.storeCoordinates.set(StoreCoordinates(this,position))
        instances[instance.uuid] = instance
    }

    override fun get(
        position: StorePosition
    ): C? {
        if (position !is UuidPosition) return null
        return instances[position.uuid]
    }


    override fun remove(instance: C): Boolean
    {
        return if (instances.containsKey(instance.uuid)) {
            instances.remove(instance.uuid)
            true
        } else false
    }

    override fun remove(
        position: UuidPosition
    ): Boolean
    {
        return if (instances.containsKey(position.uuid)) {
            instances.remove(position.uuid)
            true
        } else false
    }

    override fun getObservingPlayers(): Iterable<ServerPlayer> = observingUUIDs.mapNotNull { it.getPlayer() }

    fun addObserver(player: ServerPlayer)
    {
        observingUUIDs.add(player.uuid)
        sendTo(player)
    }

    fun removeObserver(playerID: UUID) = observingUUIDs.remove(playerID)

    /* may be helpful in future
    override fun sendPacketToObservers(packet: NetworkPacket<*>) = getObservingPlayers().forEach { it.sendPacket(packet) } */

    override fun sendTo(
        player: ServerPlayer
    ) {
        TODO("Not yet implemented")
        // need to set up for display eventually
    }

    override fun saveToNBT(nbt: CompoundTag): CompoundTag
    {
        nbt.putUUID(StoreDataKeys.STORE_ID,this.storeID)
        for((index, instance) in instances.values.withIndex()) {
            nbt.put(StoreDataKeys.STORE_DATA + index,instance.saveToNBT(CompoundTag()))
        }
        nbt.putInt(StoreDataKeys.SIZE,instances.size)
        return nbt
    }

    // low priority TODO: change to iterable
    protected fun instancesDataSetNBT(nbt: CompoundTag): Set<CompoundTag>
    {
        // low priority TODO: change to iterable
        val size = nbt.getInt(StoreDataKeys.SIZE)
        val set = mutableSetOf<CompoundTag>()
        for (i in 0 until size) {
            set.add(nbt.getCompound(StoreDataKeys.STORE_DATA + i))
        }
        return set
    }

    // low priority
    override fun saveToJSON(json: JsonObject): JsonObject { TODO("Not yet implemented") }

    // low priority
    override fun loadFromJSON(json: JsonObject): Store<UuidPosition, C> { TODO("Not yet implemented") }


    protected val anyChangeObservable = SimpleObservable<Unit>()
    private val observables = mutableListOf<Observable<*>>()

    override fun getAnyChangeObservable(): Observable<Unit> = anyChangeObservable
    fun getAllObservables() = observables.asIterable()

    protected fun registerObservable(
        observable: Observable<*>
    ): Observable<*>
    {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit() }
        return observable
    }

}
