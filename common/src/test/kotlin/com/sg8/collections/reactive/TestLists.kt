package com.sg8.collections.reactive

import com.sg8.collections.reactive.list.MutableObservableList
import com.sg8.collections.reactive.list.ObservableList

class TestLists(val inputList: List<String> = mutableListOf("zero", "one", "two", "three")) {

    val list: ObservableList<String> = ObservableList(inputList)
    val mutableList: MutableObservableList<String> = MutableObservableList(inputList)
    val emptyList = ObservableList<String>(setOf())
    val emptyMutableList = MutableObservableList<String>()
}
