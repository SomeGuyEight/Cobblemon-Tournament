package com.sg8.collections.reactive.list

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.reactive.*
import com.sg8.collections.*
import com.sg8.collections.reactive.collection.*


class MutableObservableList<T>(
    list: Collection<T> = setOf(),
    elementHandler: (T) -> Set<Observable<*>> = { it.getObservables() },
) : MutableObservableCollection<T, List<T>>,
    ObservableList<T>(list, elementHandler),
    MutableList<T> {

    private val additionObservable = SimpleObservable<Pair<List<T>, T>>()
    private val removalObservable = SimpleObservable<Pair<List<T>, T>>()
    private val orderMutationObservable = SimpleObservable<Pair<List<T>, Set<T>>>()

    override fun subscribe(
        priority: Priority,
        anyChangeHandler: (Pair<List<T>, T>) -> Unit,
        additionHandler: ((Pair<List<T>, T>) -> Unit)?,
        removalHandler: ((Pair<List<T>, T>) -> Unit)?,
    ): ListSubscription<T> {
        return ListSubscription(
            anyChange = this.subscribe(priority, anyChangeHandler),
            addition = additionHandler?.let { additionObservable.subscribe(priority, it) },
            removal = removalHandler?.let { removalObservable.subscribe(priority, it) },
        )
    }

    fun subscribe(
        priority: Priority,
        anyChangeHandler: (Pair<List<T>, T>) -> Unit,
        additionHandler: ((Pair<List<T>, T>) -> Unit)?,
        removalHandler: ((Pair<List<T>, T>) -> Unit)?,
        orderMutationHandler: ((Pair<List<T>, Set<T>>) -> Unit)?,
    ): ListSubscription<T> {
        return ListSubscription(
            anyChange = this.subscribe(priority, anyChangeHandler),
            addition = additionHandler?.let { additionObservable.subscribe(priority, it) },
            removal = removalHandler?.let { removalObservable.subscribe(priority, it) },
            orderMutation = orderMutationHandler?.let { orderMutationObservable.subscribe(priority, it) },
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

    private fun emitAddition(element: T) {
        additionObservable.emit(list as List<T> to element)
    }

    private fun emitRemoval(element: T) {
        removalObservable.emit(list as List<T> to element)
    }

    private fun emitOrderMutation(elements: Set<T>) {
        orderMutationObservable.emit(list as List<T> to elements)
        elements.forEach{ emitAnyChange(it) }
    }

    override operator fun iterator() = list.iterator()

    override fun listIterator() = MutableObservableListIterator(this)

    override fun listIterator(index: Int) = MutableObservableListIterator(this, index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = list.subList(fromIndex, toIndex)

    fun swapIf(indexOne: Int, indexTwo: Int, predicate: () -> Boolean): Boolean {
        return predicate().also { if (it) swap(indexOne, indexTwo) }
    }

    fun swap(indexOne: Int, indexTwo: Int) {
        val one = list[indexOne]
        val two = list.set(indexTwo, one)
        list[indexOne] = two
        emitOrderMutation(setOf(one, two))
    }

    fun addIf(element: T, predicate: () -> Boolean): Boolean {
        return if (predicate()) add(element) else false
    }

    override fun add(element: T): Boolean {
        list.add(element)
        return register(element)
    }

    fun addIf(index: Int, element: T, predicate: () -> Boolean): Boolean {
        return if (predicate()) {
            add(index, element)
            true
        } else {
            false
        }
    }

    override fun add(index: Int, element: T) {
        list.add(index, element)
        register(element)
    }

    override operator fun set(index: Int, element: T): T {
        return list.set(index, element).also { replaced ->
            register(element)
            unregister(replaced)
        }
    }

    fun removeIf(predicate: (T) -> Boolean): Boolean {
        var removed = false
        list.forEach { element ->
            if (predicate(element) && remove(element) && !removed) {
                removed = true
            }
        }
        return removed
    }

    fun removeIf(element: T, predicate: (List<T>) -> Boolean): Boolean {
        return if (list.contains(element) && predicate(list)) remove(element) else false
    }

    override fun remove(element: T) = if (list.remove(element)) unregister(element) else false

    fun removeAtIf(index: Int, predicate: (T) -> Boolean): T? {
        return if (predicate(list[index])) removeAt(index) else null
    }

    override fun removeAt(index: Int): T = list.removeAt(index).also { unregister(it) }

    override fun addAll(elements: Collection<T>): Boolean {
        val newElements = elements.unsharedElements(list)
        return if (list.addAll(newElements)) {
            newElements.forEach { register(it) }
            true
        } else {
            false
        }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val newElements = elements.unsharedElements(list)
        return if (list.addAll(index, newElements)) {
            newElements.forEach { register(it) }
            true
        } else {
            false
        }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val sharedElements = elements.sharedElements(list)
        return if (list.removeAll(sharedElements)) {
            sharedElements.forEach { unregister(it) }
            true
        } else {
            false
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val removedElements = list.unsharedElements(elements)
        return if (list.retainAll(elements)) {
            removedElements.forEach { unregister(it) }
            true
        } else {
            false
        }
    }

    override fun clear() {
        if (list.isNotEmpty()) {
            val elements = list.toSet()
            list.clear()
            elements.forEach { unregister(it) }
        }
    }
}
