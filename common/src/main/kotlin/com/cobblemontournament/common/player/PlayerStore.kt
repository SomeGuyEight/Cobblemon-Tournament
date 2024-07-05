package com.cobblemontournament.common.player

import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class PlayerStore(id: UUID): DefaultStore<TournamentPlayer>(id)
{
    override fun initializeSubclass() { }

    override fun loadFromNBT(nbt: CompoundTag): PlayerStore
    {
        instancesDataSetNBT(nbt).forEach { data ->
            val player = TournamentPlayer().loadFromNBT(data)
            player.storeCoordinates.set( StoreCoordinates(this, UuidPosition(player.uuid)))
            instances[player.uuid] = player.initialize()
        }
        initialize()
        return this
    }

}