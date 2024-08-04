package com.sg8.collections.reactive
import com.sg8.collections.reactive.list.loadMutableObservableListOf
import com.sg8.collections.reactive.list.loadObservableListOf
import com.sg8.collections.reactive.list.saveToNbt
import com.sg8.collections.reactive.list.toMutableObservableList
import com.sg8.collections.reactive.list.toObservableList
import com.sg8.collections.reactive.map.loadMutableObservableMapOf
import com.sg8.collections.reactive.map.loadObservableMapOf
import com.sg8.collections.reactive.map.saveToNbt
import com.sg8.collections.reactive.set.loadMutableObservableSetOf
import com.sg8.collections.reactive.set.loadObservableSetOf
import com.sg8.collections.reactive.set.saveToNbt
import com.sg8.storage.DataKeys
import net.minecraft.nbt.CompoundTag
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.collections.Map.Entry

object SerializationTests {

    @Test
    fun setSerialization() {
        val testSets = TestSets()
        val inputSet = testSets.inputSet

        val saveElementHandler = { element: String ->
            CompoundTag().also { it.putString(DataKeys.ELEMENT, element) }
        }

        val setNbt = testSets.set.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(setNbt.getInt(DataKeys.SIZE) == testSets.set.size)

        val setFromNbt = setNbt.loadObservableSetOf({ it.getString(DataKeys.ELEMENT) })
        Assertions.assertTrue(setFromNbt.containsAll(inputSet))

        val mutableSetNbt = testSets.mutableSet.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(mutableSetNbt.getInt(DataKeys.SIZE) == testSets.mutableSet.size)

        val mutableSetFromNbt = mutableSetNbt.loadMutableObservableSetOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(mutableSetFromNbt.containsAll(inputSet))

        val emptySetNbt = testSets.emptySet.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(emptySetNbt.getInt(DataKeys.SIZE) == testSets.emptySet.size)

        val emptySetFromNbt = emptySetNbt.loadObservableSetOf({ it.getString(DataKeys.ELEMENT) })
        Assertions.assertTrue(emptySetFromNbt.isEmpty())

        val emptyMutableSetNbt = testSets.emptyMutableSet.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(emptyMutableSetNbt.getInt(DataKeys.SIZE) == testSets.emptyMutableSet.size)

        val emptyMutableSetFromNbt = emptyMutableSetNbt.loadMutableObservableSetOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(emptyMutableSetFromNbt.isEmpty())
    }

    @Test
    fun listSerialization() {
        val testLists = TestLists()
        val inputList = testLists.inputList

        val saveElementHandler = { element: String ->
            CompoundTag().also { it.putString(DataKeys.ELEMENT, element) }
        }

        val listNbt = testLists.list.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(listNbt.getInt(DataKeys.SIZE) == testLists.list.size)

        val listFromNbt = listNbt.loadObservableListOf({ it.getString(DataKeys.ELEMENT) })
        Assertions.assertTrue(listFromNbt.containsAll(inputList))

        val mutableListNbt = testLists.mutableList.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(mutableListNbt.getInt(DataKeys.SIZE) == testLists.mutableList.size)

        val mutableListFromNbt = mutableListNbt.loadMutableObservableListOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(mutableListFromNbt.containsAll(inputList))

        val emptyListNbt = testLists.emptyList.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(emptyListNbt.getInt(DataKeys.SIZE) == testLists.emptyList.size)

        val emptyListFromNbt = emptyListNbt.loadObservableListOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(emptyListFromNbt.isEmpty())

        val emptyMutableListNbt = testLists.emptyMutableList.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(
            emptyMutableListNbt.getInt(DataKeys.SIZE) == testLists.emptyMutableList.size
        )

        val emptyMutableListFromNbt = emptyMutableListNbt.loadMutableObservableListOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(emptyMutableListFromNbt.isEmpty())
    }

    @Test
    fun subListSerialization() {
        val testLists = TestLists()
        val inputSubList = testLists.inputList.subList(1, 3)
        val subList = testLists.list.subList(1, 3)
        val mutableSubList = testLists.mutableList.subList(1, 3)

        val saveElementHandler = { element: String ->
            CompoundTag().also { it.putString(DataKeys.ELEMENT, element) }
        }

        val subListNbt = subList.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(subListNbt.getInt(DataKeys.SIZE) == subList.size)

        val subListFromNbt = subListNbt.loadObservableListOf({ it.getString(DataKeys.ELEMENT) })
        Assertions.assertTrue(subListFromNbt.containsAll(inputSubList))

        val mutableSubListNbt = mutableSubList.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(mutableSubListNbt.getInt(DataKeys.SIZE) == mutableSubList.size)

        val mutableSubListFromNbt = mutableSubListNbt.loadMutableObservableListOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(mutableSubListFromNbt.containsAll(inputSubList))
    }

    @Test
    fun mapSerialization() {
        val testMaps = TestMaps()
        val inputMap = testMaps.inputMap

        val saveEntryHandler = { (key, value): Entry<Int, String> ->
            val entryNbt = CompoundTag()
            entryNbt.putInt(DataKeys.KEY, key)
            entryNbt.putString(DataKeys.VALUE, value)
            entryNbt
        }

        val mapNbt = testMaps.map.saveToNbt { saveEntryHandler(it) }
        Assertions.assertTrue(mapNbt.getInt(DataKeys.SIZE) == testMaps.map.size)

        val mapFromNbt = mapNbt.loadObservableMapOf(
            { it.getInt(DataKeys.KEY) to it.getString(DataKeys.VALUE) }
        )
        Assertions.assertTrue(mapFromNbt.containsAll(inputMap))

        val mutableMapNbt = testMaps.mutableMap.saveToNbt { saveEntryHandler(it) }
        Assertions.assertTrue(mutableMapNbt.getInt(DataKeys.SIZE) == testMaps.mutableMap.size)

        val mutableMapFromNbt = mutableMapNbt.loadMutableObservableMapOf(
            { it.getInt(DataKeys.KEY)to it.getString(DataKeys.VALUE) }
        )
        Assertions.assertTrue(mutableMapFromNbt.containsAll(inputMap))

        val emptyMapNbt = testMaps.emptyMap.saveToNbt { saveEntryHandler(it) }
        Assertions.assertTrue(emptyMapNbt.getInt(DataKeys.SIZE) == testMaps.emptyMap.size)

        val emptyMapFromNbt = emptyMapNbt.loadObservableMapOf(
            { it.getInt(DataKeys.KEY) to it.getString(DataKeys.VALUE) }
        )
        Assertions.assertTrue(emptyMapFromNbt.isEmpty())

        val emptyMutableMapNbt = testMaps.emptyMutableMap.saveToNbt { saveEntryHandler(it) }
        Assertions.assertTrue(emptyMutableMapNbt.getInt(DataKeys.SIZE) == testMaps.emptyMutableMap.size)

        val emptyMutableMapFromNbt = emptyMutableMapNbt.loadMutableObservableMapOf(
            { it.getInt(DataKeys.KEY)to it.getString(DataKeys.VALUE) }
        )
        Assertions.assertTrue(emptyMutableMapFromNbt.isEmpty())
    }
}
