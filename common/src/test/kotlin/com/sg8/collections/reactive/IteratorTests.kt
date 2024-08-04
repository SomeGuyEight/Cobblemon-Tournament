package com.sg8.collections.reactive

import com.sg8.collections.reactive.collection.MutableObservableCollection
import com.sg8.collections.reactive.collection.ObservableCollection
import com.sg8.collections.reactive.list.MutableObservableSubList
import com.sg8.collections.reactive.map.MutableObservableMap
import com.sg8.collections.reactive.map.ObservableMap
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

object IteratorTests {

    @Test
    fun setIterator() {
        val testSets = TestSets()
        val inputSet = testSets.inputSet
        val mutableInputSet = testSets.inputSet.toMutableSet()
        val set = testSets.set
        val mutableSet = testSets.mutableSet
        val emptySet = testSets.emptySet
        val emptyMutableSet = testSets.emptyMutableSet

        iteratorTest(inputSet, mutableInputSet, set, mutableSet, emptySet, emptyMutableSet)
        bulkMutationTest(inputSet, mutableInputSet, set, mutableSet, emptySet, emptyMutableSet)
    }


    @Test
    fun listIterator() {
        val testLists = TestLists()
        val inputList = testLists.inputList
        val mutableInputList = testLists.inputList.toMutableList()
        val list = testLists.list
        val mutableList = testLists.mutableList
        val emptyList = testLists.emptyList
        val emptyMutableList = testLists.emptyMutableList

        iteratorTest(inputList, mutableInputList, list, mutableList, emptyList, emptyMutableList)
        bulkMutationTest(inputList, mutableInputList, list, mutableList, emptyList, emptyMutableList)
    }

    @Test
    fun subListIterator() {
        val testLists = TestLists()
        val inputSubList = testLists.inputList.subList(1, 3)
        val mutableInputList = testLists.inputList.toMutableList()
        val mutableInputSubList = mutableInputList.subList(1, 3)
        val subList = testLists.list.subList(1, 3)
        val mutableSubList = testLists.mutableList.subList(1, 3)

        subListIteratorTest(testLists, inputSubList, mutableInputSubList, subList, mutableSubList)
        subListBulkMutationTest(testLists, mutableInputList, inputSubList, mutableInputSubList, mutableSubList)
    }

    private fun subListIteratorTest(
        testLists: TestLists,
        inputSubList: List<String>,
        mutableInputSubList: MutableList<String>,
        subList: List<String>,
        mutableSubList: MutableObservableSubList<String>,
    ) {
        var inputSubListCount = 0
        var mutableInputSubListCount = 0
        var subListCount = 0
        var mutableSubListCount = 0

        inputSubList.iterator().forEach { _ -> inputSubListCount++ }
        mutableInputSubList.iterator().forEach { _ -> mutableInputSubListCount++ }
        subList.iterator().forEach { _ -> subListCount++ }
        mutableSubList.iterator().forEach { _ -> mutableSubListCount++ }

        Assertions.assertTrue(inputSubListCount == inputSubList.size)
        Assertions.assertTrue(mutableInputSubListCount == mutableInputSubList.size)
        Assertions.assertTrue(subListCount == subList.size)
        Assertions.assertTrue(mutableSubListCount == mutableSubList.size)
    }

