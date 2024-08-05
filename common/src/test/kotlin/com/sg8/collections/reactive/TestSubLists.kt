package com.sg8.collections.reactive

class TestSubLists<T>(
    input: List<T>,
    subIndices: Pair<Int, Int> = 1 to 3,
    subSubIndices: Pair<Int, Int> = 0 to 1,
) : TestLists<T>(input) {

    val inputSub = this.input.subList(subIndices.first, subIndices.second)
    val inputSubSub = inputSub.subList(subSubIndices.first, subSubIndices.second)
    val mutableInputSub = mutableInput.subList(subIndices.first, subIndices.second)
    val mutableInputSubSub = mutableInputSub.subList(subSubIndices.first, subSubIndices.second)
    val observableSub = observable.subList(subIndices.first, subIndices.second)
    val observableSubSub = observableSub.subList(subSubIndices.first, subSubIndices.second)
    val mutableObservableSub = mutableObservable.subList(subIndices.first, subIndices.second)
    val mutableObservableSubSub = mutableObservableSub.subList(subSubIndices.first, subSubIndices.second)

    companion object {
        fun default() = testSubListsOf("zero", "one", "two", "three")
    }
}

fun <T> testSubListsOf(vararg elements: T) = TestSubLists(listOf(*elements))
