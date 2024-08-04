package com.sg8.collections.reactive

import com.cobblemon.mod.common.api.Priority
import com.sg8.collections.reactive.collection.MutableObservableCollection
import com.sg8.collections.reactive.list.MutableObservableList
import com.sg8.collections.reactive.map.toMutableObservableMap
import com.sg8.collections.reactive.set.MutableObservableSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


object EmittingTests {

    @Test
    fun setAsCollectionEmitChange() = collectionEmitChange(MutableObservableSet())

    @Test
    fun listAsCollectionEmitChange() = collectionEmitChange(MutableObservableList())

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

        run {
            list.add("four")
            expectedAnyChangeList += 1
            expectedAdditionList += 1
            confirmExpected()
            confirmSynced()
        }

        run {
            list.add(4,"five")
            expectedAnyChangeList += 1
            expectedAdditionList += 1
            confirmExpected()
            confirmSynced()
        }

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
            list.remove("four")
            list.remove("five")
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
        val input = mutableMapOf<Int, String>()
        input[0] = "zero"
        input[1] = "one"
        input[2] = "two"
        input[3] = "three"
        val mutableMap = input.toMutableObservableMap()
    }

    private fun collectionEmitChange(elements: MutableObservableCollection<String, *>) {
        val input = mutableSetOf("zero", "one", "two", "three")

        var anyChangeOne = 0
        var additionOne = 0
        var removalOne = 0
        val setSubscriptionOne = elements.subscribe(
            Priority.NORMAL,
            { anyChangeOne++ },
            { additionOne++ },
            { removalOne++ },
        )
        var anyChangeTwo = 0
        var additionTwo = 0
        var removalTwo = 0
        val setSubscriptionTwo = elements.subscribe(
            Priority.NORMAL,
            { anyChangeTwo++ },
            { additionTwo++ },
            { removalTwo++ },
        )

        elements.add("zero")
        elements.add("one")
        elements.add("two")
        elements.add("three")
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 4)
            Assertions.assertTrue(additionOne == 4)
            Assertions.assertTrue(removalOne == 0)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 4)
            Assertions.assertTrue(additionOne == 4)
            Assertions.assertTrue(removalOne == 0)
        }

        elements.add("zero")
        elements.add("one")
        elements.add("two")
        elements.add("three")
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 4)
            Assertions.assertTrue(additionOne == 4)
            Assertions.assertTrue(removalOne == 0)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 8)
            Assertions.assertTrue(additionOne == 8)
            Assertions.assertTrue(removalOne == 0)
        }

        elements.remove("zero")
        elements.remove("one")
        elements.remove("two")
        elements.remove("three")
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 8)
            Assertions.assertTrue(additionOne == 4)
            Assertions.assertTrue(removalOne == 4)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 12)
            Assertions.assertTrue(additionOne == 8)
            Assertions.assertTrue(removalOne == 4)
        }

        elements.remove("zero")
        elements.remove("one")
        elements.remove("two")
        elements.remove("three")
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 8)
            Assertions.assertTrue(additionOne == 4)
            Assertions.assertTrue(removalOne == 4)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 16)
            Assertions.assertTrue(additionOne == 8)
            Assertions.assertTrue(removalOne == 8)
        }

        elements.addIf("zero") { !it.contains("zero") }
        elements.addIf("one") { !it.contains("one") }
        elements.addIf("two") { !it.contains("two") }
        elements.addIf("three") { !it.contains("three") }
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 12)
            Assertions.assertTrue(additionOne == 8)
            Assertions.assertTrue(removalOne == 4)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 20)
            Assertions.assertTrue(additionOne == 12)
            Assertions.assertTrue(removalOne == 8)
        }

        elements.addIf("zero") { !it.contains("zero") }
        elements.addIf("one") { !it.contains("one") }
        elements.addIf("two") { !it.contains("two") }
        elements.addIf("three") { !it.contains("three") }
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 12)
            Assertions.assertTrue(additionOne == 8)
            Assertions.assertTrue(removalOne == 4)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 20)
            Assertions.assertTrue(additionOne == 12)
            Assertions.assertTrue(removalOne == 8)
        }

        elements.removeIf { it == "zero" }
        elements.removeIf { it == "one" }
        elements.removeIf { it == "two" }
        elements.removeIf { it == "three" }
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 16)
            Assertions.assertTrue(additionOne == 8)
            Assertions.assertTrue(removalOne == 8)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 24)
            Assertions.assertTrue(additionOne == 12)
            Assertions.assertTrue(removalOne == 12)
        }

        elements.removeIf { it == "zero" }
        elements.removeIf { it == "one" }
        elements.removeIf { it == "two" }
        elements.removeIf { it == "three" }
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 16)
            Assertions.assertTrue(additionOne == 8)
            Assertions.assertTrue(removalOne == 8)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 24)
            Assertions.assertTrue(additionOne == 12)
            Assertions.assertTrue(removalOne == 12)
        }

        elements.addAll(input)
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 20)
            Assertions.assertTrue(additionOne == 12)
            Assertions.assertTrue(removalOne == 8)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 28)
            Assertions.assertTrue(additionOne == 16)
            Assertions.assertTrue(removalOne == 12)
        }

        elements.addAll(input)
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 20)
            Assertions.assertTrue(additionOne == 12)
            Assertions.assertTrue(removalOne == 8)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 32)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 12)
        }

        elements.removeAll(input)
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 24)
            Assertions.assertTrue(additionOne == 12)
            Assertions.assertTrue(removalOne == 12)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 40)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 20)
        }

        elements.removeAll(input)
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 24)
            Assertions.assertTrue(additionOne == 12)
            Assertions.assertTrue(removalOne == 12)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 40)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 20)
        }

        elements.retainAll(input)
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 24)
            Assertions.assertTrue(additionOne == 12)
            Assertions.assertTrue(removalOne == 12)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 40)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 20)
        }

        elements.addAll(input)
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 28)
            Assertions.assertTrue(additionOne == 16)
            Assertions.assertTrue(removalOne == 12)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 44)
            Assertions.assertTrue(additionOne == 24)
            Assertions.assertTrue(removalOne == 20)
        }

        elements.addAll(input)
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 28)
            Assertions.assertTrue(additionOne == 16)
            Assertions.assertTrue(removalOne == 12)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 48)
            Assertions.assertTrue(additionOne == 28)
            Assertions.assertTrue(removalOne == 20)
        }

        elements.retainAll(input)
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 28)
            Assertions.assertTrue(additionOne == 16)
            Assertions.assertTrue(removalOne == 12)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 48)
            Assertions.assertTrue(additionOne == 28)
            Assertions.assertTrue(removalOne == 20)
        }

        elements.retainAll(setOf())
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 32)
            Assertions.assertTrue(additionOne == 16)
            Assertions.assertTrue(removalOne == 16)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 56)
            Assertions.assertTrue(additionOne == 28)
            Assertions.assertTrue(removalOne == 28)
        }

        val copy = elements.mutableCopy()
        copy.clear()
        copy.addAll(input)
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 32)
            Assertions.assertTrue(additionOne == 16)
            Assertions.assertTrue(removalOne == 16)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 56)
            Assertions.assertTrue(additionOne == 28)
            Assertions.assertTrue(removalOne == 28)
        }

        elements.addAll(input)
        elements.clear()
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 40)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 20)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 64)
            Assertions.assertTrue(additionOne == 32)
            Assertions.assertTrue(removalOne == 32)
        }

        setSubscriptionOne.unsubscribe()
        elements.addAll(input)
        elements.clear()
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 40)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 20)
            Assertions.assertTrue(anyChangeTwo == 48)
            Assertions.assertTrue(additionTwo == 24)
            Assertions.assertTrue(removalTwo == 24)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 64)
            Assertions.assertTrue(additionOne == 32)
            Assertions.assertTrue(removalOne == 32)
            Assertions.assertTrue(anyChangeTwo == 72)
            Assertions.assertTrue(additionTwo == 36)
            Assertions.assertTrue(removalTwo == 36)
        }

        setSubscriptionTwo.anyChange.unsubscribe()
        elements.addAll(input)
        elements.clear()
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 40)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 20)
            Assertions.assertTrue(anyChangeTwo == 48)
            Assertions.assertTrue(additionTwo == 28)
            Assertions.assertTrue(removalTwo == 28)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 64)
            Assertions.assertTrue(additionOne == 32)
            Assertions.assertTrue(removalOne == 32)
            Assertions.assertTrue(anyChangeTwo == 72)
            Assertions.assertTrue(additionTwo == 40)
            Assertions.assertTrue(removalTwo == 40)
        }

        setSubscriptionTwo.addition?.unsubscribe()
        setSubscriptionTwo.removal?.unsubscribe()
        elements.addAll(input)
        elements.clear()
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 40)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 20)
            Assertions.assertTrue(anyChangeTwo == 48)
            Assertions.assertTrue(additionTwo == 28)
            Assertions.assertTrue(removalTwo == 28)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 64)
            Assertions.assertTrue(additionOne == 32)
            Assertions.assertTrue(removalOne == 32)
            Assertions.assertTrue(anyChangeTwo == 72)
            Assertions.assertTrue(additionTwo == 40)
            Assertions.assertTrue(removalTwo == 40)
        }

        val anyChangeSubscriptionOne = elements.subscribe { anyChangeOne++ }
        val anyChangeSubscriptionTwo = elements.subscribe { anyChangeTwo++ }
        elements.addAll(input)
        elements.clear()
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 48)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 20)
            Assertions.assertTrue(anyChangeTwo == 56)
            Assertions.assertTrue(additionTwo == 28)
            Assertions.assertTrue(removalTwo == 28)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 72)
            Assertions.assertTrue(additionOne == 32)
            Assertions.assertTrue(removalOne == 32)
            Assertions.assertTrue(anyChangeTwo == 80)
            Assertions.assertTrue(additionTwo == 40)
            Assertions.assertTrue(removalTwo == 40)
        }

        anyChangeSubscriptionOne.unsubscribe()
        anyChangeSubscriptionTwo.unsubscribe()
        elements.addAll(input)
        elements.clear()
        if (elements is MutableObservableSet) {
            Assertions.assertTrue(anyChangeOne == 48)
            Assertions.assertTrue(additionOne == 20)
            Assertions.assertTrue(removalOne == 20)
            Assertions.assertTrue(anyChangeTwo == 56)
            Assertions.assertTrue(additionTwo == 28)
            Assertions.assertTrue(removalTwo == 28)
        } else if (elements is MutableObservableList) {
            Assertions.assertTrue(anyChangeOne == 72)
            Assertions.assertTrue(additionOne == 32)
            Assertions.assertTrue(removalOne == 32)
            Assertions.assertTrue(anyChangeTwo == 80)
            Assertions.assertTrue(additionTwo == 40)
            Assertions.assertTrue(removalTwo == 40)
        }

    }
}
