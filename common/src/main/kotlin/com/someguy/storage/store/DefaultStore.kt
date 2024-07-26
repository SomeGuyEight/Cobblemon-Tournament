package com.someguy.storage.store

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.getPlayer
import com.someguy.storage.position.UuidPosition
import com.google.gson.JsonObject
import com.someguy.storage.*
import com.someguy.storage.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

abstract class DefaultStore <C : ClassStored>(storeID: StoreID) :
    Store<UuidPosition, C>(storeID = storeID) {

    protected var instances = mutableMapOf<UUID, C>()

    private val observingUUIDs by lazy { mutableSetOf<UUID>() }

    protected val anyChangeObservable by lazy { SimpleObservable<AnyStore>() }

    protected val observableSubscription by lazy {
        ObservableSubscription(anyChangeObservable) { anyChangeObservable.emit((this)) }
    }

    private val observables by lazy { mutableListOf<Observable<*>>() }

    override fun initialize() {
        for(entry in instances) {
            val instance = entry.value
            val coords = instance.storeCoordinates.get()
            val current: ClassStored? = instances[instance.uuid]
            if (current == null) {
                set(position = UuidPosition(uuid = instance.uuid), instance = instance)
            } else if (coords == null || coords.store != this) {
                instance.storeCoordinates.set(
                    StoreCoordinates(store = this, position = UuidPosition(uuid = instance.uuid))
                )
            }
            registerObservable(observable = instance.getChangeObservable())
        }
    }

    override fun iterator(): Iterator<C> = instances.values.iterator()

    override fun instanceNames(): NameSet {
        val names = mutableSetOf<String>()
        for (instance in instances) {
            names.add(instance.value.name)
        }
        return names
    }

    override fun isValidPosition(position: UuidPosition): Boolean {
        return !instances.containsKey(position.uuid)
    }

    override fun getFirstAvailablePosition(): UuidPosition? {
        for (i in 0 until 10) {
            val uuid = UUID.randomUUID()
            if (!instances.containsKey(uuid)) {
                return UuidPosition(uuid = uuid)
            }
        }
        return null
    }

    override fun add(instance: C): Boolean {
        val position = UuidPosition(uuid = instance.uuid)
        setAtPosition(position = position, instance = instance)
        return instances[position.uuid] == instance
    }

    override operator fun set(position: UuidPosition, instance: C) {
        setAtPosition(position = position, instance = instance)
    }

    override fun setAtPosition(position: UuidPosition, instance: C?) {
        val existingInstance = instances[position.uuid]
        if (existingInstance == instance) {
            return
        }
        if (existingInstance != null) {
            remove(position = UuidPosition(uuid = existingInstance.uuid))
        }
        if (instance == null) {
            return
        }
        instance.storeCoordinates.set(StoreCoordinates(store = this, position = position))
        instances[instance.uuid] = instance
        registerObservable(observable = instance.getChangeObservable())
        emitChange()
    }

    override fun get(position: StorePosition): C? {
        return if (position is UuidPosition) {
            instances[position.uuid]
        } else {
            null
        }
    }

    override fun remove(instance: C): Boolean {
        return remove(position = UuidPosition(uuid = instance.uuid))
    }

    override fun remove(position: UuidPosition): Boolean {
        return instances
            .remove(position.uuid)
            ?.let { emitChange() }
            .let { it != null }
    }

    override fun getObservingPlayers(): Iterable<ServerPlayer> {
        return observingUUIDs.mapNotNull { it.getPlayer() }
    }

    fun addObserver(player: ServerPlayer) {
        observingUUIDs.add(player.uuid)
        sendTo(player)
    }

    fun removeObserver(playerID: PlayerID) = observingUUIDs.remove(playerID)

    override fun sendTo(player: ServerPlayer) = TODO("Not yet implemented")

    private fun emitChange() = anyChangeObservable.emit(this)

    override fun getStoreChangeObservable() = anyChangeObservable

    fun getAllObservables() = observables.asIterable()

    private fun registerObservable(observable: Observable<*>): Observable<*> {
        observables.add(observable)
        observable.subscribe { anyChangeObservable.emit(this) }
        return observable
    }

    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        nbt.putUUID(STORE_ID_KEY, this.storeID )
        for ((index, instance) in instances.values.withIndex() ) {
            nbt.put(STORE_DATA_KEY + index, instance.saveToNbt(nbt = CompoundTag()))
        }
        nbt.putInt(SIZE_KEY, instances.size)
        return nbt
    }

    override fun saveToJson(json: JsonObject): JsonObject { TODO("Not yet implemented") }

    protected fun loadInstanceDataSetFromNbt(nbt: CompoundTag): Set<CompoundTag> {
        val size = nbt.getInt(SIZE_KEY)
        val set = mutableSetOf<CompoundTag>()
        for (i in 0..< size) {
            set.add(nbt.getCompound(STORE_DATA_KEY + i))
        }
        return set
    }

}
