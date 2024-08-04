package com.sg8.collections.reactive.list

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.collection.getElementObservables
import com.sg8.storage.DataKeys
import com.sg8.util.getIntOrNull
import net.minecraft.nbt.CompoundTag


fun <T> List<T>.saveToNbt(
    elementHandler: (T) -> CompoundTag,
): CompoundTag {
    val nbt = CompoundTag()
    var size = 0
    this.iterator().forEach { nbt.put(DataKeys.ELEMENT + size++, elementHandler(it)) }
    nbt.putInt(DataKeys.SIZE, size)
    return nbt
}


fun <T> CompoundTag.loadObservableListOf(
    loadElementHandler: (CompoundTag) -> T,
    elementObservableHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
) = ObservableList(this.loadList(loadElementHandler), elementObservableHandler)


fun <T> CompoundTag.loadMutableObservableListOf(
    loadElementHandler: (CompoundTag) -> T,
    elementObservableHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
) = MutableObservableList(this.loadList(loadElementHandler), elementObservableHandler)


private fun <T> CompoundTag.loadList(elementHandler: (CompoundTag) -> T): List<T> {
    val newList = mutableListOf<T>()
    this.getIntOrNull(DataKeys.SIZE)?.let { size ->
        for (i in 0 until size) {
            newList.add(elementHandler(this.getCompound(DataKeys.ELEMENT + i)))
        }
    }
    return newList
}

