package com.someguy.storage

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.google.gson.JsonObject
import com.someguy.storage.util.InstanceID
import net.minecraft.nbt.CompoundTag

interface ClassStored {

    val uuid: InstanceID
    val name: String
    val storeCoordinates: SettableObservable<StoreCoordinates<*, *>?>

    fun initialize(): ClassStored

    fun getChangeObservable(): Observable<*>

    fun saveToNbt(nbt: CompoundTag): CompoundTag
    fun loadFromNBT(nbt: CompoundTag): ClassStored
    fun saveToJSON(json: JsonObject): JsonObject
    fun loadFromJSON(json: JsonObject): ClassStored

    fun printProperties()

}
