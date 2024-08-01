package com.sg8.collections.reactive.set

import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import com.sg8.collections.reactive.collection.CollectionSubscription


data class SetSubscription<T>(
    override val anyChange: ObservableSubscription<Pair<Set<T>, T>>,
    override val addition: ObservableSubscription<Pair<Set<T>, T>>? = null,
    override val removal: ObservableSubscription<Pair<Set<T>, T>>? = null,
) : CollectionSubscription<T, Set<T>>
