package com.someguy.storage.util

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.util.UUID

object PlaceholderClassStored: ClassStored
{
    private val minUuid: UUID = UUID(0,0)
    override var uuid: UUID = minUuid
    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable( StoreCoordinates( PlaceholderStore, UuidPosition(minUuid) ))

    override fun initialize() { }

    override fun getChangeObservable(): Observable<ClassStored> = SimpleObservable()

    override fun saveToNBT(nbt: CompoundTag): CompoundTag = CompoundTag()

    override fun loadFromNBT(nbt: CompoundTag): ClassStored = this

    override fun saveToJSON(json: JsonObject): JsonObject = JsonObject()

    override fun loadFromJSON(json: JsonObject): ClassStored = this

}