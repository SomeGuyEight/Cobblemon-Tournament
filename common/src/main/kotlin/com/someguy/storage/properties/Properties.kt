package com.someguy.storage.properties

import net.minecraft.nbt.CompoundTag

interface Properties  <F: PropertyFields, P: Properties<F,P,M>, M: MutableProperties<F,P,M>>
    : PropertyFields
{
    fun getHelper(): PropertiesHelper<F,P,M>
    fun deepCopy() : P
    fun deepMutableCopy(): M
    fun saveToNBT(nbt: CompoundTag): CompoundTag
    // does not contain load or set directly b/c properties are immutable
}
