package com.sg8.collections.reactive.map

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.*
import kotlin.collections.Map.Entry


inline fun <reified K, reified V, reified M : Map<K, V>> M.toObservableMapOf(
    noinline entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getObservables() },
) = ObservableMap(this, entryHandler)


inline fun <reified K, reified V, reified M : Map<K, V>> M.toMutableObservableMapOf(
    noinline entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getObservables() },
) = MutableObservableMap(this, entryHandler)


inline fun <reified K, reified V, reified M : ObservableMap<K, V>> observableMapOf(
    noinline entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getObservables() },
): M {
    val map = mapOf<K, V>()
    return M::class.java
        .getConstructor(map::class.java, entryHandler::class.java)
        .newInstance(map, entryHandler)
}


fun <K, V, E: Entry<K, V>> E.getObservables(): Set<Observable<*>> {
    val observables = mutableSetOf<Observable<*>>()
    this.key.tryGetObservable()?.let { observables.add(it) }
    this.value.tryGetObservable()?.let { observables.add(it) }
    return observables
}
