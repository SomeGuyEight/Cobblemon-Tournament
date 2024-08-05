package com.sg8.collections.reactive

import com.cobblemon.mod.common.api.Priority
import com.sg8.collections.reactive.collection.MutableObservableCollection
import com.sg8.collections.reactive.list.MutableObservableList
import com.sg8.collections.reactive.map.mutableObservableMapOf
import com.sg8.collections.reactive.set.MutableObservableSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


object EmittingTests {

    @Test
    fun setAsCollectionEmitChange() = collectionEmitChange(MutableObservableSet())

    @Test
    fun listAsCollectionEmitChange() = collectionEmitChange(MutableObservableList())

    private fun collectionEmitChange(elements: MutableObservableCollection<String, *>) {
        var anyChangeOne = 0
        var additionOne = 0
        var removalOne = 0

        var anyChangeTwo = 0
        var additionTwo = 0
        var removalTwo = 0

        var expectedAnyChangeOne = 0
        var expectedAdditionOne = 0
        var expectedRemovalOne = 0

        var expectedAnyChangeTwo = 0
        var expectedAdditionTwo = 0
        var expectedRemovalTwo = 0

        val confirmExpected = {
            Assertions.assertTrue(anyChangeOne == expectedAnyChangeOne)
            Assertions.assertTrue(additionOne == expectedAdditionOne)
            Assertions.assertTrue(removalOne == expectedRemovalOne)
            Assertions.assertTrue(anyChangeTwo == expectedAnyChangeTwo)
            Assertions.assertTrue(additionTwo == expectedAdditionTwo)
            Assertions.assertTrue(removalTwo == expectedRemovalTwo)
        }

        val getNewSubOne = {
            elements.subscribe(
                anyChangeHandler = { anyChangeOne++ },
                additionHandler = { additionOne++ },
                removalHandler = { removalOne++ },
            )
        }

        val getNewSubTwo = {
            elements.subscribe(
                anyChangeHandler = { anyChangeTwo++ },
                additionHandler = { additionTwo++ },
                removalHandler = { removalTwo++ },
            )
        }

        val subscriptionOne = getNewSubOne()
        val subscriptionTwo = getNewSubTwo()

        var subbedAnyChangeOne = true
        var subbedAdditionOne = true
        var subbedRemovalOne = true

        var subbedAnyChangeTwo = true
        var subbedAdditionTwo = true
        var subbedRemovalTwo = true

        val incrementExpectedAddition = { value: Int ->
            if (subbedAnyChangeOne) expectedAnyChangeOne += value
            if (subbedAdditionOne) expectedAdditionOne += value
            if (subbedAnyChangeTwo) expectedAnyChangeTwo += value
            if (subbedAdditionTwo) expectedAdditionTwo += value
        }
        val incrementExpectedRemoval = { value: Int ->
            if (subbedAnyChangeOne) expectedAnyChangeOne += value
            if (subbedRemovalOne) expectedRemovalOne += value
            if (subbedAnyChangeTwo) expectedAnyChangeTwo += value
            if (subbedRemovalTwo) expectedRemovalTwo += value
        }

        var currentCount = 0
        val next = { "${currentCount++}" }
        val bulkInput = listOf(next(), next(), next(), next())

        val addNew = { iterations: Int ->
            for (i in 0 until iterations) {
                if (elements.add(next())) incrementExpectedAddition(1)
            }
            confirmExpected()
        }

        val addDuplicates = { iterations: Int ->
            elements.firstOrNull() ?. let{ dupe ->
                for (i in 0 until iterations) {
                    if (elements.add(dupe)) incrementExpectedAddition(1)
                }
            }
            confirmExpected()
        }

        val addNewIfTrue = { iterations: Int ->
            for (i in 0 until iterations) {
                if (elements.addIf(next()) { true }) incrementExpectedAddition(1)
            }
            confirmExpected()
        }

        val addNewIfFalse = { iterations: Int ->
            for (i in 0 until iterations) {
                if (elements.addIf(next()) { false }) incrementExpectedAddition(1)
            }
            confirmExpected()
        }

        val addDupeIfTrue = { iterations: Int ->
            elements.firstOrNull() ?. let{ dupe ->
                for (i in 0 until iterations) {
                    if (elements.addIf(dupe) { true }) incrementExpectedAddition(1)
                }
            }
            confirmExpected()
        }

        val addDupeIfFalse = { iterations: Int ->
            elements.firstOrNull() ?. let{ dupe ->
                for (i in 0 until iterations) {
                    if (elements.addIf(dupe) { false }) incrementExpectedAddition(1)
                }
            }
            confirmExpected()
        }

        val remove = { iterations: Int ->
            for (i in 0 until iterations) {
                elements.firstOrNull()?.let { if (elements.remove(it)) incrementExpectedRemoval(1) }
            }
            confirmExpected()
        }

        val removeNotContained = { iterations: Int ->
            for (i in 0 until iterations) {
                if (elements.remove("I am not here...")) incrementExpectedRemoval(1)
            }
            confirmExpected()
        }

        val removeDupeIfTrue = { iterations: Int ->
            elements.firstOrNull() ?. let{ dupe ->
                for (i in 0 until iterations) {
                    val size = elements.size
                    elements.removeIf { it == dupe }
                    val change = size - elements.size
                    if (change > 0) incrementExpectedRemoval(change)
                }
            }
            confirmExpected()
        }

        val removeDupeIfFalse = { iterations: Int ->
            elements.firstOrNull() ?. let{ dupe ->
                for (i in 0 until iterations) {
                    if (elements.removeIf { false }) incrementExpectedRemoval(1)
                }
            }
            confirmExpected()
        }

        val addAll = { iterations: Int ->
            for (i in 0 until iterations) {
                val size = elements.size
                if (elements.addAll(bulkInput)) incrementExpectedAddition(elements.size - size)
            }
            confirmExpected()
        }

        val removeAll = { iterations: Int ->
            for (i in 0 until iterations) {
                val size = elements.size
                if (elements.removeAll(bulkInput)) incrementExpectedRemoval(size - elements.size)
            }
            confirmExpected()
        }

        val retainAll = { iterations: Int ->
            for (i in 0 until iterations) {
                val size = elements.size
                if (elements.retainAll(bulkInput)) incrementExpectedRemoval(size - elements.size)
            }
            confirmExpected()
        }

        val clear = { iterations: Int ->
            for (i in 0 until iterations) {
                val size = elements.size
                elements.clear()
                val change = size - elements.size
                if (change > 0) incrementExpectedRemoval(change)
            }
            confirmExpected()
        }

        val runAll = { iterations: Int ->
            addNew(iterations)
            addDuplicates(iterations)
            addNewIfTrue(iterations)
            addNewIfFalse(iterations)
            addDupeIfTrue(iterations)
            addDupeIfFalse(iterations)
            remove(iterations)
            removeNotContained(iterations)
            removeDupeIfTrue(iterations)
            removeDupeIfFalse(iterations)
            addAll(iterations)
            removeAll(iterations)
            retainAll(iterations)
            clear(iterations)
        }

        runAll(8)

        subscriptionOne.unsubscribe()
        subbedAnyChangeOne = false
        subbedAdditionOne = false
        subbedRemovalOne = false
        runAll(8)

        subscriptionTwo.anyChange.unsubscribe()
        subbedAnyChangeTwo = false
        runAll(8)

        subscriptionTwo.addition?.unsubscribe()
        subscriptionTwo.removal?.unsubscribe()
        subbedAdditionTwo = false
        subbedRemovalTwo = false
        runAll(8)

        val newSubOne = getNewSubOne()
        newSubOne.addition?.unsubscribe()
        newSubOne.removal?.unsubscribe()
        subbedAnyChangeOne = true
        runAll(8)

        val newSubTwo = getNewSubTwo()
        newSubTwo.addition?.unsubscribe()
        newSubTwo.removal?.unsubscribe()
        subbedAnyChangeTwo = true
        runAll(8)
    }

