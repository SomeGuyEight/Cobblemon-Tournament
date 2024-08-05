package com.sg8.collections.reactive.subscriptions

import com.cobblemon.mod.common.api.reactive.ObservableSubscription

data class MapSubscription<K, T>(
    val anyChange: ObservableSubscription<Pair<Map<K, T>, Pair<K, T>>>,
    val addition: ObservableSubscription<Pair<Map<K, T>, Pair<K, T>>>? = null,
    val removal: ObservableSubscription<Pair<Map<K, T>, Pair<K, T>>>? = null,
) {

    fun unsubscribe() {
        anyChange.unsubscribe()
        addition?.unsubscribe()
        removal?.unsubscribe()
    }
}
