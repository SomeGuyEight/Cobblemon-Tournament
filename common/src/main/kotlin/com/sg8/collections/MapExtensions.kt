package com.sg8.collections

import kotlin.collections.Map.Entry


fun <K, V> Entry<K, V>.toPair() = this.key to this.value


fun <K, V> Map<K, V>.toPairSet(): Set<Pair<K, V>> {
    mutableSetOf<Pair<K, V>>().let { set ->
        this.forEach { set.add(it.key to it.value) }
        return set
    }
}
