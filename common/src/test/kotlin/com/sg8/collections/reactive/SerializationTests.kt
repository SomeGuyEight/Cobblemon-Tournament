package com.sg8.collections.reactive
import com.sg8.collections.reactive.list.loadMutableObservableListOf
import com.sg8.collections.reactive.list.loadObservableListOf
import com.sg8.collections.reactive.list.saveToNbt
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
        val sets = TestSets.default()

        val saveElementHandler = { element: String ->
            CompoundTag().also { it.putString(DataKeys.ELEMENT, element) }
        }

        val setNbt = sets.observable.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(setNbt.getInt(DataKeys.SIZE) == sets.observable.size)

        val setFromNbt = setNbt.loadObservableSetOf({ it.getString(DataKeys.ELEMENT) })
        Assertions.assertTrue(setFromNbt.containsAll(sets.input))

        val mutableSetNbt = sets.mutableObservable.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(mutableSetNbt.getInt(DataKeys.SIZE) == sets.mutableObservable.size)

        val mutableSetFromNbt = mutableSetNbt.loadMutableObservableSetOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(mutableSetFromNbt.containsAll(sets.input))

        val emptySetNbt = sets.emptyObservable.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(emptySetNbt.getInt(DataKeys.SIZE) == sets.emptyObservable.size)

        val emptySetFromNbt = emptySetNbt.loadObservableSetOf({ it.getString(DataKeys.ELEMENT) })
        Assertions.assertTrue(emptySetFromNbt.isEmpty())

        val emptyMutableSetNbt = sets.emptyMutableObservable.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(emptyMutableSetNbt.getInt(DataKeys.SIZE) == sets.emptyMutableObservable.size)

        val emptyMutableSetFromNbt = emptyMutableSetNbt.loadMutableObservableSetOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(emptyMutableSetFromNbt.isEmpty())
    }

    @Test
    fun listSerialization() {
        val lists = TestLists.default()

        val saveElementHandler = { element: String ->
            CompoundTag().also { it.putString(DataKeys.ELEMENT, element) }
        }

        val listNbt = lists.observable.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(listNbt.getInt(DataKeys.SIZE) == lists.observable.size)

        val listFromNbt = listNbt.loadObservableListOf({ it.getString(DataKeys.ELEMENT) })
        Assertions.assertTrue(listFromNbt.containsAll(lists.input))

        val mutableListNbt = lists.mutableObservable.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(mutableListNbt.getInt(DataKeys.SIZE) == lists.mutableObservable.size)

        val mutableListFromNbt = mutableListNbt.loadMutableObservableListOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(mutableListFromNbt.containsAll(lists.input))

        val emptyListNbt = lists.emptyObservable.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(emptyListNbt.getInt(DataKeys.SIZE) == lists.emptyObservable.size)

        val emptyListFromNbt = emptyListNbt.loadObservableListOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(emptyListFromNbt.isEmpty())

        val emptyMutableListNbt = lists.emptyMutableObservable.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(
            emptyMutableListNbt.getInt(DataKeys.SIZE) == lists.emptyMutableObservable.size
        )

        val emptyMutableListFromNbt = emptyMutableListNbt.loadMutableObservableListOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(emptyMutableListFromNbt.isEmpty())
    }

    @Test
    fun subListSerialization() {
        val lists = TestSubLists.default()

        val saveElementHandler = { element: String ->
            CompoundTag().also { it.putString(DataKeys.ELEMENT, element) }
        }

        val subListNbt = lists.observableSub.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(subListNbt.getInt(DataKeys.SIZE) == lists.observableSub.size)

        val subListFromNbt = subListNbt.loadObservableListOf({ it.getString(DataKeys.ELEMENT) })
        Assertions.assertTrue(subListFromNbt.containsAll(lists.observableSub))

        val mutableSubListNbt = lists.mutableObservableSub.saveToNbt { saveElementHandler(it) }
        Assertions.assertTrue(mutableSubListNbt.getInt(DataKeys.SIZE) == lists.mutableObservableSub.size)

        val mutableSubListFromNbt = mutableSubListNbt.loadMutableObservableListOf(
            { it.getString(DataKeys.ELEMENT) }
        )
        Assertions.assertTrue(mutableSubListFromNbt.containsAll(lists.mutableObservableSub))
    }

    @Test
    fun mapSerialization() {
        val maps = TestMaps.default()

        val saveEntryHandler = { (key, value): Entry<Int, String> ->
            val entryNbt = CompoundTag()
            entryNbt.putInt(DataKeys.KEY, key)
            entryNbt.putString(DataKeys.VALUE, value)
            entryNbt
        }

        val mapNbt = maps.observable.saveToNbt { saveEntryHandler(it) }
        Assertions.assertTrue(mapNbt.getInt(DataKeys.SIZE) == maps.observable.size)

        val mapFromNbt = mapNbt.loadObservableMapOf(
            { it.getInt(DataKeys.KEY) to it.getString(DataKeys.VALUE) }
        )
        Assertions.assertTrue(mapFromNbt.containsAll(maps.input))

        val mutableMapNbt = maps.mutableObservable.saveToNbt { saveEntryHandler(it) }
        Assertions.assertTrue(mutableMapNbt.getInt(DataKeys.SIZE) == maps.mutableObservable.size)

        val mutableMapFromNbt = mutableMapNbt.loadMutableObservableMapOf(
            { it.getInt(DataKeys.KEY)to it.getString(DataKeys.VALUE) }
        )
        Assertions.assertTrue(mutableMapFromNbt.containsAll(maps.input))

        val emptyMapNbt = maps.emptyObservable.saveToNbt { saveEntryHandler(it) }
        Assertions.assertTrue(emptyMapNbt.getInt(DataKeys.SIZE) == maps.emptyObservable.size)

        val emptyMapFromNbt = emptyMapNbt.loadObservableMapOf(
            { it.getInt(DataKeys.KEY) to it.getString(DataKeys.VALUE) }
        )
        Assertions.assertTrue(emptyMapFromNbt.isEmpty())

        val emptyMutableMapNbt = maps.emptyMutableObservable.saveToNbt { saveEntryHandler(it) }
        Assertions.assertTrue(emptyMutableMapNbt.getInt(DataKeys.SIZE) == maps.emptyMutableObservable.size)

        val emptyMutableMapFromNbt = emptyMutableMapNbt.loadMutableObservableMapOf(
            { it.getInt(DataKeys.KEY)to it.getString(DataKeys.VALUE) }
        )
        Assertions.assertTrue(emptyMutableMapFromNbt.isEmpty())
    }
}
