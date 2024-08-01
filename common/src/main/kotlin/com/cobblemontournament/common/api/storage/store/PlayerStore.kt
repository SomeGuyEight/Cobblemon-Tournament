package com.cobblemontournament.common.api.storage.store

import com.cobblemontournament.common.player.TournamentPlayer
import com.google.gson.JsonObject
import com.sg8.storage.StoreCoordinates
import com.sg8.storage.DefaultStore
import com.sg8.storage.UuidPosition
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class PlayerStore(uuid: UUID) : DefaultStore<TournamentPlayer>(uuid = uuid) {

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
