package com.sg8.collections.reactive.set

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.sg8.collections.reactive.collection.MutableObservableCollection
import com.sg8.collections.reactive.collection.getElementObservables
import com.sg8.collections.reactive.subscriptions.SetSubscription
import com.sg8.collections.removeIf


class MutableObservableSet<T>(
    set: Collection<T> = setOf(),
    elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
) : MutableObservableCollection<T, Set<T>>,
    ObservableSet<T>(set, elementHandler),
    MutableSet<T> {

    private val additionObservable = SimpleObservable<Pair<Set<T>, T>>()
    private val removalObservable = SimpleObservable<Pair<Set<T>, T>>()

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

    /**
     * @return `true` after handling [register] & all subscriptions
     * to [additionObservable] & [ObservableSet.emitAnyChange].
     */
    private fun emitAddition(element: T): Boolean {
        register(element)
        additionObservable.emit(set to element)
        return emitAnyChange(element)
    }

    /**
     * @return `true` after handling [unregister] & all subscriptions
     * to [removalObservable] & [ObservableSet.emitAnyChange].
     */
    private fun emitRemoval(element: T): Boolean {
        unregister(element)
        removalObservable.emit(set to element)
        return emitAnyChange(element)
    }

    override operator fun iterator() = MutableObservableSetIterator(this)

    override fun addIf(element: T, predicate: (Set<T>) -> Boolean): Boolean {
        return if (predicate(set)) add(element) else false
    }

    // All mutations resulting from addition are funneled through here
    // Calls [emitAddition] if [element] is added to [set]
    override fun add(element: T): Boolean {
        return if (set.add(element)) emitAddition(element) else false
    }

    override fun removeIf(predicate: (T) -> Boolean): Boolean {
        return iterator().removeIf(predicate)
    }

    fun removeIf(element: T, predicate: (Set<T>) -> Boolean): Boolean {
        return if (predicate(set)) remove(element) else false
    }

    // All mutations resulting from removal are funneled through here
    // Calls [emitRemoval] if [element] is removed from [set]
    override fun remove(element: T): Boolean {
        return if (set.remove(element)) emitRemoval(element) else false
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var mutated = false
        elements.forEach { if (add(it) && !mutated) mutated = true }
        return mutated
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var mutated = false
        elements.forEach { element ->
            if (removeIf { it == element } && !mutated) {
                mutated = true
            }
        }
        return mutated
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return removeIf { !elements.contains(it) }
    }

    override fun clear() {
        // simpler way to iterate through the mutable iterator & emit with each removal
        removeIf { true }
    }
}
