package com.someguy.storage

import com.cobblemon.mod.common.api.reactive.Observable
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

interface Properties <P : Properties<P>> {

    val instance: P
    val helper: PropertiesHelper<P>

    fun getChangeObservable(): Observable<P>

    fun setFromNbt(nbt: CompoundTag): P {
        return helper.setFromNbtHelper(mutable = instance, nbt = nbt)
    }

    fun deepCopy(): P = helper.deepCopyHelper(properties = instance)

    fun saveToNbt(nbt: CompoundTag): CompoundTag {
        return helper.saveToNbtHelper(properties = instance, nbt = nbt)
    }

    fun loadFromNbt(nbt: CompoundTag): P {
        return helper.setFromNbtHelper(mutable = instance, nbt = nbt)
    }

    fun logDebug() = helper.logDebugHelper(properties = instance)

    fun displayInChat(player: ServerPlayer) {
        helper.displayInChatHelper(properties = instance, player = player)
    }

}
