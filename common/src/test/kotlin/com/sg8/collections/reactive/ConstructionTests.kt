package com.sg8.collections.reactive

import com.sg8.collections.pairs
import com.sg8.collections.reactive.list.*
import com.sg8.collections.reactive.map.*
import com.sg8.collections.reactive.set.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


object ConstructionTests {

    @Test
    fun setConstruction() {
        val testSets = TestSets()

        val inputSet = testSets.inputSet
        val set = testSets.set
        val mutableSet = testSets.mutableSet
        val emptySet = testSets.emptySet
        val emptyMutableSet = testSets.emptyMutableSet

        Assertions.assertTrue(set.containsAll(inputSet))
        Assertions.assertTrue(mutableSet.containsAll(inputSet))
        Assertions.assertTrue(emptySet.isEmpty())
        Assertions.assertTrue(emptyMutableSet.isEmpty())

        Assertions.assertTrue(ObservableSet(inputSet).containsAll(inputSet))
        Assertions.assertTrue(ObservableSet(set).containsAll(inputSet))
        Assertions.assertTrue(ObservableSet(mutableSet).containsAll(inputSet))
        Assertions.assertTrue(ObservableSet(emptySet).isEmpty())
        Assertions.assertTrue(ObservableSet(emptyMutableSet).isEmpty())
        Assertions.assertTrue(ObservableSet<String>(inputSet).containsAll(inputSet))
        Assertions.assertTrue(ObservableSet<String>(set).containsAll(inputSet))
        Assertions.assertTrue(ObservableSet<String>(mutableSet).containsAll(inputSet))
        Assertions.assertTrue(ObservableSet<String>(emptySet).isEmpty())
        Assertions.assertTrue(ObservableSet<String>(emptyMutableSet).isEmpty())

        Assertions.assertTrue(MutableObservableSet(inputSet).containsAll(inputSet))
        Assertions.assertTrue(MutableObservableSet(set).containsAll(inputSet))
        Assertions.assertTrue(MutableObservableSet(mutableSet).containsAll(inputSet))
        Assertions.assertTrue(MutableObservableSet(emptySet).isEmpty())
        Assertions.assertTrue(MutableObservableSet(emptyMutableSet).isEmpty())
        Assertions.assertTrue(MutableObservableSet<String>(inputSet).containsAll(inputSet))
        Assertions.assertTrue(MutableObservableSet<String>(set).containsAll(inputSet))
        Assertions.assertTrue(MutableObservableSet<String>(mutableSet).containsAll(inputSet))
        Assertions.assertTrue(MutableObservableSet<String>(emptySet).isEmpty())
        Assertions.assertTrue(MutableObservableSet<String>(emptyMutableSet).isEmpty())

        Assertions.assertTrue(inputSet.toObservableSet().containsAll(inputSet))
        Assertions.assertTrue(set.toObservableSet().containsAll(inputSet))
        Assertions.assertTrue(mutableSet.toObservableSet().containsAll(inputSet))
        Assertions.assertTrue(emptySet.toObservableSet().isEmpty())
        Assertions.assertTrue(emptyMutableSet.toObservableSet().isEmpty())
        Assertions.assertTrue(inputSet.toObservableSet<String, Collection<String>>().containsAll(inputSet))
        Assertions.assertTrue(set.toObservableSet<String, Collection<String>>().containsAll(inputSet))
        Assertions.assertTrue(mutableSet.toObservableSet<String, Collection<String>>().containsAll(inputSet))
        Assertions.assertTrue(emptySet.toObservableSet<String, Collection<String>>().isEmpty())
        Assertions.assertTrue(emptyMutableSet.toObservableSet<String, Collection<String>>().isEmpty())

        Assertions.assertTrue(inputSet.toMutableObservableSet().containsAll(inputSet))
        Assertions.assertTrue(set.toMutableObservableSet().containsAll(inputSet))
        Assertions.assertTrue(mutableSet.toMutableObservableSet().containsAll(inputSet))
        Assertions.assertTrue(emptySet.toMutableObservableSet().isEmpty())
        Assertions.assertTrue(emptyMutableSet.toMutableObservableSet().isEmpty())
        Assertions.assertTrue(inputSet.toMutableObservableSet<String, Collection<String>>().containsAll(inputSet))
        Assertions.assertTrue(set.toMutableObservableSet<String, Collection<String>>().containsAll(inputSet))
        Assertions.assertTrue(mutableSet.toMutableObservableSet<String, Collection<String>>().containsAll(inputSet))
        Assertions.assertTrue(emptySet.toMutableObservableSet<String, Collection<String>>().isEmpty())
        Assertions.assertTrue(emptyMutableSet.toMutableObservableSet<String, Collection<String>>().isEmpty())

        Assertions.assertTrue(observableSetOf(inputSet).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf(set).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf(mutableSet).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf(emptySet).isEmpty())
        Assertions.assertTrue(observableSetOf(emptyMutableSet).isEmpty())
        Assertions.assertTrue(observableSetOf<String>(inputSet).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf<String>(set).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf<String>(mutableSet).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf<String>(emptySet).isEmpty())
        Assertions.assertTrue(observableSetOf<String>(emptyMutableSet).isEmpty())

        Assertions.assertTrue(mutableObservableSetOf(inputSet).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf(set).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf(mutableSet).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf(emptySet).isEmpty())
        Assertions.assertTrue(mutableObservableSetOf(emptyMutableSet).isEmpty())
        Assertions.assertTrue(mutableObservableSetOf<String>(inputSet).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf<String>(set).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf<String>(mutableSet).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf<String>(emptySet).isEmpty())
        Assertions.assertTrue(mutableObservableSetOf<String>(emptyMutableSet).isEmpty())

        val ordered = inputSet.toList()
        Assertions.assertTrue(observableSetOf(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf<String>(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf<String>(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(observableSetOf<String>(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))

        Assertions.assertTrue(mutableObservableSetOf(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf<String>(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf<String>(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
        Assertions.assertTrue(mutableObservableSetOf<String>(ordered[0], ordered[1], ordered[2], ordered[3]).containsAll(inputSet))
    }

    @Test
    fun listConstruction() {
        val testLists = TestLists()

        val inputList = testLists.inputList
        val list = testLists.list
        val mutableList = testLists.mutableList
        val emptyList = testLists.emptyList
        val emptyMutableList = testLists.emptyMutableList

        Assertions.assertTrue(list.containsAll(inputList))
        Assertions.assertTrue(mutableList.containsAll(inputList))
        Assertions.assertTrue(emptyList.isEmpty())
        Assertions.assertTrue(emptyMutableList.isEmpty())

        Assertions.assertTrue(ObservableList(inputList).containsAll(inputList))
        Assertions.assertTrue(ObservableList(list).containsAll(inputList))
        Assertions.assertTrue(ObservableList(mutableList).containsAll(inputList))
        Assertions.assertTrue(ObservableList(emptyList).isEmpty())
        Assertions.assertTrue(ObservableList(emptyMutableList).isEmpty())
        Assertions.assertTrue(ObservableList<String>(inputList).containsAll(inputList))
        Assertions.assertTrue(ObservableList<String>(list).containsAll(inputList))
        Assertions.assertTrue(ObservableList<String>(mutableList).containsAll(inputList))
        Assertions.assertTrue(ObservableList<String>(emptyList).isEmpty())
        Assertions.assertTrue(ObservableList<String>(emptyMutableList).isEmpty())

        Assertions.assertTrue(MutableObservableList(inputList).containsAll(inputList))
        Assertions.assertTrue(MutableObservableList(list).containsAll(inputList))
        Assertions.assertTrue(MutableObservableList(mutableList).containsAll(inputList))
        Assertions.assertTrue(MutableObservableList(emptyList).isEmpty())
        Assertions.assertTrue(MutableObservableList(emptyMutableList).isEmpty())
        Assertions.assertTrue(MutableObservableList<String>(inputList).containsAll(inputList))
        Assertions.assertTrue(MutableObservableList<String>(list).containsAll(inputList))
        Assertions.assertTrue(MutableObservableList<String>(mutableList).containsAll(inputList))
        Assertions.assertTrue(MutableObservableList<String>(emptyList).isEmpty())
        Assertions.assertTrue(MutableObservableList<String>(emptyMutableList).isEmpty())

        Assertions.assertTrue(inputList.toObservableList().containsAll(inputList))
        Assertions.assertTrue(list.toObservableList().containsAll(inputList))
        Assertions.assertTrue(mutableList.toObservableList().containsAll(inputList))
        Assertions.assertTrue(emptyList.toObservableList().isEmpty())
        Assertions.assertTrue(emptyMutableList.toObservableList().isEmpty())
        Assertions.assertTrue(inputList.toObservableList<String, Collection<String>>().containsAll(inputList))
        Assertions.assertTrue(list.toObservableList<String, Collection<String>>().containsAll(inputList))
        Assertions.assertTrue(mutableList.toObservableList<String, Collection<String>>().containsAll(inputList))
        Assertions.assertTrue(emptyList.toObservableList<String, Collection<String>>().isEmpty())
        Assertions.assertTrue(emptyMutableList.toObservableList<String, Collection<String>>().isEmpty())

        Assertions.assertTrue(inputList.toMutableObservableList().containsAll(inputList))
        Assertions.assertTrue(list.toMutableObservableList().containsAll(inputList))
        Assertions.assertTrue(mutableList.toMutableObservableList().containsAll(inputList))
        Assertions.assertTrue(emptyList.toMutableObservableList().isEmpty())
        Assertions.assertTrue(emptyMutableList.toMutableObservableList().isEmpty())
        Assertions.assertTrue(inputList.toMutableObservableList<String, Collection<String>>().containsAll(inputList))
        Assertions.assertTrue(list.toMutableObservableList<String, Collection<String>>().containsAll(inputList))
        Assertions.assertTrue(mutableList.toMutableObservableList<String, Collection<String>>().containsAll(inputList))
        Assertions.assertTrue(emptyList.toMutableObservableList<String, Collection<String>>().isEmpty())
        Assertions.assertTrue(emptyMutableList.toMutableObservableList<String, Collection<String>>().isEmpty())

        Assertions.assertTrue(observableListOf(inputList).containsAll(inputList))
        Assertions.assertTrue(observableListOf(list).containsAll(inputList))
        Assertions.assertTrue(observableListOf(mutableList).containsAll(inputList))
        Assertions.assertTrue(observableListOf(emptyList).isEmpty())
        Assertions.assertTrue(observableListOf(emptyMutableList).isEmpty())
        Assertions.assertTrue(observableListOf<String>(inputList).containsAll(inputList))
        Assertions.assertTrue(observableListOf<String>(list).containsAll(inputList))
        Assertions.assertTrue(observableListOf<String>(mutableList).containsAll(inputList))
        Assertions.assertTrue(observableListOf<String>(emptyList).isEmpty())
        Assertions.assertTrue(observableListOf<String>(emptyMutableList).isEmpty())

        Assertions.assertTrue(mutableObservableListOf(inputList).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf(list).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf(mutableList).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf(emptyList).isEmpty())
        Assertions.assertTrue(mutableObservableListOf(emptyMutableList).isEmpty())
        Assertions.assertTrue(mutableObservableListOf<String>(inputList).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf<String>(list).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf<String>(mutableList).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf<String>(emptyList).isEmpty())
        Assertions.assertTrue(mutableObservableListOf<String>(emptyMutableList).isEmpty())

        Assertions.assertTrue(observableListOf(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(observableListOf(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(observableListOf(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(observableListOf<String>(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(observableListOf<String>(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(observableListOf<String>(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))

        Assertions.assertTrue(mutableObservableListOf(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf<String>(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf<String>(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))
        Assertions.assertTrue(mutableObservableListOf<String>(inputList[0], inputList[1], inputList[2], inputList[3]).containsAll(inputList))

    }

    @Test
    fun subListConstruction() {
        val testLists = TestLists()

        val inputSubList = testLists.inputList.subList(1, 3)
        val inputSubSubList = inputSubList.subList(0, 1)
        val subList = testLists.list.subList(1, 3)
        val subSubList = subList.subList(0, 1)
        val mutableSubList = testLists.mutableList.subList(1, 3)
        val mutableSubSubList = mutableSubList.subList(0, 1)

        Assertions.assertTrue(subList.containsAll(inputSubList))
        Assertions.assertTrue(mutableSubList.containsAll(inputSubList))

        Assertions.assertTrue(ObservableList(inputSubList).containsAll(inputSubList))
        Assertions.assertTrue(ObservableList(subList).containsAll(inputSubList))
        Assertions.assertTrue(ObservableList(mutableSubList).containsAll(inputSubList))
        Assertions.assertTrue(ObservableList(inputSubSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(ObservableList(subSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(ObservableList(mutableSubSubList).containsAll(inputSubSubList))

        Assertions.assertTrue(ObservableList<String>(inputSubList).containsAll(inputSubList))
        Assertions.assertTrue(ObservableList<String>(subList).containsAll(inputSubList))
        Assertions.assertTrue(ObservableList<String>(mutableSubList).containsAll(inputSubList))
        Assertions.assertTrue(ObservableList<String>(inputSubSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(ObservableList<String>(subSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(ObservableList<String>(mutableSubSubList).containsAll(inputSubSubList))

        Assertions.assertTrue(MutableObservableList(inputSubList).containsAll(inputSubList))
        Assertions.assertTrue(MutableObservableList(subList).containsAll(inputSubList))
        Assertions.assertTrue(MutableObservableList(mutableSubList).containsAll(inputSubList))
        Assertions.assertTrue(MutableObservableList(inputSubSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(MutableObservableList(subSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(MutableObservableList(mutableSubSubList).containsAll(inputSubSubList))

        Assertions.assertTrue(MutableObservableList<String>(inputSubList).containsAll(inputSubList))
        Assertions.assertTrue(MutableObservableList<String>(subList).containsAll(inputSubList))
        Assertions.assertTrue(MutableObservableList<String>(mutableSubList).containsAll(inputSubList))
        Assertions.assertTrue(MutableObservableList<String>(inputSubSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(MutableObservableList<String>(subSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(MutableObservableList<String>(mutableSubSubList).containsAll(inputSubSubList))

        Assertions.assertTrue(observableListOf(inputSubList).containsAll(inputSubList))
        Assertions.assertTrue(observableListOf(subList).containsAll(inputSubList))
        Assertions.assertTrue(observableListOf(mutableSubList).containsAll(inputSubList))
        Assertions.assertTrue(observableListOf(inputSubSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(observableListOf(subSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(observableListOf(mutableSubSubList).containsAll(inputSubSubList))

        Assertions.assertTrue(observableListOf<String>(inputSubList).containsAll(inputSubList))
        Assertions.assertTrue(observableListOf<String>(subList).containsAll(inputSubList))
        Assertions.assertTrue(observableListOf<String>(mutableSubList).containsAll(inputSubList))
        Assertions.assertTrue(observableListOf<String>(inputSubSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(observableListOf<String>(subSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(observableListOf<String>(mutableSubSubList).containsAll(inputSubSubList))

        Assertions.assertTrue(mutableObservableListOf(inputSubList).containsAll(inputSubList))
        Assertions.assertTrue(mutableObservableListOf(subList).containsAll(inputSubList))
        Assertions.assertTrue(mutableObservableListOf(mutableSubList).containsAll(inputSubList))
        Assertions.assertTrue(mutableObservableListOf(inputSubSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(mutableObservableListOf(subSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(mutableObservableListOf(mutableSubSubList).containsAll(inputSubSubList))

        Assertions.assertTrue(mutableObservableListOf<String>(inputSubList).containsAll(inputSubList))
        Assertions.assertTrue(mutableObservableListOf<String>(subList).containsAll(inputSubList))
        Assertions.assertTrue(mutableObservableListOf<String>(mutableSubList).containsAll(inputSubList))
        Assertions.assertTrue(mutableObservableListOf<String>(inputSubSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(mutableObservableListOf<String>(subSubList).containsAll(inputSubSubList))
        Assertions.assertTrue(mutableObservableListOf<String>(mutableSubSubList).containsAll(inputSubSubList))

        Assertions.assertTrue(inputSubList.toObservableList().containsAll(inputSubList))
        Assertions.assertTrue(subList.toObservableList().containsAll(inputSubList))
        Assertions.assertTrue(mutableSubList.toObservableList().containsAll(inputSubList))
        Assertions.assertTrue(inputSubSubList.toObservableList().containsAll(inputSubSubList))
        Assertions.assertTrue(subSubList.toObservableList().containsAll(inputSubSubList))
        Assertions.assertTrue(mutableSubSubList.toObservableList().containsAll(inputSubSubList))

        Assertions.assertTrue(inputSubList.toObservableList<String, Collection<String>>().containsAll(inputSubList))
        Assertions.assertTrue(subList.toObservableList<String, Collection<String>>().containsAll(inputSubList))
        Assertions.assertTrue(mutableSubList.toObservableList<String, Collection<String>>().containsAll(inputSubList))
        Assertions.assertTrue(inputSubSubList.toObservableList<String, Collection<String>>().containsAll(inputSubSubList))
        Assertions.assertTrue(subSubList.toObservableList<String, Collection<String>>().containsAll(inputSubSubList))
        Assertions.assertTrue(mutableSubSubList.toObservableList<String, Collection<String>>().containsAll(inputSubSubList))

        Assertions.assertTrue(inputSubList.toMutableObservableList().containsAll(inputSubList))
        Assertions.assertTrue(subList.toMutableObservableList().containsAll(inputSubList))
        Assertions.assertTrue(mutableSubList.toMutableObservableList().containsAll(inputSubList))
        Assertions.assertTrue(inputSubSubList.toMutableObservableList().containsAll(inputSubSubList))
        Assertions.assertTrue(subSubList.toMutableObservableList().containsAll(inputSubSubList))
        Assertions.assertTrue(mutableSubSubList.toMutableObservableList().containsAll(inputSubSubList))

        Assertions.assertTrue(inputSubList.toMutableObservableList<String, Collection<String>>().containsAll(inputSubList))
        Assertions.assertTrue(subList.toMutableObservableList<String, Collection<String>>().containsAll(inputSubList))
        Assertions.assertTrue(mutableSubList.toMutableObservableList<String, Collection<String>>().containsAll(inputSubList))
        Assertions.assertTrue(inputSubSubList.toMutableObservableList<String, Collection<String>>().containsAll(inputSubSubList))
        Assertions.assertTrue(subSubList.toMutableObservableList<String, Collection<String>>().containsAll(inputSubSubList))
        Assertions.assertTrue(mutableSubSubList.toMutableObservableList<String, Collection<String>>().containsAll(inputSubSubList))
    }

    @Test
    fun mapConstruction() {
        val testMaps = TestMaps()

        val inputMap = testMaps.inputMap
        val map = testMaps.map
        val mutableMap = testMaps.mutableMap
        val emptyMap = testMaps.emptyMap
        val emptyMutableMap = testMaps.emptyMutableMap
        val entries = inputMap.entries.toList()
        val pairs = entries.pairs().toList()

        Assertions.assertTrue(map.containsAll(inputMap))
        Assertions.assertTrue(mutableMap.containsAll(inputMap))
        Assertions.assertTrue(emptyMap.isEmpty())
        Assertions.assertTrue(emptyMutableMap.isEmpty())

        Assertions.assertTrue(ObservableMap(inputMap).containsAll(inputMap))
        Assertions.assertTrue(ObservableMap(map).containsAll(inputMap))
        Assertions.assertTrue(ObservableMap(mutableMap).containsAll(inputMap))
        Assertions.assertTrue(ObservableMap(emptyMap).isEmpty())
        Assertions.assertTrue(ObservableMap(emptyMutableMap).isEmpty())
        Assertions.assertTrue(ObservableMap<Int, String>(inputMap).containsAll(inputMap))
        Assertions.assertTrue(ObservableMap<Int, String>(map).containsAll(inputMap))
        Assertions.assertTrue(ObservableMap<Int, String>(mutableMap).containsAll(inputMap))
        Assertions.assertTrue(ObservableMap<Int, String>(emptyMap).isEmpty())
        Assertions.assertTrue(ObservableMap<Int, String>(emptyMutableMap).isEmpty())

        Assertions.assertTrue(MutableObservableMap(inputMap).containsAll(inputMap))
        Assertions.assertTrue(MutableObservableMap(map).containsAll(inputMap))
        Assertions.assertTrue(MutableObservableMap(mutableMap).containsAll(inputMap))
        Assertions.assertTrue(MutableObservableMap(emptyMap).isEmpty())
        Assertions.assertTrue(MutableObservableMap(emptyMutableMap).isEmpty())
        Assertions.assertTrue(MutableObservableMap<Int, String>(inputMap).containsAll(inputMap))
        Assertions.assertTrue(MutableObservableMap<Int, String>(map).containsAll(inputMap))
        Assertions.assertTrue(MutableObservableMap<Int, String>(mutableMap).containsAll(inputMap))
        Assertions.assertTrue(MutableObservableMap<Int, String>(emptyMap).isEmpty())
        Assertions.assertTrue(MutableObservableMap<Int, String>(emptyMutableMap).isEmpty())

        Assertions.assertTrue(inputMap.toObservableMap().containsAll(inputMap))
        Assertions.assertTrue(map.toObservableMap().containsAll(inputMap))
        Assertions.assertTrue(mutableMap.toObservableMap().containsAll(inputMap))
        Assertions.assertTrue(pairs.toObservableMap().containsAll(inputMap))
        Assertions.assertTrue(emptyMap.toObservableMap().isEmpty())
        Assertions.assertTrue(emptyMutableMap.toObservableMap().isEmpty())
        Assertions.assertTrue(inputMap.toObservableMap<Int, String>().containsAll(inputMap))
        Assertions.assertTrue(map.toObservableMap<Int, String>().containsAll(inputMap))
        Assertions.assertTrue(mutableMap.toObservableMap<Int, String>().containsAll(inputMap))
        Assertions.assertTrue(pairs.toObservableMap<Int, String>().containsAll(inputMap))
        Assertions.assertTrue(emptyMap.toObservableMap<Int, String>().isEmpty())
        Assertions.assertTrue(emptyMutableMap.toObservableMap<Int, String>().isEmpty())

        Assertions.assertTrue(inputMap.toMutableObservableMap().containsAll(inputMap))
        Assertions.assertTrue(map.toMutableObservableMap().containsAll(inputMap))
        Assertions.assertTrue(mutableMap.toMutableObservableMap().containsAll(inputMap))
        Assertions.assertTrue(pairs.toMutableObservableMap().containsAll(inputMap))
        Assertions.assertTrue(emptyMap.toMutableObservableMap().isEmpty())
        Assertions.assertTrue(emptyMutableMap.toMutableObservableMap().isEmpty())
        Assertions.assertTrue(inputMap.toMutableObservableMap<Int, String>().containsAll(inputMap))
        Assertions.assertTrue(map.toMutableObservableMap<Int, String>().containsAll(inputMap))
        Assertions.assertTrue(
            mutableMap.toMutableObservableMap<Int, String>().containsAll(inputMap)
        )
        Assertions.assertTrue(pairs.toMutableObservableMap<Int, String>().containsAll(inputMap))
        Assertions.assertTrue(emptyMap.toMutableObservableMap<Int, String>().isEmpty())
        Assertions.assertTrue(emptyMutableMap.toMutableObservableMap<Int, String>().isEmpty())

        Assertions.assertTrue(observableMapOf(inputMap).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf(map).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf(mutableMap).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf(pairs).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf(emptyMap).isEmpty())
        Assertions.assertTrue(observableMapOf(emptyMutableMap).isEmpty())
        Assertions.assertTrue(observableMapOf<Int, String>(inputMap).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf<Int, String>(map).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf<Int, String>(mutableMap).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf<Int, String>(pairs).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf<Int, String>(emptyMap).isEmpty())
        Assertions.assertTrue(observableMapOf<Int, String>(emptyMutableMap).isEmpty())

        Assertions.assertTrue(mutableObservableMapOf(inputMap).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf(map).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf(mutableMap).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf(pairs).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf(emptyMap).isEmpty())
        Assertions.assertTrue(mutableObservableMapOf(emptyMutableMap).isEmpty())
        Assertions.assertTrue(mutableObservableMapOf<Int, String>(inputMap).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf<Int, String>(map).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf<Int, String>(mutableMap).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf<Int, String>(pairs).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf<Int, String>(emptyMap).isEmpty())
        Assertions.assertTrue(mutableObservableMapOf<Int, String>(emptyMutableMap).isEmpty())

        Assertions.assertTrue(observableMapOf(entries[0], entries[1], entries[2], entries[3]).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf(pairs[0], pairs[1], pairs[2], pairs[3]).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf<Int, String>(entries[0], entries[1], entries[2], entries[3]).containsAll(inputMap))
        Assertions.assertTrue(observableMapOf<Int, String>(pairs[0], pairs[1], pairs[2], pairs[3]).containsAll(inputMap))

        Assertions.assertTrue(mutableObservableMapOf(entries[0], entries[1], entries[2], entries[3]).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf(pairs[0], pairs[1], pairs[2], pairs[3]).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf<Int, String>(entries[0], entries[1], entries[2], entries[3]).containsAll(inputMap))
        Assertions.assertTrue(mutableObservableMapOf<Int, String>(pairs[0], pairs[1], pairs[2], pairs[3]).containsAll(inputMap))
    }
}
