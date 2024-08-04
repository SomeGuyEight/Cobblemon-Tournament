package com.sg8.collections.reactive

import org.junit.jupiter.api.Test

object AllCollectionTests {

    @Test
    fun runConstructionTests() {
        ConstructionTests.setConstruction()
        ConstructionTests.listConstruction()
        ConstructionTests.subListConstruction()
        ConstructionTests.mapConstruction()
    }

    @Test
    fun runSerializationTests() {
        SerializationTests.setSerialization()
        SerializationTests.listSerialization()
        SerializationTests.subListSerialization()
        SerializationTests.mapSerialization()
    }

    @Test
    fun runIteratorTests() {
        IteratorTests.setIterator()
        IteratorTests.listIterator()
        IteratorTests.subListIterator()
        IteratorTests.mapIterator()
    }

    @Test
    fun runEmittingTests() {
        EmittingTests.setAsCollectionEmitChange()
        EmittingTests.listAsCollectionEmitChange()
        EmittingTests.listAndSubListEmitChange()
        EmittingTests.mapEmitChange()
    }
}
