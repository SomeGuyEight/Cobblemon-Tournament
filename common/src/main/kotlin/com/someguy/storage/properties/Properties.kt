package com.someguy.storage.properties

import com.cobblemon.mod.common.api.reactive.Observable
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

interface Properties <P: Properties <P> >
{
    val instance: P
    val helper: PropertiesHelper <P>

    fun getAllObservables(): Iterable <Observable <*> >
    fun getChangeObservable(): Observable <P>

    fun deepCopy(): P = helper.deepCopyHelper( instance )
    fun setFromProperties( from: P ): P     = helper.setFromPropertiesHelper( instance, from )
    fun setFromNBT( nbt: CompoundTag ): P   = helper.setFromNBTHelper( instance, nbt )
    fun saveToNBT( nbt: CompoundTag )       = helper.saveToNBTHelper( instance, nbt )
    fun loadFromNBT( nbt: CompoundTag ): P  = helper.setFromNBTHelper( instance, nbt )
    fun logDebug() = helper.logDebugHelper( instance )
    fun displayInChat( player: ServerPlayer ) = helper.displayInChatHelper( instance, player )
}
