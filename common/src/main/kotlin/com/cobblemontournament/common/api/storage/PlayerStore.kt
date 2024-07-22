package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.player.TournamentPlayer
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class PlayerStore(storeID: UUID) : DefaultStore<TournamentPlayer>(storeID)
{
    override fun initializeSubclass() { }

    override fun loadFromNbt(nbt: CompoundTag): PlayerStore {
        for (dataCompound in instancesDataSetNBT(nbt)) {
            val player = TournamentPlayer().loadFromNBT(dataCompound)
            player.storeCoordinates.set(StoreCoordinates(
                store = this,
                position = UuidPosition(player.uuid))
            )
            instances[player.uuid] = player.initialize()
        }
        initialize()
        return this
    }

}