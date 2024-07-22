package com.someguy.storage.util

import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

object PlaceholderStore: DefaultStore<PlaceholderClassStored>(UUID.randomUUID())
{
    override fun initializeSubclass() { }
    override fun getFirstAvailablePosition(): UuidPosition? = null
    override fun instanceNames(): Set<String> = setOf()
    override fun loadFromNbt(nbt: CompoundTag): DefaultStore<PlaceholderClassStored> = this
    override fun isValidPosition(position: UuidPosition): Boolean = false
}
