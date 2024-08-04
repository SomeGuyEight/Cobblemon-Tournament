package com.sg8.collections

import kotlin.collections.Map.Entry


fun <K, V> Entry<K, V>.pair() = this.key to this.value


fun <K, V> Collection<Entry<K, V>>.pairs(): Set<Pair<K, V>> {
    mutableSetOf<Pair<K, V>>().let { set ->
        this.forEach { set.add(it.key to it.value) }
        return set
    }
}


fun <K, V> Map<K, V>.pairs(): Set<Pair<K, V>> {
    mutableSetOf<Pair<K, V>>().let { set ->
        this.forEach { set.add(it.key to it.value) }
        return set
    }
}


fun <K, V> Collection<Entry<K, V>>.toMap(): Map<K, V> = this.toMutableMap()


fun <K, V> Collection<Entry<K, V>>.toMutableMap(): MutableMap<K, V> {
    return mutableMapOf<K, V>().putAll(this)
}


fun <K, V> MutableMap<K, V>.putAll(entries: Collection<Entry<K, V>>): MutableMap<K, V> {
    return entries.forEach { this[it.key] = it.value }.let { this }
}
