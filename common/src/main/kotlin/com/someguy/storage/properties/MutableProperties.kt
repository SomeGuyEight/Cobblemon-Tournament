package com.someguy.storage.properties

import net.minecraft.nbt.CompoundTag
import java.util.UUID

interface MutableProperties<T: Properties>
{
    companion object
    {
        val minUUID = UUID(0,0)
    }
    fun toProperties() : T
    fun setFromProperties(properties: T) : MutableProperties<T>
    fun saveToNBT(nbt: CompoundTag) : CompoundTag
    fun loadFromNBT(nbt: CompoundTag) : MutableProperties<T>
}