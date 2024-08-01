package com.sg8.collections.reactive.list

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.collection.getObservables


inline fun <reified T, reified C : Collection<T>> C.toObservableListOf(
    noinline elementHandler: (T) -> Set<Observable<*>> = { it.getObservables() },
) = ObservableList(this, elementHandler)


inline fun <reified T, reified C : Collection<T>> C.toMutableObservableListOf(
    noinline elementHandler: (T) -> Set<Observable<*>> = { it.getObservables() },
) = MutableObservableList(this, elementHandler)


inline fun <reified T, reified L : ObservableList<T>> observableListOf(): L {
    val emptySet = setOf<T>()
    return L::class.java.getConstructor(emptySet::class.java).newInstance(emptySet)
}
