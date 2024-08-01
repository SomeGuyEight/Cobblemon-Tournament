package com.sg8.collections.reactive.collection


interface MutableObservableCollectionIterator<T, C : Collection<T>> :
    ObservableCollectionIterator<T, Iterator<T>>,
    MutableIterator<T> {

    override fun remove()
}
