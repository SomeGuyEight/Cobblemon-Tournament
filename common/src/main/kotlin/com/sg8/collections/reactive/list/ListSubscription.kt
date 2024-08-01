package com.sg8.collections.reactive.list

import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import com.sg8.collections.reactive.collection.CollectionSubscription


data class ListSubscription<T>(
    override val anyChange: ObservableSubscription<Pair<List<T>, T>>,
    override val addition: ObservableSubscription<Pair<List<T>, T>>? = null,
    override val removal: ObservableSubscription<Pair<List<T>, T>>? = null,
    /**
     * Any mutation in the underlying [MutableObservableList] will emit the list
     * & a set of all elements impacted by the order mutation
     */
    val orderMutation: ObservableSubscription<Pair<List<T>, Set<T>>>? = null,
) : CollectionSubscription<T, List<T>>
