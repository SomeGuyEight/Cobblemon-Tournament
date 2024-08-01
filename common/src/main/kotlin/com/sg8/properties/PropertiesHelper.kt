package com.sg8.properties

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

interface PropertiesHelper<P : Properties<P>> {
    fun saveToNbt(properties: P, nbt: CompoundTag): CompoundTag
    fun loadFromNbt(nbt: CompoundTag): P
    fun setFromNbt(mutable: P, nbt: CompoundTag): P
    fun deepCopy(properties: P): P
    fun copy(properties: P): P
    fun printDebug(properties: P)
    fun displayInChat(properties: P, player: ServerPlayer)
}
