package com.sg8.collections.reactive

import com.sg8.collections.reactive.set.MutableObservableSet
import com.sg8.collections.reactive.set.ObservableSet


class TestSets(val inputSet: Set<String> = mutableSetOf("zero", "one", "two", "three")) {

    val set: ObservableSet<String> = ObservableSet(inputSet)
    val mutableSet: MutableObservableSet<String> = MutableObservableSet(inputSet)
    val emptySet = ObservableSet<String>(setOf())
    val emptyMutableSet = MutableObservableSet<String>()
}
