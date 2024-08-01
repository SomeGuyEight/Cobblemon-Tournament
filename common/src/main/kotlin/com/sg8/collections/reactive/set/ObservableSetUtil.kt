package com.sg8.collections.reactive.set

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.collection.getObservables


inline fun <reified T, reified C : Collection<T>> C.toObservableSetOf(
    noinline elementHandler: (T) -> Set<Observable<*>> = { it.getObservables() },
) = ObservableSet(this, elementHandler)


inline fun <reified T, reified C : Collection<T>> C.toMutableObservableSetOf(
    noinline elementHandler: (T) -> Set<Observable<*>> = { it.getObservables() },
) = MutableObservableSet(this, elementHandler)


inline fun <reified T, reified S : ObservableSet<T>> observableSetOf(): S {
    val emptySet = setOf<T>()
    return S::class.java.getConstructor(emptySet::class.java).newInstance(emptySet)
}
