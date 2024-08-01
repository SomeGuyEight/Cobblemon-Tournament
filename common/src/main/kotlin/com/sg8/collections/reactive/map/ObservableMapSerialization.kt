package com.sg8.collections.reactive.map

import com.sg8.storage.datakeys.*
import com.sg8.util.getIntOrNull
import net.minecraft.nbt.CompoundTag
import kotlin.collections.Map.Entry


inline fun <K, V, reified M: ObservableMap<K, V>> M.saveToNbt(
    entryHandler: (Entry<K, V>) -> CompoundTag,
): CompoundTag {
    val nbt = CompoundTag()
    var size = 0
    this.iterator().forEach { nbt.put(ENTRY_KEY + size++, entryHandler(it)) }
    nbt.putInt(SIZE_KEY, size)
    return nbt
}


inline fun  <reified K, reified V, reified M: ObservableMap<K, V>> CompoundTag.loadObservableMapOf(
    noinline entryHandler: (CompoundTag) -> Pair<K, V>,
): M {
    val newMap = mutableMapOf<K, V>()
    this.getIntOrNull(SIZE_KEY)?.let {size ->
        for (i in 0 until size) {
            val (key, value) = entryHandler(this.getCompound(ENTRY_KEY + i))
            newMap[key] = value
        }
    }
    return M::class.java
        .getConstructor(newMap::class.java, entryHandler::class.java)
        .newInstance(newMap, entryHandler)
}
