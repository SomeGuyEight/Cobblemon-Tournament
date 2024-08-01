package com.sg8.storage.placeholders

import com.google.gson.JsonObject
import com.sg8.storage.DefaultStore
import com.sg8.storage.UuidPosition
import net.minecraft.nbt.CompoundTag
import java.util.UUID

object PlaceholderStore : DefaultStore<PlaceholderTypeStored>(uuid = UUID.randomUUID()) {

    override fun isValidPosition(position: UuidPosition) = false
    override fun getFirstAvailablePosition() = null
    override fun instanceNames() = setOf<String>()
    override fun loadFromNbt(nbt: CompoundTag) = this
    override fun loadFromJson(json: JsonObject) = this

}
