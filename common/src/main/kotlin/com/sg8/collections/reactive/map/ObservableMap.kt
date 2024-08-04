package com.sg8.collections.reactive.map

import com.cobblemon.mod.common.api.PrioritizedList
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import kotlin.Comparator
import kotlin.collections.Map.Entry


open class ObservableMap<K, V>(
    map: Map<K, V>,
    protected val entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getEntryObservables() },
) : Map<K, V>,
    Observable<Pair<Map<K, V>, Entry<K, V>>> {

    protected open val map: MutableMap<K, V> = map.toMutableMap()

    private val subscriptions = PrioritizedList<ObservableSubscription<Pair<Map<K, V>, Entry<K, V>>>>()
    protected val subscriptionMap: MutableMap<Observable<*>, ObservableSubscription<*>> = mutableMapOf()

    override val size get() = map.size
    override val keys get() = map.keys
    override val values get() = map.values
    override val entries get() = map.entries

    init {
        map.entries.forEach { entry ->
            entryHandler(entry).forEach{ observable ->
                subscriptionMap[observable] = observable.subscribe { emitAnyChange(entry) }
            }
        }
    }

    override fun subscribe(
        priority: Priority,
        handler: (Pair<Map<K, V>, Entry<K, V>>) -> Unit
    ): ObservableSubscription<Pair<Map<K, V>, Entry<K, V>>> {
        val subscription = ObservableSubscription(this, handler)
        subscriptions.add(priority, subscription)
        return subscription
    }

    fun subscribeThenHandle(
        priority: Priority,
        handler: (Pair<Map<K, V>, Entry<K, V>>) -> Unit
    ): ObservableSubscription<Pair<Map<K, V>, Entry<K, V>>> {
        val subscription = ObservableSubscription(this, handler)
        subscriptions.add(priority, subscription)
        map.forEach { handler(this to it)}
        return subscription
    }

    override fun unsubscribe(subscription: ObservableSubscription<Pair<Map<K, V>, Entry<K, V>>>) {
        subscriptions.remove(subscription)
    }

    protected open fun register(entry: Entry<K, V>): Boolean {
        if (contains(entry.key)) {
            entryHandler(entry).forEach{ observable ->
                subscriptionMap[observable] = observable.subscribe { emitAnyChange(entry) }
            }
        }
        return emitAnyChange(entry)
    }

    protected fun emitAnyChange(pair: Entry<K, V>): Boolean {
        //subscriptions.forEach { it.handle(map to pair) }
        subscriptions.forEach { it.handle(map.toMap() to pair) }
        return true
    }

    override fun isEmpty() = map.isEmpty()

    fun isNotEmpty() = map.isNotEmpty()

    override fun containsKey(key: K) = map.containsKey(key)

    override fun containsValue(value: V) = map.containsValue(value)

    fun contains(key: K, value: V) = map[key] == value

    fun contains(predicate: (Entry<K, V>) -> Boolean) = map.any(predicate)

    fun containsAll(other: Map<K, V>): Boolean {
        this.forEach { if (it.value != other[it.key]) return false }
        return true
    }

    open operator fun iterator(): Iterator<Entry<K, V>> = map.iterator()

    override operator fun get(key: K) = map[key]

    fun toSortedMap(comparator: Comparator<in K>) = map.toMap().toSortedMap(comparator)

    fun firstKeyOrNull(predicate: (Entry<K, V>) -> Boolean): K? {
        map.forEach { if (predicate(it)) return it.key }
        return null
    }

    fun firstValueOrNull(predicate: (Entry<K, V>) -> Boolean): V? {
        map.forEach { if (predicate(it)) return it.value }
        return null
    }

    fun firstEntryOrNull(predicate: (Entry<K, V>) -> Boolean): Entry<K, V>? {
        map.forEach { if (predicate(it)) return it }
        return null
    }

    open fun copy() = ObservableMap(this.map, entryHandler)

    fun mutableCopy() = MutableObservableMap(this.map, entryHandler)
}
