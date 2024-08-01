package com.sg8.properties

import com.cobblemon.mod.common.api.reactive.Observable
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

interface Properties <P : Properties<P>> {
    val observable: Observable<P>
    fun emitChange()
    fun saveToNbt(nbt: CompoundTag): CompoundTag
    fun loadFromNbt(nbt: CompoundTag): P
    fun setFromNbt(nbt: CompoundTag): P
    fun deepCopy(): P
    fun copy(): P
    fun printDebug()
    fun displayInChat(player: ServerPlayer)
}
