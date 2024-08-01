package com.sg8.collections.reactive.set

import com.cobblemon.mod.common.api.*
import com.cobblemon.mod.common.api.reactive.*
import com.sg8.collections.reactive.collection.*


open class ObservableSet<T>(
    set: Collection<T>,
    protected val elementHandler: (T) -> Set<Observable<*>> = { it.getObservables() },
) : ObservableCollection<T, Set<T>>,
    Set<T>,
    Observable<Pair<Set<T>, T>> {

    protected open val set: MutableSet<T> = set.toMutableSet()

    private val subscriptions = PrioritizedList<ObservableSubscription<Pair<Set<T>, T>>>()
    protected val subscriptionMap: MutableMap<Observable<*>, ObservableSubscription<*>> = mutableMapOf()

    override val elements: Set<T> get() = set
    override val size: Int get() = set.size

    init {
        set.forEach { element ->
            elementHandler(element).forEach { observable ->
                subscriptionMap[observable] = observable.subscribe { emitAnyChange(element) }
            }
        }
    }

    override fun subscribe(
        priority: Priority,
        handler: (Pair<Set<T>, T>) -> Unit,
    ): ObservableSubscription<Pair<Set<T>, T>> {
        val subscription = ObservableSubscription(this, handler)
        subscriptions.add(priority, subscription)
        return subscription
    }

    override fun unsubscribe(subscription: ObservableSubscription<Pair<Set<T>, T>>) {
        subscriptions.remove(subscription)
    }

    protected open fun register(element: T): Boolean {
        elementHandler(element).forEach{ observable ->
            subscriptionMap[observable] = observable.subscribe { emitAnyChange(element) }
        }
        return emitAnyChange(element)
    }

    protected fun emitAnyChange(element: T): Boolean {
        subscriptions.forEach { it.handle(set to element) }
        return true
    }

    override fun isEmpty() = set.isEmpty()

    override fun isNotEmpty() = set.isNotEmpty()

    override fun contains(element: T) = set.contains(element)

    override fun containsAll(elements: Collection<T>) = set.containsAll(elements)

    override operator fun iterator(): Iterator<T> = set.iterator()

    override fun copy() = ObservableSet(set, elementHandler)

    fun mutableCopy() = MutableObservableSet(set, elementHandler)
}
