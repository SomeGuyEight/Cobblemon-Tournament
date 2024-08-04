package com.sg8.storage

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.getPlayer
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID


abstract class DefaultStore <T : TypeStored>(uuid: UUID) :
    Store<UuidPosition, T>(uuid = uuid) {

    protected var instances = mutableMapOf<UUID, T>()

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
            val current: TypeStored? = instances[instance.uuid]
            if (current == null) {
                set(position = UuidPosition(uuid = instance.uuid), instance = instance)
            } else if (coords == null || coords.store != this) {
                instance.storeCoordinates.set(
                    StoreCoordinates(store = this, position = UuidPosition(uuid = instance.uuid))
                )
            }
            registerObservable(observable = instance.getObservable())
        }
    }

    override fun iterator(): Iterator<T> = instances.values.iterator()

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

    override fun add(instance: T): Boolean {
        val position = UuidPosition(uuid = instance.uuid)
        setAtPosition(position = position, instance = instance)
        return instances[position.uuid] == instance
    }

    override operator fun set(position: UuidPosition, instance: T) {
        setAtPosition(position = position, instance = instance)
    }

    override fun setAtPosition(position: UuidPosition, instance: T?) {
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
        registerObservable(observable = instance.getObservable())
        emitChange()
    }

    override fun get(position: StorePosition): T? {
        return if (position is UuidPosition) {
            instances[position.uuid]
        } else {
            null
        }
    }

    override fun remove(instance: T): Boolean {
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

    fun removeObserver(playerUuid: UUID) = observingUUIDs.remove(playerUuid)

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
        nbt.putUUID(DataKeys.STORE_ID, this.uuid )
        for ((index, instance) in instances.values.withIndex() ) {
            nbt.put(DataKeys.STORE_DATA + index, instance.saveToNbt(nbt = CompoundTag()))
        }
        nbt.putInt(DataKeys.SIZE, instances.size)
        return nbt
    }

    override fun saveToJson(json: JsonObject): JsonObject { TODO("Not yet implemented") }

    protected fun loadInstanceDataSetFromNbt(nbt: CompoundTag): Set<CompoundTag> {
        val size = nbt.getInt(DataKeys.SIZE)
        val set = mutableSetOf<CompoundTag>()
        for (i in 0..< size) {
            set.add(nbt.getCompound(DataKeys.STORE_DATA + i))
        }
        return set
    }

}
