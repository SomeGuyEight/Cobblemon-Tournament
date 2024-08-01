package com.sg8.collections


/** Returns a set with only elements [other] does not contain */
fun <T> Collection<T>.unsharedElements(other: Collection<T>): Set<T> {
    val newElements = mutableSetOf<T>()
    this.forEach { if (!other.contains(it)) newElements.add(it) }
    return newElements
}


/** Returns a set with only elements [other] does not contain */
fun <T> Collection<T>.sharedElements(other: Collection<T>): Set<T> {
    val sharedElements = mutableSetOf<T>()
    this.forEach { if (other.contains(it)) sharedElements.add(it) }
    return sharedElements
}


fun <T, C: MutableCollection<T>> C.addIf(element: T, predicate: (T) -> Boolean): Boolean {
    return if (predicate(element)) this.add(element) else false
}
