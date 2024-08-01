package com.sg8.collections.reactive.list

import com.sg8.storage.datakeys.*
import com.sg8.util.getIntOrNull
import net.minecraft.nbt.CompoundTag


inline fun <reified T, reified L : ObservableList<T>> L.saveToNbt(
    elementHandler: (T) -> CompoundTag,
): CompoundTag {
    val nbt = CompoundTag()
    var size = 0
    this.iterator().forEach { nbt.put(ELEMENT_KEY + size++, elementHandler(it)) }
    nbt.putInt(SIZE_KEY, size)
    return nbt
}


inline fun <reified T, reified L : ObservableList<T>> CompoundTag.loadObservableListOf(
    noinline elementHandler: (CompoundTag) -> T,
): L {
    val newList = mutableListOf<T>()
    this.getIntOrNull(SIZE_KEY)?.let { size ->
        for (i in 0 until size) {
            newList.add(elementHandler(this.getCompound(ELEMENT_KEY + i)))
        }
    }
    return L::class.java
        .getConstructor(newList::class.java, elementHandler::class.java)
        .newInstance(newList, elementHandler)
}
