package com.someguy.storage.util

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.ObservableSubscription

typealias SubscriptionMap = MutableMap<Observable<*>, ObservableSubscription<*>>

fun <T, O: Observable<T>> O.registerObservable(
    observables: SubscriptionMap,
    handler: () -> Unit,
): O {
    observables[this] = this.subscribe { handler.invoke() }
    return this
}
