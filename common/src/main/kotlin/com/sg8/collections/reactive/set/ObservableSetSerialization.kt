package com.sg8.collections.reactive.set

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.collection.getElementObservables
import com.sg8.storage.DataKeys
import com.sg8.util.getIntOrNull
import net.minecraft.nbt.CompoundTag


fun <T> Set<T>.saveToNbt(
    elementHandler: (T) -> CompoundTag,
): CompoundTag {
    val nbt = CompoundTag()
    var size = 0
    this.iterator().forEach { nbt.put(DataKeys.ELEMENT + size++, elementHandler(it)) }
    nbt.putInt(DataKeys.SIZE, size)
    return nbt
}


fun <T> CompoundTag.loadObservableSetOf(
    loadElementHandler: (CompoundTag) -> T,
    elementObservableHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
) = ObservableSet(this.loadSet(loadElementHandler), elementObservableHandler)


fun <T> CompoundTag.loadMutableObservableSetOf(
    loadElementHandler: (CompoundTag) -> T,
    elementObservableHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
) = MutableObservableSet(this.loadSet(loadElementHandler), elementObservableHandler)


private fun <T> CompoundTag.loadSet(elementHandler: (CompoundTag) -> T): Set<T> {
    val newSet = mutableSetOf<T>()
    this.getIntOrNull(DataKeys.SIZE)?.let { size ->
        for (i in 0 until size) {
            newSet.add(elementHandler(this.getCompound(DataKeys.ELEMENT + i)))
        }
    }
    return newSet
}
