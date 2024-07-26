package com.cobblemontournament.common.util

/**
 * The input list is not mutated
 *
 * @return [ArrayDeque] of every unique permutation of every unique permutation.
 */
fun <T>  List<T>.allPermutations(): ArrayDeque<ArrayDeque<T>> {
    val combinations = ArrayDeque<ArrayDeque<T>>()
    recursivePermutations(
        input = this.toMutableList(),
        index = 0,
        handler = { combinations.addLast(it.toQueue()) },
    )
    return combinations
}

/**
 * Invokes [handler] with every unique permutation.
 *
 * The input list is not mutated.
 */
fun <T> List<T>.allPermutations(handler: (MutableList<T>) -> Unit) {
    recursivePermutations(
        input = this.toMutableList(),
        index = 0,
        handler = handler,
    )
}

/**
 * Invokes [handler] with every unique permutation of [input].
 */
private fun <T> recursivePermutations(
    input: MutableList<T>,
    index: Int,
    handler: (MutableList<T>) -> Unit
) {
    if (index == input.lastIndex) {
        handler.invoke(input)
    }
    for (i in index until input.size) {
        input.swap(index, i)
        recursivePermutations(
            input = input,
            index = index + 1,
            handler = handler,
        )
        input.swap(i, index)
    }
}

private fun <T> List<T>.toQueue(): ArrayDeque<T> {
    val queue = ArrayDeque<T>()
    this.forEach { queue.addLast(it) }
    return queue
}

private fun <T> MutableList<T>.swap(indexOne: Int, indexTwo: Int) {
    val elementOne = this[indexOne]
    val elementTwo = this[indexTwo]
    this.add(indexOne, elementTwo)
    this.removeAt(index = indexOne + 1)
    this.add(indexTwo, elementOne)
    this.removeAt(index = indexTwo + 1)
}
