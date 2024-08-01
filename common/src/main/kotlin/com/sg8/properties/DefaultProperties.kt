package com.sg8.properties

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

interface DefaultProperties <P : DefaultProperties<P>> : Properties<P> {

    val instance: P
    val helper: PropertiesHelper<P>

    override fun saveToNbt(nbt: CompoundTag): CompoundTag {
        return helper.saveToNbt(properties = instance, nbt = nbt)
    }

    override fun loadFromNbt(nbt: CompoundTag): P {
        return helper.loadFromNbt(nbt = nbt)
    }

    override fun setFromNbt(nbt: CompoundTag): P {
        return helper.setFromNbt(mutable = instance, nbt = nbt)
    }

    override fun deepCopy(): P = helper.deepCopy(properties = instance)

    override fun copy(): P = helper.copy(instance)

    override fun printDebug() = helper.printDebug(properties = instance)

    override fun displayInChat(player: ServerPlayer) {
        helper.displayInChat(properties = instance, player = player)
    }

    companion object {
        inline fun <reified P : DefaultProperties<P>> new(): P {
            return P::class.java.getConstructor().newInstance()
        }
    }
}

