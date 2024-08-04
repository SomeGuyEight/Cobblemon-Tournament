package com.sg8.collections.reactive.list

import com.cobblemon.mod.common.api.reactive.Observable
import com.sg8.collections.reactive.collection.getElementObservables


inline fun <reified T, reified C : Collection<T>> C.toObservableList(
    noinline elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
) = ObservableList(this, elementHandler)


inline fun <reified T, reified C : Collection<T>> C.toMutableObservableList(
    noinline elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
) = MutableObservableList(this, elementHandler)


fun <T> observableListOf(
    vararg elements: T,
    elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
): ObservableList<T> = ObservableList(elements.toList(), elementHandler)


fun <T> mutableObservableListOf(
    vararg elements: T,
    elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
): MutableObservableList<T> = MutableObservableList(elements.toList(), elementHandler)


fun <T> observableListOf(
    elements: Collection<T> = setOf(),
    elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
): ObservableList<T> = ObservableList(elements, elementHandler)


fun <T> mutableObservableListOf(
    elements: Collection<T> = setOf(),
    elementHandler: (T) -> Set<Observable<*>> = { it.getElementObservables() },
): MutableObservableList<T> = MutableObservableList(elements, elementHandler)
