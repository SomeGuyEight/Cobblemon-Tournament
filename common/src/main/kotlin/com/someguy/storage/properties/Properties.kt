package com.someguy.storage.properties

import net.minecraft.nbt.CompoundTag

interface Properties
{
    fun saveToNBT(nbt: CompoundTag): CompoundTag
}