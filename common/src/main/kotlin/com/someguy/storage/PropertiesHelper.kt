package com.someguy.storage

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

interface PropertiesHelper<P : Properties<P>> {

    fun setFromNbtHelper(mutable: P, nbt: CompoundTag): P
    fun deepCopyHelper(properties: P): P
    fun saveToNbtHelper(properties: P, nbt: CompoundTag): CompoundTag
    fun loadFromNbtHelper(nbt: CompoundTag): P

    fun logDebugHelper(properties: P)
    fun displayInChatHelper(properties: P, player: ServerPlayer)

}
