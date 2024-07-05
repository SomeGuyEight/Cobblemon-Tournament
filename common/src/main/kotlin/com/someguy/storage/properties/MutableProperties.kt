package com.someguy.storage.properties

import net.minecraft.nbt.CompoundTag

interface MutableProperties <F: PropertyFields, P: Properties<F,P,M>, M: MutableProperties<F,P,M>>
    : PropertyFields
{
    fun getHelper(): PropertiesHelper<F,P,M>
    fun deepCopy(): P
    fun deepMutableCopy(): M
    fun setFromProperties(from: F): M
    fun setFromNBT(nbt: CompoundTag): M
    fun saveToNBT( nbt: CompoundTag): CompoundTag
    // does not contain load b/c helper can do so through a constructor directly
    //  also load === set at instance level

}
