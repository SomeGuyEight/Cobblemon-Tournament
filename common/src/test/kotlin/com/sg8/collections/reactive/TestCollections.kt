package com.sg8.collections.reactive

import com.sg8.collections.reactive.collection.MutableObservableCollection
import com.sg8.collections.reactive.collection.ObservableCollection


interface TestCollections<T, C : Collection<T>, M : MutableCollection<T>> {

    val input: C
    val mutableInput: M
    val observable: ObservableCollection<T, C>
    val mutableObservable: MutableObservableCollection<T, C>
    val emptyObservable: ObservableCollection<T, C>
    val emptyMutableObservable: MutableObservableCollection<T, C>
}
