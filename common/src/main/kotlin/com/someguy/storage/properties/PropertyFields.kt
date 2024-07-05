package com.someguy.storage.properties

import com.cobblemon.mod.common.api.reactive.Observable

/**
 * This is just a generic interface that links a [MutableProperties] & [Properties] together.
 *
 * Any val/var implemented by a pair of Properties & MutableProperties should be included in their shared PropertyFields contract
 */
interface PropertyFields
{
    fun printProperties()
    fun getAllObservables(): Iterable<Observable<*>>
    fun getChangeObservable(): Observable<*>
}
