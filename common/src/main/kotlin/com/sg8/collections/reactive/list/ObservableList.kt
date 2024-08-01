package com.sg8.collections.reactive.list

import com.cobblemon.mod.common.api.*
import com.cobblemon.mod.common.api.reactive.*
import com.sg8.collections.reactive.collection.*


open class ObservableList<T>(
    list: Collection<T>,
    protected val elementHandler: (T) -> Set<Observable<*>> = { it.getObservables() },
) : ObservableCollection<T, List<T>>,
    List<T>,
    Observable<Pair<List<T>, T>> {

    val list: MutableList<T> = list.toMutableList()

    private val subscriptions = PrioritizedList<ObservableSubscription<Pair<List<T>, T>>>()
    protected val subscriptionMap: MutableMap<Observable<*>, ObservableSubscription<*>> = mutableMapOf()

    override val elements: List<T> get() = list.toList()
    override val size: Int get() = list.size
    val lastIndex: Int get() = list.lastIndex

    init {
        list.forEach { element ->
            elementHandler(element).forEach { observable ->
                subscriptionMap[observable] = observable.subscribe { emitAnyChange(element) }
            }
        }
    }

    override fun subscribe(
        priority: Priority,
        handler: (Pair<List<T>, T>) -> Unit,
    ): ObservableSubscription<Pair<List<T>, T>> {
        val subscription = ObservableSubscription(this, handler)
        subscriptions.add(priority, subscription)
        return subscription
    }

    override fun unsubscribe(subscription: ObservableSubscription<Pair<List<T>, T>>) {
        subscriptions.remove(subscription)
    }

    protected open fun register(element: T): Boolean {
        elementHandler(element).forEach{ observable ->
            subscriptionMap[observable] = observable.subscribe { emitAnyChange(element) }
        }
        return emitAnyChange(element)
    }

    protected fun emitAnyChange(element: T): Boolean {
        subscriptions.forEach { it.handle(list to element) }
        return true
    }

    override fun isEmpty() = list.isEmpty()

    override fun isNotEmpty() = list.isNotEmpty()

    override fun contains(element: T) = list.contains(element)

    override fun containsAll(elements: Collection<T>) = list.containsAll(elements)

    override operator fun get(index: Int): T = list[index]

    override fun indexOf(element: T) = list.indexOf(element)

    override operator fun iterator(): Iterator<T> = list.iterator()

    override fun lastIndexOf(element: T) = list.lastIndexOf(element)

    override fun listIterator(): ListIterator<T> = list.listIterator()

    override fun listIterator(index: Int): ListIterator<T> = list.listIterator(index = index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = list.subList(fromIndex, toIndex)

    override fun copy() = ObservableList(this.list, elementHandler)

    open fun mutableCopy() = MutableObservableList(this.list, elementHandler)
}
