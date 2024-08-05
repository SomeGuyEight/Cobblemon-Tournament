package com.sg8.collections.reactive.map

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.tryGetObservable
import com.sg8.collections.toMap
import kotlin.collections.Map.Entry


fun <K, V> Map<K, V>.toObservableMap(
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = ObservableMap(this, entryHandler)


fun <K, V> Map<K, V>.toMutableObservableMap(
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = MutableObservableMap(this, entryHandler)


fun <K, V> Collection<Pair<K, V>>.toObservableMap(
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = ObservableMap(this.toMap(), entryHandler)


fun <K, V> Collection<Pair<K, V>>.toMutableObservableMap(
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = MutableObservableMap(this.toMap(), entryHandler)


fun <K, V> observableMapOf(
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = ObservableMap(mapOf(), entryHandler)


fun <K, V> mutableObservableMapOf(
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = MutableObservableMap(mapOf(), entryHandler)


fun <K, V> observableMapOf(
    map: Map<K, V> = emptyMap(),
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = ObservableMap(map, entryHandler)


fun <K, V> observableMapOf(
    vararg pairs: Pair<K, V>,
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = ObservableMap(pairs.toMap(), entryHandler)


fun <K, V> mutableObservableMapOf(
    vararg pairs: Pair<K, V>,
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = MutableObservableMap(pairs.toMap(), entryHandler)


fun <K, V> mutableObservableMapOf(
    map: Map<K, V> = emptyMap(),
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = MutableObservableMap(map, entryHandler)


fun <K, V> observableMapOf(
    vararg entries: Entry<K, V>,
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = ObservableMap(entries.toSet().toMap(), entryHandler)


fun <K, V> mutableObservableMapOf(
    vararg entries: Entry<K, V>,
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = MutableObservableMap(entries.toSet().toMap(), entryHandler)


fun <K, V> observableMapOf(
    pairs: Collection<Pair<K, V>> = emptySet(),
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = ObservableMap(pairs.toMap(), entryHandler)


fun <K, V> mutableObservableMapOf(
    pairs: Collection<Pair<K, V>> = emptySet(),
    entryHandler: (K, V) -> Set<Observable<*>> = { k, v -> k.getEntryObservables(v) },
) = MutableObservableMap(pairs.toMap(), entryHandler)


fun <K, V> K.getEntryObservables(value: V): Set<Observable<*>> {
    val observables = mutableSetOf<Observable<*>>()
    this.tryGetObservable()?.let { observables.add(it) }
    value.tryGetObservable()?.let { observables.add(it) }
    return observables
}