    private fun subListBulkMutationTest(
        testLists: TestLists,
        mutableInputList: MutableList<String>,
        inputSubList: List<String>,
        mutableInputSubList: MutableList<String>,
        mutableSubList: MutableObservableSubList<String>,
    ) {
        // confirm mutations via iterator internally are tracked & reflected consistent with
        // kotlin mutable set implementations
        mutableInputSubList.removeAll(inputSubList)
        mutableSubList.removeAll(inputSubList)

        Assertions.assertTrue(mutableSubList.containsAll(mutableInputSubList))
        Assertions.assertTrue(mutableInputSubList.containsAll(mutableSubList))

        Assertions.assertTrue(inputSubList.containsAll(mutableInputSubList))
        Assertions.assertTrue(!mutableInputSubList.containsAll(inputSubList))
        Assertions.assertTrue(inputSubList.containsAll(mutableSubList))
        Assertions.assertTrue(!mutableSubList.containsAll(inputSubList))

        mutableInputSubList.addAll(inputSubList)
        mutableSubList.addAll(inputSubList)

        Assertions.assertTrue(inputSubList.containsAll(mutableInputSubList))
        Assertions.assertTrue(mutableInputSubList.containsAll(inputSubList))
        Assertions.assertTrue(inputSubList.containsAll(mutableSubList))
        Assertions.assertTrue(mutableSubList.containsAll(inputSubList))

        mutableInputSubList.retainAll(inputSubList)
        mutableSubList.retainAll(inputSubList)

        Assertions.assertTrue(inputSubList.containsAll(mutableInputSubList))
        Assertions.assertTrue(mutableInputSubList.containsAll(inputSubList))
        Assertions.assertTrue(inputSubList.containsAll(mutableSubList))
        Assertions.assertTrue(mutableSubList.containsAll(inputSubList))
        Assertions.assertTrue(testLists.mutableList.containsAll(mutableInputList))
        Assertions.assertTrue(testLists.mutableList.size == mutableInputList.size)

        // back to starting lists
        mutableInputSubList.clear()
        mutableSubList.clear()
        Assertions.assertTrue(inputSubList.containsAll(mutableInputSubList))
        Assertions.assertTrue(!mutableInputSubList.containsAll(inputSubList))
        Assertions.assertTrue(inputSubList.containsAll(mutableSubList))
        Assertions.assertTrue(!mutableSubList.containsAll(inputSubList))
    }

    @Test
    fun mapIterator() {
        val testMaps = TestMaps()
        val inputMap = testMaps.inputMap
        val mutableInputMap = testMaps.inputMap.toMutableMap()
        val map = testMaps.map
        val mutableMap = testMaps.mutableMap
        val emptyMap = testMaps.emptyMap
        val emptyMutableMap = testMaps.emptyMutableMap

        mapIteratorTest(inputMap, mutableInputMap, map, mutableMap, emptyMap, emptyMutableMap)

        // confirm mutations are tracked & reflected

    }

    private fun mapIteratorTest(
        inputMap: Map<Int, String>,
        mutableInputMap: MutableMap<Int, String>,
        map: ObservableMap<Int, String>,
        mutableMap: MutableObservableMap<Int, String>,
        emptyMap: ObservableMap<Int, String>,
        emptyMutableMap: MutableObservableMap<Int, String>,
    ) {
        var inputCount = 0
        var mutableInputCount = 0
        var mapCount = 0
        var mutableMapCount = 0
        var emptyMapCount = 0
        var emptyMutableMapCount = 0

        inputMap.iterator().forEach { _ -> inputCount++ }
        mutableInputMap.iterator().forEach { _ -> mutableInputCount++ }
        map.iterator().forEach { _ -> mapCount++ }
        mutableMap.iterator().forEach { _ -> mutableMapCount++ }
        emptyMap.iterator().forEach { _ -> emptyMapCount++ }
        emptyMutableMap.iterator().forEach { _ -> emptyMutableMapCount++ }

        Assertions.assertTrue(inputCount == inputMap.size)
        Assertions.assertTrue(mutableInputCount == mutableInputMap.size)
        Assertions.assertTrue(mapCount == map.size)
        Assertions.assertTrue(mutableMapCount == mutableMap.size)
        Assertions.assertTrue(emptyMapCount == emptyMap.size)
        Assertions.assertTrue(emptyMutableMapCount == emptyMutableMap.size)
    }


