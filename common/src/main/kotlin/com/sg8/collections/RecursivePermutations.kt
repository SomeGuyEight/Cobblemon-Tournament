package com.sg8.collections


fun <T>  List<T>.allPermutations(): ArrayDeque<ArrayDeque<T>> {
    val combinations = ArrayDeque<ArrayDeque<T>>()
    recursivePermutations(
        input = ArrayDeque(this),
        index = 0,
        handler = { combinations.addLast(it) },
    )
    return combinations
}


fun <T> List<T>.allPermutations(handler: (MutableList<T>) -> Unit) {
    recursivePermutations(input = ArrayDeque(this), index = 0, handler = handler)
}


private fun <T> recursivePermutations(
    input: ArrayDeque<T>,
    index: Int,
    handler: (ArrayDeque<T>) -> Unit
) {
    if (index == input.lastIndex) {
        handler.invoke(input)
    }
    for (i in index until input.size) {
        input.swap(index, i)
        recursivePermutations(input = input, index = index + 1, handler = handler)
        input.swap(i, index)
    }
}


private fun <T> MutableList<T>.swap(indexOne: Int, indexTwo: Int) {
    val elementOne = this.removeAt(indexOne)
    this.add(indexOne, this.removeAt(indexTwo))
    this[indexTwo] = elementOne
}
