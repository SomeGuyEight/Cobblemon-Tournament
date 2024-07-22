package com.cobblemontournament.common.generator.indexedseed

import com.someguy.collections.SortType

object IndexedSeedGenerator {

    fun getIndexedSeedArray(
        seedCount: Int,
        currentSortType: SortType? = null,
    ): IndexedSeedList {
        val finalSize = ceilToPowerOfTwo(seedCount)
        var seedPairs = mutableListOf<Pair<Int, Int>>()
        seedPairs.add(Pair(1, 2))

        while ((seedPairs.size * 2) < finalSize) {
            // add filler seed to collection b/c all current seeds processed ( filler seed == -1 )
            val newPairs = mutableListOf<Pair<Int, Int>>()
            for (pair in seedPairs) {
                newPairs.add(Pair(pair.first, -1))
                newPairs.add(Pair(pair.second, -1))
            }

            seedPairs = newPairs
            val size = seedPairs.size
            var targetSeed = size // not -1 b/c seeds start at 1 instead of 0...
            for (i in size until (size * 2)) {
                var index: Int = -1
                run loop@ {
                    for (ii in 0 until seedPairs.size) {
                        if (seedPairs[ii].first == targetSeed) {
                            index = ii
                            return@loop
                        }
                    }
                }
                seedPairs.removeAt(index)
                seedPairs.add(index, Pair(targetSeed--, (i + 1))) // b/c seeds start at 1...
            }
        }

        val orderedList = ArrayDeque<Int>()
        seedPairs.forEach {
            orderedList.add(it.first)
            orderedList.add(it.second)
        }

        return finalSort(orderedList, currentSortType)
    }

    private fun finalSort(seeds: ArrayDeque<Int>, sortStatus: SortType?): IndexedSeedList {
        val queueList = ArrayList<ArrayDeque<Int>>()
        queueList.add(seeds)

        while (queueList[0].size > 1) {
            val innerIterations = queueList.size
            for(ii in 0 until innerIterations) {
                val index = ii * 2
                var originalQueue = queueList.removeAt(index)
                var newQueue = ArrayDeque<Int>()

                while (newQueue.size < originalQueue.size) {
                    newQueue.addFirst(originalQueue.removeLast())
                }

                if (originalQueue.minOrNull()!! < newQueue.minOrNull()!!) {
                    // original has lower min value (higher seed) -> invert newQueue
                    newQueue = invertQueue(newQueue)
                } else {
                    originalQueue = invertQueue(originalQueue)
                }

                queueList.add(index, originalQueue)
                // if/else here so last queue added doesn't throw out of range exception
                if ((index + 1) < queueList.size) {
                    queueList.add((index + 1), newQueue)
                } else {
                    queueList.add(newQueue)
                }
            }
        }

        // each queue has a single seed & is in order
        val finalSeeds = mutableListOf<IndexedSeed>()
        val size = queueList.size
        for(i in 0 until size) {
            finalSeeds.add(
                IndexedSeed(
                    index = i,
                    seed = queueList.removeFirst().removeFirst(),
                )
            )
        }

        return if (sortStatus != null) {
            IndexedSeedList(finalSeeds, sortStatus)
        } else {
            IndexedSeedList(finalSeeds)
        }
    }

    fun ceilToPowerOfTwo(value: Int): Int {
        val maxBitInt = Integer.highestOneBit(value)
        return if ((value != 0) && ((value xor maxBitInt) == 0)) value else maxBitInt shl 1
    }

    private fun invertQueue(queue: ArrayDeque<Int>): ArrayDeque<Int> {
        val inverted = ArrayDeque<Int>()
        queue.forEach { inverted.addFirst(it) }
        return inverted
    }

}
