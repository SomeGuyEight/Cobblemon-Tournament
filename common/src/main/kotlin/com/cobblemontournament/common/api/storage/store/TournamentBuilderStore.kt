package com.cobblemontournament.common.api.storage.store

import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.google.gson.JsonObject
import com.sg8.storage.DefaultStore
import com.sg8.storage.StoreCoordinates
import com.sg8.storage.UuidPosition
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class TournamentBuilderStore(uuid: UUID) :
    DefaultStore<TournamentBuilder>(uuid = uuid) {

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
