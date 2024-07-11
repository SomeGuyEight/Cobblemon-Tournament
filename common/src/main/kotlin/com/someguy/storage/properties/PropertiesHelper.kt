package com.someguy.storage.properties

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

interface PropertiesHelper <P: Properties<P>>
{
    fun deepCopyHelper( properties: P ): P
    fun setFromPropertiesHelper( mutable: P, from: P ): P
    fun setFromNBTHelper( mutable: P, nbt: CompoundTag ): P
    fun saveToNBTHelper( properties: P, nbt: CompoundTag ): CompoundTag
    fun loadFromNBTHelper( nbt: CompoundTag ): P
    fun logDebugHelper( properties: P )
    fun displayInChatHelper( properties: P, player: ServerPlayer )
}
