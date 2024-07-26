package com.cobblemontournament.common.generator.indexedseed

import org.slf4j.helpers.Util

class IndexedSeedList(
    indexedSeeds: List<IndexedSeed>,
    sortType: SortType = SortType.INDEX_ASCENDING,
) {

    private var indexedSeeds: List<IndexedSeed> = indexedSeeds.toList()

    var sortType: SortType = sortType
        private set

    val size: Int get() = indexedSeeds.size

    init {
        sortBy(sortType = sortType)
    }

    fun sortBy(sortType: SortType): Boolean {
        indexedSeeds = when (sortType) {
            SortType.INDEX_ASCENDING -> indexedSeeds.sortedBy { it.index }
            SortType.INDEX_DESCENDING -> indexedSeeds.sortedByDescending { it.index }
            SortType.VALUE_ASCENDING -> indexedSeeds.sortedBy { it.seed }
            SortType.VALUE_DESCENDING -> indexedSeeds.sortedByDescending { it.seed }
            else -> return false
        }
        this.sortType = sortType
        return true
    }

    fun deepCopy(): List<IndexedSeed> {
        val copy = mutableListOf<IndexedSeed>()
        indexedSeeds.forEach { copy.add(IndexedSeed(it.index, it.seed)) }
        return copy
    }

    fun print() = indexedSeeds.forEach { Util.report(it.toString()) }

}
