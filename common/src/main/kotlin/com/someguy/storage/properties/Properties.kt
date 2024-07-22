package com.someguy.storage.properties

import com.cobblemon.mod.common.api.reactive.Observable
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

interface Properties <P : Properties<P>> {

    val instance: P
    val helper: PropertiesHelper <P>

    fun deepCopy(): P = helper.deepCopyHelper(properties = instance)
    fun setFromProperties(from: P): P = helper.setFromPropertiesHelper(mutable = instance, from = from)
    fun setFromNBT(nbt: CompoundTag): P = helper.setFromNBTHelper(mutable = instance, nbt = nbt)
    fun saveToNBT(nbt: CompoundTag) = helper.saveToNBTHelper(properties = instance, nbt = nbt)
    fun loadFromNBT(nbt: CompoundTag): P = helper.setFromNBTHelper(mutable = instance, nbt = nbt)
    fun logDebug() = helper.logDebugHelper(properties = instance)
    fun displayInChat(player: ServerPlayer) = helper.displayInChatHelper(properties = instance, player = player)

    fun getAllObservables(): Iterable<Observable<*>>
    fun getChangeObservable(): Observable<P>

}
