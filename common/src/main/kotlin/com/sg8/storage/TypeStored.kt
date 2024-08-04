package com.sg8.storage

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.util.UUID


interface TypeStored {

    val uuid: UUID
    val name: String
    val storeCoordinates: SettableObservable<StoreCoordinates<*, *>?>

    fun initialize(): TypeStored
    fun getObservable(): Observable<*>
    fun saveToNbt(nbt: CompoundTag): CompoundTag
    fun saveToJSON(json: JsonObject): JsonObject
    fun loadFromNBT(nbt: CompoundTag): TypeStored
    fun loadFromJSON(json: JsonObject): TypeStored
    fun printProperties()

}
