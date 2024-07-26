package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.player.TournamentPlayer
import com.google.gson.JsonObject
import com.someguy.storage.util.StoreID
import com.someguy.storage.StoreCoordinates
import com.someguy.storage.position.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag

class PlayerStore(storeID: StoreID) : DefaultStore<TournamentPlayer>(storeID = storeID) {

    override fun loadFromNbt(nbt: CompoundTag): PlayerStore {
        for (dataCompound in loadInstanceDataSetFromNbt(nbt = nbt)) {
            val player = TournamentPlayer.loadFromNbt(nbt = dataCompound)
            player.storeCoordinates.set(
                StoreCoordinates(store = this, position = UuidPosition(uuid = player.uuid))
            )
            instances[player.uuid] = player.initialize()
        }
        initialize()
        return this
    }

    override fun loadFromJson(json: JsonObject) = TODO("Not yet implemented")

}
