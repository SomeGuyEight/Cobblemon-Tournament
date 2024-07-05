package com.someguy.storage.properties

import net.minecraft.nbt.CompoundTag

interface PropertiesCompanion <
        F: PropertyFields,
        P: Properties<F,P,M>,
        M: MutableProperties<F,P,M>>
{
    val helper: PropertiesHelper<F,P,M>

    /**
     * Path for loading [Properties] & [MutableProperties] without
     * initializing an object just to reset the values
     */
    fun loadFromNBT(nbt: CompoundTag): P = helper.loadFromNBT(nbt = nbt)
    fun loadMutableFromNBT(nbt: CompoundTag): M = helper.loadMutableFromNBT(nbt = nbt)

}
