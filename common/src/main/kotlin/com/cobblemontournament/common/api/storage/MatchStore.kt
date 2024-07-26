package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.match.TournamentMatch
import com.google.gson.JsonObject
import com.someguy.storage.util.StoreID
import com.someguy.storage.StoreCoordinates
import com.someguy.storage.position.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag

class MatchStore(storeID: StoreID) : DefaultStore<TournamentMatch>(storeID = storeID) {

    override fun loadFromNbt(nbt: CompoundTag): MatchStore {
        for (dataCompound in loadInstanceDataSetFromNbt(nbt = nbt)) {
            val match = TournamentMatch.loadFromNbt(nbt = dataCompound)
            match.storeCoordinates.set(
                StoreCoordinates(store = this, position = UuidPosition(uuid = match.uuid))
            )
            instances[match.uuid] = match.initialize()
        }
        initialize()
        return this
    }

    override fun loadFromJson(json: JsonObject) = TODO("Not yet implemented")

}