    @Test
    fun listAndSubListEmitChange() {
        var anyChangeList = 0
        var additionList = 0
        var removalList = 0
        var setList = 0
        var swapList = 0
        var anyChangeSubList = 0
        var additionSubList = 0
        var removalSubList = 0
        var setSubList = 0
        var swapSubList = 0

        var expectedAnyChangeList = 0
        var expectedAdditionList = 0
        var expectedRemovalList = 0
        var expectedSetList = 0
        var expectedSwapList = 0
        var expectedAnyChangeSubList = 0
        var expectedAdditionSubList = 0
        var expectedRemovalSubList = 0
        var expectedSetSubList = 0
        var expectedSwapSubList = 0

        val input = listOf("zero", "one", "two", "three")
        val list = MutableObservableList(input)
        val subList = list.subList(1, 3)

        val listSub = list.subscribe(
            priority = Priority.NORMAL,
            anyChangeHandler = { anyChangeList++ },
            additionHandler = { additionList++ },
            removalHandler = { removalList++ },
            setHandler = { setList++ },
            swapHandler = { swapList++ },
        )

        val subListSub = subList.subscribe(
            priority = Priority.NORMAL,
            anyChangeHandler = { anyChangeSubList++ },
            additionHandler = { additionSubList++ },
            removalHandler = { removalSubList++ },
            setHandler = { setSubList++ },
            swapHandler = { swapSubList++ },
        )

        val confirmExpected = {
            Assertions.assertTrue(anyChangeList == expectedAnyChangeList)
            Assertions.assertTrue(additionList == expectedAdditionList)
            Assertions.assertTrue(removalList == expectedRemovalList)
            Assertions.assertTrue(setList == expectedSetList)
            Assertions.assertTrue(swapList == expectedSwapList)
            Assertions.assertTrue(anyChangeSubList == expectedAnyChangeSubList)
            Assertions.assertTrue(additionSubList == expectedAdditionSubList)
            Assertions.assertTrue(removalSubList == expectedRemovalSubList)
            Assertions.assertTrue(setSubList == expectedSetSubList)
            Assertions.assertTrue(swapSubList == expectedSwapSubList)
        }

        val confirmSynced: () -> Unit = {
            for (i in 1 until 3) {
                Assertions.assertTrue(subList[i - 1] == list[i])
            }
        }

        var currentCount = 0
        val next = { "${currentCount++}" }

        val addNewEnd = { iterations: Int ->
            for (i in 0 until iterations) {
                list.add(next())
                expectedAnyChangeList += 1
                expectedAdditionList += 1
            }
            confirmExpected()
            confirmSynced()
        }

        addNewEnd(2)

        run {
            list.addIf("six") { false }
            confirmExpected()
            confirmSynced()
        }

        run {
            list.addIf("seven") { true }
            expectedAnyChangeList += 1
            expectedAdditionList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.addAtIf(0,"eight") { false }
            confirmExpected()
            confirmSynced()
        }

        run {
            list.addAtIf(4,"nine") { true }
            expectedAnyChangeList += 1
            expectedAdditionList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.remove(list[4])
            list.remove(list[4])
            expectedAnyChangeList += 2
            expectedRemovalList += 2
            confirmExpected()
            confirmSynced()
        }

        run {
            list.removeAt(4)
            expectedAnyChangeList += 1
            expectedRemovalList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.removeAtIf(0) { false }
            confirmExpected()
            confirmSynced()
        }

        run {
            list.removeAtIf(4) { true }
            expectedAnyChangeList += 1
            expectedRemovalList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.add("ten")
            list[4] = "eleven"
            // 2 b/c each set emits 2 anyChange for elements & 1 set for the pair
            expectedAnyChangeList += 3
            expectedAdditionList += 1
            expectedSetList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.setIf(0, "twelve") { false }
            confirmExpected()
            confirmSynced()
        }

        run {
            list.setIf(4, "thirteen") { true }
            // increments anyChange by 2 b/c of 2 elements per set
            expectedAnyChangeList += 2
            expectedSetList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.add("fourteen")
            list.swap(4, 5)
            // increments anyChange by 2 b/c of 2 elements per swap
            expectedAnyChangeList += 3
            expectedAdditionList += 1
            expectedSwapList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.swapIf(4, 5) { false }
            confirmExpected()
            confirmSynced()
        }

        run {
            list.swapIf(4, 5) { true }
            // increments anyChange by 2 b/c of 2 elements per swap
            expectedAnyChangeList += 2
            expectedSwapList += 1
            confirmExpected()
            confirmSynced()
        }

        // test mutations in the sublist window
        run {
            list.add(1, "fifteen")
            expectedAnyChangeList += 1
            expectedAdditionList += 1
            // removal for the elements pushed out of the sublist window
            expectedAnyChangeSubList += 2
            expectedAdditionSubList += 1
            expectedRemovalSubList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.removeAt(1)
            expectedAnyChangeList += 1
            expectedRemovalList += 1
            // addition for the elements dropping into the sublist window
            expectedAnyChangeSubList += 2
            expectedAdditionSubList += 1
            expectedRemovalSubList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list[1] = "sixteen"
            expectedAnyChangeList += 2
            expectedSetList += 1
            expectedAnyChangeSubList += 2
            expectedSetSubList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.swap(1, 2)
            expectedAnyChangeList += 2
            expectedSwapList += 1
            expectedAnyChangeSubList += 2
            expectedSwapSubList += 1
            confirmExpected()
            confirmSynced()
        }

        // test swap with one index out & one index in the window
        run {
            list.swap(0, 1)
            expectedAnyChangeList += 2
            expectedSwapList += 1
            expectedAnyChangeSubList += 2
            expectedAdditionSubList += 1
            expectedRemovalSubList += 1
            confirmExpected()
            confirmSynced()
        }

        // test mutations beneath the window
        run {
            list.add(0, "seventeen")
            expectedAnyChangeList += 1
            expectedAdditionList += 1
            // removal for the elements pushed out of the sublist window
            expectedAnyChangeSubList += 2
            expectedAdditionSubList += 1
            expectedRemovalSubList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.removeAt(0)
            expectedAnyChangeList += 1
            expectedRemovalList += 1
            // removal for the elements pushed out of the sublist window
            expectedAnyChangeSubList += 2
            expectedAdditionSubList += 1
            expectedRemovalSubList += 1
            confirmExpected()
            confirmSynced()
        }
    }

