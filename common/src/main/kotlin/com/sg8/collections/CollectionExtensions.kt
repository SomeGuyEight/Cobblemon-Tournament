package com.sg8.collections


/** Returns a set with only elements [other] does not contain */
fun <T> Collection<T>.unsharedElements(other: Collection<T>): Set<T> {
    val newElements = mutableSetOf<T>()
    this.forEach { if (!other.contains(it)) newElements.add(it) }
    return newElements
}


/** Returns a set with only elements [other] does not contain */
fun <T> Collection<T>.sharedElements(other: Collection<T>): List<T> = filter(other::contains)


fun <T, C: MutableCollection<T>> C.addIf(element: T, predicate: (T) -> Boolean): Boolean {
    return if (predicate(element)) this.add(element) else false
}


fun <T> MutableIterator<T>.removeIf(predicate: (T) -> Boolean): Boolean {
    var removed = false
    this.forEach { element ->
        if (predicate(element)) {
            this.remove()
            if (!removed) {
                removed = true
            }
        }
    }
    return removed
}
