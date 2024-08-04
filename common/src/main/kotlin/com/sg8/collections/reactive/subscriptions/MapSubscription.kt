package com.sg8.collections.reactive.subscriptions

import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import kotlin.collections.Map.Entry

data class MapSubscription<K, T>(
    val anyChange: ObservableSubscription<Pair<Map<K, T>, Entry<K, T>>>,
    val addition: ObservableSubscription<Pair<Map<K, T>, Entry<K, T>>>? = null,
    val removal: ObservableSubscription<Pair<Map<K, T>, Entry<K, T>>>? = null,
)
