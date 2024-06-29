package com.someguy.storage.classstored.extension

import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.util.PlaceholderClassStored

object ClassStoredExtension
{
    fun ClassStored.defaultStoreCoords(): SettableObservable<StoreCoordinates<*, *>?> =
        PlaceholderClassStored.storeCoordinates
}