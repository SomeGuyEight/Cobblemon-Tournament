package com.sg8.collections.reactive.set

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.collection.getElementObservables


inline fun <reified T, C : Collection<T>> C.toObservableSet(
    noinline elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
) = ObservableSet(this, elementHandler)


inline fun <reified T, reified C : Collection<T>> C.toMutableObservableSet(
    noinline elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
) = MutableObservableSet(this, elementHandler)


fun <T> observableSetOf(
    vararg elements: T,
    elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
): ObservableSet<T> = ObservableSet(elements.toSet(), elementHandler)


fun <T> mutableObservableSetOf(
    vararg elements: T,
    elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
): MutableObservableSet<T> = MutableObservableSet(elements.toSet(), elementHandler)


fun <T> observableSetOf(
    elements: Collection<T> = setOf(),
    elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
): ObservableSet<T> = ObservableSet(elements, elementHandler)


fun <T> mutableObservableSetOf(
    elements: Collection<T> = setOf(),
    elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
): MutableObservableSet<T> = MutableObservableSet(elements, elementHandler)
