package com.sg8.collections.reactive

import com.sg8.collections.reactive.map.MutableObservableMap
import com.sg8.collections.reactive.map.ObservableMap


class TestMaps(vararg inputPairs: Pair<Int, String>) {

    val inputMap = if (inputPairs.isNotEmpty()) mapOf(*inputPairs) else default()
    val map: ObservableMap<Int, String> = ObservableMap(inputMap)
    val mutableMap: MutableObservableMap<Int, String> = MutableObservableMap(inputMap)
    val emptyMap = ObservableMap<Int, String>(mapOf())
    val emptyMutableMap = MutableObservableMap<Int, String>()

    private fun default() = mutableMapOf(0 to "zero", 1 to "one", 2 to "two", 3 to "three")
}
