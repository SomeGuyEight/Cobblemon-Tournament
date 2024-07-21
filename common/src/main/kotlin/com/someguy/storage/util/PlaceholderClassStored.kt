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
    override var uuid: UUID = UUID.randomUUID()

    override val name: String = "Placeholder ClassStored"

    override var storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> =
        SettableObservable( StoreCoordinates( PlaceholderStore, UuidPosition(uuid) ))

    override fun printProperties() { }

    /**
     *  Initializes & returns a reference to itself
     *
     * &#9888; Observables will be broken if [initialize] is not called after construction
     */
    override fun initialize(): PlaceholderClassStored = this

    override fun getChangeObservable(): Observable<ClassStored> = SimpleObservable()

    override fun saveToNBT(nbt: CompoundTag): CompoundTag = CompoundTag()

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    override fun loadFromNBT(nbt: CompoundTag): ClassStored = this

    override fun saveToJSON(json: JsonObject): JsonObject = JsonObject()

    /** &#9888; Observables will be broken if [initialize] is not called after construction */
    override fun loadFromJSON(json: JsonObject): ClassStored = this

}
