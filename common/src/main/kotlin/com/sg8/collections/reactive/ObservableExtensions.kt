package com.sg8.collections.reactive

import com.cobblemon.mod.common.api.reactive.Observable


/**
 * This attempts to cast this value to [Observable].
 *
 * @return This value as an [Observable] or null.
 */
fun <T> T.tryGetObservable(): Observable<*>? = if (this is Observable<*>) this else null