    private fun iteratorTest(
        inputCollection: Collection<String>,
        mutableInputCollection: MutableCollection<String>,
        collection: ObservableCollection<String, *>,
        mutableCollection: MutableObservableCollection<String, *>,
        emptyCollection: ObservableCollection<String, *>,
        emptyMutableCollection: MutableObservableCollection<String, *>,
    ) {
        var inputCount = 0
        var mutableInputCount = 0
        var collectionCount = 0
        var mutableCollectionCount = 0
        var emptyCollectionCount = 0
        var emptyMutableCollectionCount = 0

        inputCollection.iterator().forEach { _ -> inputCount++ }
        mutableInputCollection.iterator().forEach { _ -> mutableInputCount++ }
        collection.iterator().forEach { _ -> collectionCount++ }
        mutableCollection.iterator().forEach { _ -> mutableCollectionCount++ }
        emptyCollection.iterator().forEach { _ -> emptyCollectionCount++ }
        emptyMutableCollection.iterator().forEach { _ -> emptyMutableCollectionCount++ }

        Assertions.assertTrue(inputCount == inputCollection.size)
        Assertions.assertTrue(mutableInputCount == mutableInputCollection.size)
        Assertions.assertTrue(collectionCount == collection.size)
        Assertions.assertTrue(mutableCollectionCount == mutableCollection.size)
        Assertions.assertTrue(emptyCollectionCount == emptyCollection.size)
        Assertions.assertTrue(emptyMutableCollectionCount == emptyMutableCollection.size)
    }

    private fun bulkMutationTest(
        inputCollection: Collection<String>,
        mutableInputCollection: MutableCollection<String>,
        collection: ObservableCollection<String, *>,
        mutableCollection: MutableObservableCollection<String, *>,
        emptyCollection: ObservableCollection<String, *>,
        emptyMutableCollection: MutableObservableCollection<String, *>,
    ) {
        // confirm mutations via iterator internally are tracked & reflected consistent with
        // kotlin mutable set implementations
        mutableInputCollection.removeAll(inputCollection)
        mutableCollection.removeAll(inputCollection)
        emptyMutableCollection.removeAll(inputCollection)
        iteratorTest(inputCollection, mutableInputCollection, collection, mutableCollection, emptyCollection, emptyMutableCollection)

        Assertions.assertTrue(inputCollection.containsAll(mutableInputCollection))
        Assertions.assertTrue(!mutableInputCollection.containsAll(inputCollection))
        Assertions.assertTrue(inputCollection.containsAll(mutableCollection))
        Assertions.assertTrue(!mutableCollection.containsAll(inputCollection))
        Assertions.assertTrue(emptyMutableCollection.isEmpty())

        mutableInputCollection.addAll(inputCollection)
        mutableCollection.addAll(inputCollection)
        emptyMutableCollection.addAll(inputCollection)
        iteratorTest(inputCollection, mutableInputCollection, collection, mutableCollection, emptyCollection, emptyMutableCollection)

        Assertions.assertTrue(inputCollection.containsAll(mutableInputCollection))
        Assertions.assertTrue(mutableInputCollection.containsAll(inputCollection))
        Assertions.assertTrue(inputCollection.containsAll(mutableCollection))
        Assertions.assertTrue(mutableCollection.containsAll(inputCollection))
        Assertions.assertTrue(emptyMutableCollection.isNotEmpty())

        mutableInputCollection.retainAll(inputCollection)
        mutableCollection.retainAll(inputCollection)
        emptyMutableCollection.retainAll(inputCollection)
        iteratorTest(inputCollection, mutableInputCollection, collection, mutableCollection, emptyCollection, emptyMutableCollection)

        Assertions.assertTrue(inputCollection.containsAll(mutableInputCollection))
        Assertions.assertTrue(mutableInputCollection.containsAll(inputCollection))
        Assertions.assertTrue(inputCollection.containsAll(mutableCollection))
        Assertions.assertTrue(mutableCollection.containsAll(inputCollection))
        Assertions.assertTrue(emptyMutableCollection.isNotEmpty())
    }
}
