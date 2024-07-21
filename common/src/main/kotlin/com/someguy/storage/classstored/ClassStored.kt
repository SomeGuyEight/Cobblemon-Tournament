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
    val storeCoordinates: SettableObservable<StoreCoordinates<*,*>?>

    /** Initializes & returns a reference to itself */
    fun initialize(): ClassStored
    fun getChangeObservable(): Observable <*>
    fun printProperties()

    fun saveToNBT( nbt: CompoundTag ): CompoundTag
    fun loadFromNBT( nbt: CompoundTag ): ClassStored
    fun saveToJSON( json: JsonObject ): JsonObject
    fun loadFromJSON( json: JsonObject ): ClassStored

    /* TODO: add fun getDebugInfo()
    fun getDebugInfo()
     */

}
