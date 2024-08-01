package com.sg8.storage.placeholders

import com.cobblemon.mod.common.api.reactive.*
import com.sg8.storage.*
import com.google.gson.JsonObject
import com.sg8.storage.UuidPosition
import net.minecraft.nbt.CompoundTag
import java.util.UUID

object PlaceholderTypeStored : TypeStored {

    override var uuid: UUID = UUID.randomUUID()
    override val name: String = this.javaClass.simpleName
    override val storeCoordinates: SettableObservable<StoreCoordinates<*, *>?> = run {
        SettableObservable(
            StoreCoordinates(PlaceholderStore, position = UuidPosition(uuid = uuid)),
        )
    }

    override fun initialize() = this
    override fun getObservable(): Observable<TypeStored> = SimpleObservable()
    override fun saveToNbt(nbt: CompoundTag) = CompoundTag()
    override fun saveToJSON(json: JsonObject) = JsonObject()
    override fun loadFromNBT(nbt: CompoundTag) = this
    override fun loadFromJSON(json: JsonObject) = this
    override fun printProperties() { }

}
