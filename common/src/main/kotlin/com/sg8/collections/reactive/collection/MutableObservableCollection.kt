package com.sg8.collections.reactive.collection

import com.cobblemon.mod.common.api.Priority


interface MutableObservableCollection<T, C : Collection<T>> :
    ObservableCollection<T, C>,
    MutableCollection<T> {

    fun subscribe(
        priority: Priority,
        anyChangeHandler: (Pair<C, T>) -> Unit,
        additionHandler: ((Pair<C, T>) -> Unit)? = null,
        removalHandler: ((Pair<C, T>) -> Unit)? = null,
    ): CollectionSubscription<T, C>

    override fun add(element: T): Boolean

    override fun remove(element: T): Boolean

    override fun addAll(elements: Collection<T>): Boolean

    override fun retainAll(elements: Collection<T>): Boolean

    override fun removeAll(elements: Collection<T>): Boolean

    override fun iterator(): MutableIterator<T>

    override fun clear()

    fun mutableCopy(): MutableObservableCollection<T, C>
}
