package com.sg8.collections.reactive.collection


interface ObservableCollectionIterator<T, I : Iterator<T>> : Iterator<T> {

    override fun hasNext(): Boolean
    override fun next(): T
}
