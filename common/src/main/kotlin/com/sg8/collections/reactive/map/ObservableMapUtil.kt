package com.sg8.collections.reactive.map

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.tryGetObservable
import com.sg8.collections.toMap
import kotlin.collections.Map.Entry


fun <K, V> Map<K, V>.toObservableMap(
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = ObservableMap(this, entryHandler)


fun <K, V> Map<K, V>.toMutableObservableMap(
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = MutableObservableMap(this, entryHandler)


fun <K, V> Collection<Pair<K, V>>.toObservableMap(
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = ObservableMap(this.toMap(), entryHandler)


fun <K, V> Collection<Pair<K, V>>.toMutableObservableMap(
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = MutableObservableMap(this.toMap(), entryHandler)


fun <K, V> observableMapOf(
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = ObservableMap(mapOf(), entryHandler)


fun <K, V> mutableObservableMapOf(
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = MutableObservableMap(mapOf(), entryHandler)


fun <K, V> observableMapOf(
    map: Map<K, V> = emptyMap(),
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = ObservableMap(map, entryHandler)


fun <K, V> observableMapOf(
    vararg pairs: Pair<K, V>,
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = ObservableMap(pairs.toMap(), entryHandler)


fun <K, V> mutableObservableMapOf(
    vararg pairs: Pair<K, V>,
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = MutableObservableMap(pairs.toMap(), entryHandler)


fun <K, V> mutableObservableMapOf(
    map: Map<K, V> = emptyMap(),
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = MutableObservableMap(map, entryHandler)


fun <K, V> observableMapOf(
    vararg entries: Entry<K, V>,
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = ObservableMap(entries.toSet().toMap(), entryHandler)


fun <K, V> mutableObservableMapOf(
    vararg entries: Entry<K, V>,
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = MutableObservableMap(entries.toSet().toMap(), entryHandler)


fun <K, V> observableMapOf(
    pairs: Collection<Pair<K, V>> = emptySet(),
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = ObservableMap(pairs.toMap(), entryHandler)


fun <K, V> mutableObservableMapOf(
    pairs: Collection<Pair<K, V>> = emptySet(),
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) = MutableObservableMap(pairs.toMap(), entryHandler)


fun <K, V, E: Entry<K, V>> E.getEntryObservables(): Set<Observable<*>> {
    val observables = mutableSetOf<Observable<*>>()
    this.key.tryGetObservable()?.let { observables.add(it) }
    this.value.tryGetObservable()?.let { observables.add(it) }
    return observables
}

