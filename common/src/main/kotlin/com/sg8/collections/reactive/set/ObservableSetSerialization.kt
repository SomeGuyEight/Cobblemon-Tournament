package com.sg8.collections.reactive.set

import com.sg8.storage.datakeys.*
import com.sg8.util.getIntOrNull
import net.minecraft.nbt.CompoundTag


inline fun <reified T, reified S : ObservableSet<T>> S.saveToNbt(
    elementHandler: (T) -> CompoundTag,
): CompoundTag {
    val nbt = CompoundTag()
    var size = 0
    this.iterator().forEach { nbt.put(ELEMENT_KEY + size++, elementHandler(it)) }
    nbt.putInt(SIZE_KEY, size)
    return nbt
}


inline fun <reified T, reified S : ObservableSet<T>> CompoundTag.loadObservableSetOf(
    noinline elementHandler: (CompoundTag) -> T,
): S {
    val newSet = mutableSetOf<T>()
    this.getIntOrNull(SIZE_KEY)?.let { size ->
        for (i in 0 until size) {
            newSet.add(elementHandler(this.getCompound(ELEMENT_KEY + i)))
        }
    }
    return S::class.java
        .getConstructor(newSet::class.java, elementHandler::class.java)
        .newInstance(newSet, elementHandler)
}
