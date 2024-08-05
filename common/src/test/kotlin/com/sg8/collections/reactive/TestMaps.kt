package com.sg8.collections.reactive

import com.sg8.collections.reactive.map.MutableObservableMap
import com.sg8.collections.reactive.map.ObservableMap


class TestMaps<K, V>(val input: Map<K, V>) {

    val mutableInput: MutableMap<K, V> = input.toMutableMap()
    val observable: ObservableMap<K, V> = ObservableMap(input)
    val mutableObservable: MutableObservableMap<K, V> = MutableObservableMap(input)
    val emptyObservable = ObservableMap<K, V>(mapOf())
    val emptyMutableObservable = MutableObservableMap<K, V>()

    companion object {
        fun default() = testMapsOf(0 to "zero", 1 to "one", 2 to "two", 3 to "three")
        fun defaultMap() = mapOf(0 to "zero", 1 to "one", 2 to "two", 3 to "three")
    }
}

fun <K, V> testMapsOf(vararg pairs: Pair<K, V>) = TestMaps(mapOf(*pairs))
