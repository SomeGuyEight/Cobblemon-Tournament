package com.sg8.collections.reactive.collection

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.tryGetObservable


/**
 * This attempts to cast this value to [Observable].
 *
 * @return An observable set of this value as an [Observable] or an empty set.
 */
fun <T> T.getElementObservables(): Set<Observable<*>> {
    this.tryGetObservable()?.let { return setOf(it) }
    return emptySet()
}
