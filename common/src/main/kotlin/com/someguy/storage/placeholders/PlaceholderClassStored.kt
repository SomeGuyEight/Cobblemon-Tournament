package com.someguy.storage.placeholders

import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.someguy.storage.ClassStored
import com.someguy.storage.StoreCoordinates
import com.someguy.storage.position.UuidPosition
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.util.UUID

object PlaceholderClassStored : ClassStored {

    override var uuid: UUID = UUID.randomUUID()
    override val name: String = "Placeholder ClassStored Object"

    override val storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> by lazy {
        SettableObservable(
            StoreCoordinates(PlaceholderStore, position = UuidPosition(uuid = uuid)),
        )
    }

    override fun printProperties() { }
    override fun initialize() = this
    override fun getChangeObservable() = SimpleObservable<ClassStored>()
    override fun saveToNbt(nbt: CompoundTag) = CompoundTag()
    override fun loadFromNBT(nbt: CompoundTag) = this
    override fun saveToJSON(json: JsonObject) = JsonObject()
    override fun loadFromJSON(json: JsonObject) = this

}
