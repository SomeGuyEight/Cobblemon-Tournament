package com.someguy.storage.properties

import net.minecraft.nbt.CompoundTag

interface PropertiesHelper <F: PropertyFields, P: Properties<F,P,M>, M: MutableProperties<F,P,M>>
{
    fun deepCopyHelper(properties: F): P
    fun deepMutableCopyHelper(properties: F): M
    fun setFromPropertiesHelper(mutable: M, from: F): M
    fun setFromNBTHelper(mutable: M, nbt: CompoundTag): M
    fun saveToNBTHelper(properties: F, nbt: CompoundTag): CompoundTag
    fun loadFromNBT(nbt: CompoundTag): P
    fun loadMutableFromNBT(nbt: CompoundTag): M

    /*
        TODO: implement debug Info Inner
    fun getDebugInfoInner(): Set<String>
     */
}
