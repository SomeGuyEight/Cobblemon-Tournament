package com.sg8.collections.reactive.set

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.reactive.*
import com.sg8.collections.*
import com.sg8.collections.reactive.collection.*


class MutableObservableSet<T>(
    set: Collection<T> = setOf(),
    elementHandler: (T) -> Set<Observable<*>> = { it.getObservables() },
) : MutableObservableCollection<T, Set<T>>,
    ObservableSet<T>(set, elementHandler),
    MutableSet<T> {

    private val additionObservable: SimpleObservable<Pair<Set<T>, T>> = SimpleObservable()
    private val removalObservable: SimpleObservable<Pair<Set<T>, T>> = SimpleObservable()

    override fun subscribe(
        priority: Priority,
        anyChangeHandler: (Pair<Set<T>, T>) -> Unit,
        additionHandler: ((Pair<Set<T>, T>) -> Unit)?,
        removalHandler: ((Pair<Set<T>, T>) -> Unit)?,
    ): SetSubscription<T> {
        return SetSubscription(
            anyChange = this.subscribe(priority, anyChangeHandler),
            addition = additionHandler?.let { additionObservable.subscribe(priority, it) },
            removal = removalHandler?.let { removalObservable.subscribe(priority, it) },
        )
    }

    override fun register(element: T): Boolean {
        elementHandler(element).forEach{ observable ->
            subscriptionMap[observable] = observable.subscribe { emitAnyChange(element) }
        }
        emitAddition(element)
        return emitAnyChange(element)
    }

    private fun unregister(element: T): Boolean {
        elementHandler(element).forEach{ subscriptionMap.remove(it)?.unsubscribe() }
        emitRemoval(element)
        return emitAnyChange(element)
    }

    private fun emitAddition(element: T) = additionObservable.emit(set to element)

    private fun emitRemoval(element: T) = removalObservable.emit(set to element)

    override operator fun iterator() = MutableObservableSetIterator(this)

    fun addIf(element: T, predicate: () -> Boolean) = if (predicate()) add(element) else false

    override fun add(element: T) = if (set.add(element)) register(element) else false

    fun removeIf(predicate: (T) -> Boolean): Boolean {
        var removed = false
        set.forEach { element ->
            if (predicate(element) && remove(element) && !removed) {
                removed = true
            }
        }
        return removed
    }

    fun removeIf(element: T, predicate: (Set<T>) -> Boolean): Boolean {
        return if (set.contains(element) && predicate(set)) remove(element) else false
    }

    override fun remove(element: T) = if (set.remove(element)) unregister(element) else false

    override fun addAll(elements: Collection<T>): Boolean {
        val newElements = elements.unsharedElements(set)
        return if (set.addAll(newElements)) {
            newElements.forEach { register(it) }
            true
        } else {
            false
        }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val sharedElements = elements.sharedElements(set)
        return if (set.removeAll(sharedElements)) {
            sharedElements.forEach { unregister(it) }
            true
        } else {
            false
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val removedElements = set.unsharedElements(elements)
        return if (set.retainAll(elements.toSet())) {
            removedElements.forEach { unregister(it) }
            true
        } else {
            false
        }
    }

    override fun clear() {
        if (set.isNotEmpty()) {
            val elements = set.toSet()
            set.clear()
            elements.forEach { unregister(it) }
        }
    }
}
