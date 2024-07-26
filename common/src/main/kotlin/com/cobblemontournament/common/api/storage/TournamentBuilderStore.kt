package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.google.gson.JsonObject
import com.someguy.storage.util.StoreID
import com.someguy.storage.StoreCoordinates
import com.someguy.storage.position.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag

class TournamentBuilderStore(storeID: StoreID) :
    DefaultStore<TournamentBuilder>(storeID = storeID) {

    override fun loadFromNbt(nbt: CompoundTag): TournamentBuilderStore {
        for (dataCompound in loadInstanceDataSetFromNbt(nbt = nbt)) {
            val builder = TournamentBuilder.loadFromNbt(nbt = dataCompound)
            builder.storeCoordinates.set(
                StoreCoordinates(store = this, position = UuidPosition(uuid = builder.uuid))
            )
            instances[builder.uuid] = builder.initialize()
        }
        initialize()
        return this
    }

    override fun loadFromJson(json: JsonObject) = TODO("Not yet implemented")

}
