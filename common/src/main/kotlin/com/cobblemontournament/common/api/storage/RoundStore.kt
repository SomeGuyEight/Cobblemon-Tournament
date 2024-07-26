package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.round.TournamentRound
import com.google.gson.JsonObject
import com.someguy.storage.util.StoreID
import com.someguy.storage.StoreCoordinates
import com.someguy.storage.position.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag

class RoundStore(storeID: StoreID) : DefaultStore<TournamentRound>(storeID = storeID) {

    override fun loadFromNbt(nbt: CompoundTag): RoundStore {
        for (dataCompound in loadInstanceDataSetFromNbt(nbt = nbt)) {
            val round = TournamentRound.loadFromNbt(nbt = dataCompound)
            round.storeCoordinates.set(
                StoreCoordinates(store = this, position = UuidPosition(uuid = round.uuid))
            )
            instances[round.uuid] = round.initialize()
        }
        initialize()
        return this
    }

    override fun loadFromJson(json: JsonObject) = TODO("Not yet implemented")

}
