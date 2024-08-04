package com.sg8.collections.reactive.subscriptions

import com.cobblemon.mod.common.api.reactive.ObservableSubscription


/**
 * Immutable observable collections implement an [anyChange] subscription directly.
 *
 * Mutable observable collections implement [anyChange], [addition],
 * & [removal] subscriptions through a [CollectionSubscription].
 *
 * @property anyChange Emits the collection paired with the element mutation.
 * This emits with any mutation of the collection or a mutation of the
 * collection's observable elements.
 *
 * @property addition Emits the collection paired with a set of the elements mutated.
 * This emits when any collection mutation results from the addition of an element.
 *
 * @property removal Emits the collection paired with a set of the elements mutated.
 * This emits when any collection mutation results from the removal of an element.
 *
 * @see ListSubscription.swap
 */
interface CollectionSubscription<T, C : Collection<T>> {

    val anyChange: ObservableSubscription<Pair<C, T>>
    val addition: ObservableSubscription<Pair<C, T>>?
    val removal: ObservableSubscription<Pair<C, T>>?

    fun unsubscribe()
}
