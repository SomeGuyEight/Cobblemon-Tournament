package com.cobblemontournament.common.util;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Vector;

public class IndexedSeedArray
{
    public IndexedSeedArray(
            Vector<IndexedSeed> collection,
            @Nullable
            CollectionSortType sortingType
    ) {
        this.collection = collection;
        if (sortingType == null) {
            return;
        }
        switch (sortingType) {
            case VALUE_ASCENDING -> sortBySeedAscending();
            case VALUE_DESCENDING -> sortBySeedDescending();
            case INDEX_ASCENDING -> sortByIndexAscending();
            case INDEX_DESCENDING -> sortByIndexDescending();
        }
    }

    public Vector<IndexedSeed> collection;
    private CollectionSortType indexedSeedStatus = CollectionSortType.UNKNOWN;
    public CollectionSortType sortStatus()
    {
        return indexedSeedStatus;
    }

    public int size()
    {
        return collection.size();
    }
    public IndexedSeed get(int index)
    {
        return collection.get(index);
    }

    public void sortBySeedAscending()
    {
        collection.sort(Comparator.comparing(IndexedSeed::seed));
        indexedSeedStatus = CollectionSortType.VALUE_ASCENDING;
    }
    public void sortBySeedDescending()
    {
        collection.sort(Comparator.comparing(IndexedSeed::seed).reversed());
        indexedSeedStatus = CollectionSortType.VALUE_DESCENDING;
    }
    public void sortByIndexAscending()
    {
        collection.sort(Comparator.comparing(IndexedSeed::index));
        indexedSeedStatus = CollectionSortType.INDEX_ASCENDING;
    }
    public void sortByIndexDescending()
    {
        collection.sort(Comparator.comparing(IndexedSeed::index).reversed());
        indexedSeedStatus = CollectionSortType.INDEX_DESCENDING;
    }

}
