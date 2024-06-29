package com.someguy.storage.classstored

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.someguy.storage.coordinates.StoreCoordinates
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.util.UUID

interface ClassStored
{
    val uuid: UUID
    val storeCoordinates: SettableObservable<StoreCoordinates<*, *>?>

    fun initialize()
    fun getChangeObservable(): Observable<ClassStored>

    fun saveToNBT(nbt: CompoundTag): CompoundTag
    fun loadFromNBT(nbt: CompoundTag): ClassStored
    fun saveToJSON(json: JsonObject): JsonObject
    fun loadFromJSON(json: JsonObject): ClassStored
}
