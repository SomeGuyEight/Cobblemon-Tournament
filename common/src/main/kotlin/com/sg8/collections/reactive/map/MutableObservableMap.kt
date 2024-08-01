package com.sg8.collections.reactive.map

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.reactive.*
import kotlin.collections.Map.Entry


private class ObservableEntry<K, V>(override val key: K, override val value: V) : Entry<K, V>


class MutableObservableMap<K, V>(
    map: Map<K, V> = mapOf(),
    entryHandler: (Entry<K, V>) -> Set<Observable<*>> = { it.getObservables() },
) : ObservableMap<K, V>(map = map, entryHandler = entryHandler),
    MutableMap<K, V> {

    private val additionObservable = SimpleObservable<Pair<Map<K, V>, Entry<K, V>>>()
    private val removalObservable = SimpleObservable<Pair<Map<K, V>, Entry<K, V>>>()

    fun subscribe(
        priority: Priority,
        anyChangeHandler: (Pair<Map<K, V>, Entry<K, V>>) -> Unit,
        additionHandler: ((Pair<Map<K, V>, Entry<K, V>>) -> Unit)? = null,
        removalHandler: ((Pair<Map<K, V>, Entry<K, V>>) -> Unit)? = null,
    ): MapSubscription<K, V> {
        return MapSubscription(
            anyChange = this.subscribe(priority, anyChangeHandler),
            addition = additionHandler?.let { additionObservable.subscribe(priority, it) },
            removal = removalHandler?.let { removalObservable.subscribe(priority, it) },
        )
    }

    override fun register(entry: Entry<K, V>): Boolean {
        entryHandler(entry).forEach{ observable ->
            subscriptionMap[observable] = observable.subscribe { emitAnyChange(entry) }
        }
        emitAddition(entry)
        return emitAnyChange(entry)
    }

    private fun unregister(entry: Entry<K, V>): Boolean {
        entryHandler(entry).forEach{ subscriptionMap.remove(it)?.unsubscribe() }
        emitRemoval(entry)
        return emitAnyChange(entry)
    }

    private fun emitAddition(entry: Entry<K, V>) = additionObservable.emit(map to entry)

    private fun emitRemoval(entry: Entry<K, V>) = removalObservable.emit(map to entry)

    override operator fun iterator(): MutableIterator<Entry<K, V>> {
        return MutableObservableMapIterator(this)
    }

    operator fun set(key: K, value: V): V? = put(key, value)

    override fun put(key: K, value: V): V? {
        val previous = map.put(key, value)
        if (previous != null) {
            unregister(ObservableEntry(key, value))
        }
        return previous
    }

    fun remove(key: K, value: V) = if (map[key] == value) (remove(key) != null) else false

    override fun remove(key: K): V? {
        val value = map.remove(key) ?: return null
        unregister(ObservableEntry(key, value))
        return value
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { put(it.key, it.value) }
    }

    fun getOrDefault(key: K, defaultValue: V): V = map[key] ?: defaultValue

    fun getOrPut(key: K, newValue: V) = map[key] ?: newValue.also { map[key] = it }

    override fun clear() {
        if (map.isNotEmpty()) {
            val removedEntries = map.entries
            map.clear()
            removedEntries.forEach { unregister(it) }
        }
    }
}
