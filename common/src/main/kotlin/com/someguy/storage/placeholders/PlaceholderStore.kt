package com.someguy.storage.placeholders

import com.google.gson.JsonObject
import com.someguy.storage.position.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

object PlaceholderStore : DefaultStore<PlaceholderClassStored>(storeID = UUID.randomUUID()) {

    override fun isValidPosition(position: UuidPosition) = false
    override fun getFirstAvailablePosition() = null
    override fun instanceNames() = setOf<String>()
    override fun loadFromNbt(nbt: CompoundTag) = this
    override fun loadFromJson(json: JsonObject) = this

}
