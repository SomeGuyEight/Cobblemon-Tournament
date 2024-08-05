package com.sg8.collections.reactive.map

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.storage.DataKeys
import com.sg8.util.getIntOrNull
import net.minecraft.nbt.CompoundTag
import kotlin.collections.Map.Entry


fun <K, V> Map<K, V>.saveToNbt(
    entryHandler: (Entry<K, V>) -> CompoundTag,
): CompoundTag {
    val nbt = CompoundTag()
    var size = 0
    this.iterator().forEach { nbt.put(DataKeys.ENTRY + size++, entryHandler(it)) }
    nbt.putInt(DataKeys.SIZE, size)
    return nbt
}


fun  <K, V> CompoundTag.loadObservableMapOf(
    loadEntryHandler: (CompoundTag) -> Pair<K, V>,
    entryObservableHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = ObservableMap(this.loadMap(loadEntryHandler), entryObservableHandler)


fun  <K, V> CompoundTag.loadMutableObservableMapOf(
    loadEntryHandler: (CompoundTag) -> Pair<K, V>,
    entryObservableHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = MutableObservableMap(this.loadMap(loadEntryHandler), entryObservableHandler)


private fun <K, V> CompoundTag.loadMap(entryHandler: (CompoundTag) -> Pair<K, V>): MutableMap<K, V> {
    val newMap: MutableMap<K, V> = mutableMapOf()
    this.getIntOrNull(DataKeys.SIZE)?.let { size ->
        for (i in 0 until size) {
            val (key, value) = entryHandler(this.getCompound(DataKeys.ENTRY + i))
            newMap[key] = value
        }
    }
    return newMap
}