    @Test
    fun mapEmitChange() {
        val mutableMap = mutableObservableMapOf(TestMaps.defaultMap())
        var anyChangeOne = 0
        var additionOne = 0
        var removalOne = 0

        var anyChangeTwo = 0
        var additionTwo = 0
        var removalTwo = 0

        var expectedAnyChangeOne = 0
        var expectedAdditionOne = 0
        var expectedRemovalOne = 0

        var expectedAnyChangeTwo = 0
        var expectedAdditionTwo = 0
        var expectedRemovalTwo = 0

        val confirmExpected = {
            Assertions.assertTrue(anyChangeOne == expectedAnyChangeOne)
            Assertions.assertTrue(additionOne == expectedAdditionOne)
            Assertions.assertTrue(removalOne == expectedRemovalOne)
            Assertions.assertTrue(anyChangeTwo == expectedAnyChangeTwo)
            Assertions.assertTrue(additionTwo == expectedAdditionTwo)
            Assertions.assertTrue(removalTwo == expectedRemovalTwo)
        }

        val getNewSubOne = {
            mutableMap.subscribe(
                anyChangeHandler = { anyChangeOne++ },
                additionHandler = { additionOne++ },
                removalHandler = { removalOne++ },
            )
        }

        val getNewSubTwo = {
            mutableMap.subscribe(
                anyChangeHandler = { anyChangeTwo++ },
                additionHandler = { additionTwo++ },
                removalHandler = { removalTwo++ },
            )
        }

        val subscriptionOne = getNewSubOne()
        val subscriptionTwo = getNewSubTwo()

        var subbedAnyChangeOne = true
        var subbedAdditionOne = true
        var subbedRemovalOne = true

        var subbedAnyChangeTwo = true
        var subbedAdditionTwo = true
        var subbedRemovalTwo = true

        val incrementExpectedAddition = { value: Int ->
            if (subbedAnyChangeOne) expectedAnyChangeOne += value
            if (subbedAdditionOne) expectedAdditionOne += value
            if (subbedAnyChangeTwo) expectedAnyChangeTwo += value
            if (subbedAdditionTwo) expectedAdditionTwo += value
        }
        val incrementExpectedRemoval = { value: Int ->
            if (subbedAnyChangeOne) expectedAnyChangeOne += value
            if (subbedRemovalOne) expectedRemovalOne += value
            if (subbedAnyChangeTwo) expectedAnyChangeTwo += value
            if (subbedRemovalTwo) expectedRemovalTwo += value
        }

        var currentCount = 0
        val next = { currentCount to "${currentCount++}" }
        val bulkInput = mapOf(next(), next(), next(), next())

        val putNew = { iterations: Int ->
            for (i in 0 until iterations) {
                if (mutableMap.put(next()) != null) {
                    incrementExpectedRemoval(1)
                }
                incrementExpectedAddition(1)
            }
            confirmExpected()
        }

        val putCurrentKeys = { iterations: Int ->
            mutableMap.firstKeyOrNull()?. let { dupe ->
                for (i in 0 until iterations) {
                    if (mutableMap.put(dupe, next().second) != null) {
                        incrementExpectedRemoval(1)
                    }
                    incrementExpectedAddition(1)
                }
            }
            confirmExpected()
        }

        val removeNew = { iterations: Int ->
            for (i in 0 until iterations) {
                if (mutableMap.remove(next().first) != null) {
                    incrementExpectedRemoval(1)
                }
            }
            confirmExpected()
        }

        val removeCurrent = { iterations: Int ->
            for (i in 0 until iterations) {
                if (mutableMap.remove(next().first) != null) {
                    incrementExpectedRemoval(1)
                }
            }
            confirmExpected()
        }

        val putAll = { iterations: Int ->
            for (i in 0 until iterations) {
                var replacedCount = 0
                bulkInput.forEach { if (mutableMap.keys.contains(it.key)) replacedCount++ }
                mutableMap.putAll(bulkInput)
                incrementExpectedAddition(bulkInput.size)
                incrementExpectedRemoval(replacedCount)
            }
            confirmExpected()
        }

        val clear = { iterations: Int ->
            for (i in 0 until iterations) {
                val size = mutableMap.size
                mutableMap.clear()
                val change = size - mutableMap.size
                if (change > 0) incrementExpectedRemoval(change)
            }
            confirmExpected()
        }

        val runAll = { iterations: Int ->
            putNew(iterations)
            putCurrentKeys(iterations)
            removeNew(iterations)
            removeCurrent(iterations)
            putAll(iterations)
            clear(iterations)
        }

        runAll(8)

        subscriptionOne.unsubscribe()
        subbedAnyChangeOne = false
        subbedAdditionOne = false
        subbedRemovalOne = false
        runAll(8)

        subscriptionTwo.anyChange.unsubscribe()
        subbedAnyChangeTwo = false
        runAll(8)

        subscriptionTwo.addition?.unsubscribe()
        subscriptionTwo.removal?.unsubscribe()
        subbedAdditionTwo = false
        subbedRemovalTwo = false
        runAll(8)

        val newSubOne = getNewSubOne()
        newSubOne.addition?.unsubscribe()
        newSubOne.removal?.unsubscribe()
        subbedAnyChangeOne = true
        runAll(8)

        val newSubTwo = getNewSubTwo()
        newSubTwo.addition?.unsubscribe()
        newSubTwo.removal?.unsubscribe()
        subbedAnyChangeTwo = true
        runAll(8)
    }

}
