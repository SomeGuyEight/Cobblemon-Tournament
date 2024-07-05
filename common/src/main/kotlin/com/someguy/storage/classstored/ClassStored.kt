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
    val name: String
    val storeCoordinates: SettableObservable<StoreCoordinates<*, *>?>

    fun initialize(): ClassStored
    fun getChangeObservable(): Observable<*>
    fun printProperties()

    fun saveToNBT(nbt: CompoundTag): CompoundTag
    fun loadFromNBT(nbt: CompoundTag): ClassStored
    fun saveToJSON(json: JsonObject): JsonObject
    fun loadFromJSON(json: JsonObject): ClassStored

    /* TODO: add fun getDebugInfo(): Set<String>
    // template below
    fun getDebugInfo(): Set<String>
    {
        val debug = mutableSetOf<String>()
        debug.add("Name: $this.name (${this::class.java})")
        debug.add(" - ID: $this.uuid")
        // below is for any fields the class implements or other pertinent info
        debug.add(" - ")
    }
     */

}
