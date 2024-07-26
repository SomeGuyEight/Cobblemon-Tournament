package com.cobblemontournament.common.generator.indexedseed

import com.cobblemontournament.common.util.ceilToPowerOfTwo

object IndexedSeedGenerator {

    fun getIndexedSeedArray(seedCount: Int, sortType: SortType? = null): IndexedSeedList {
        val finalSize = ceilToPowerOfTwo(seedCount)

        var seedPairs = mutableListOf<Pair<Int, Int>>()
        seedPairs.add(1 to 2)

        while ((seedPairs.size * 2) < finalSize) {
            // add filler seed to collection b/c all current seeds processed ( filler seed == -1 )
            val newPairs = mutableListOf<Pair<Int, Int>>()
            for (pair in seedPairs) {
                newPairs.add(pair.first to -1)
                newPairs.add(pair.second to -1)
            }

            seedPairs = newPairs
            val size = seedPairs.size
            var targetSeed = size
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
                seedPairs.add(index, (targetSeed-- to (i + 1)))
            }
        }

        val orderedList = ArrayDeque<Int>()
        seedPairs.forEach { (seedOne, seedTwo) ->
            orderedList.add(seedOne)
            orderedList.add(seedTwo)
        }

        return finalSort(orderedList, sortType)
    }

    private fun finalSort(seeds: ArrayDeque<Int>, sortType: SortType?): IndexedSeedList {
        val queueList = ArrayList<ArrayDeque<Int>>()
        queueList.add(seeds)

        while (queueList[0].size > 1) {
            val innerIterations = queueList.size
            for(i in 0 until innerIterations) {
                val index = i * 2
                var originalQueue = queueList.removeAt(index)
                var newQueue = ArrayDeque<Int>()

                while (newQueue.size < originalQueue.size) {
                    newQueue.addFirst(originalQueue.removeLast())
                }

                // invert queue with lower min value (aka higher seed)
                if (originalQueue.minOrNull()!! < newQueue.minOrNull()!!) {
                    newQueue = invertQueue(newQueue)
                } else {
                    originalQueue = invertQueue(originalQueue)
                }

                queueList.add(index, originalQueue)
                // if/else here so last queue added doesn't throw out of range exception
                if (index < queueList.lastIndex) {
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
            finalSeeds.add(IndexedSeed(index = i, seed = queueList.removeFirst().removeFirst()))
        }

        return if (sortType != null) {
            IndexedSeedList(finalSeeds, sortType)
        } else {
            IndexedSeedList(finalSeeds)
        }
    }

    private fun invertQueue(queue: ArrayDeque<Int>): ArrayDeque<Int> {
        val inverted = ArrayDeque<Int>()
        queue.forEach { inverted.addFirst(it) }
        return inverted
    }

}
