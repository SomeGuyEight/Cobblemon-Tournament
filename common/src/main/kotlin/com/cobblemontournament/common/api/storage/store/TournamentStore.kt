package com.cobblemontournament.common.api.storage.store

import com.cobblemontournament.common.tournament.Tournament
import com.google.gson.JsonObject
import com.sg8.storage.StoreCoordinates
import com.sg8.storage.DefaultStore
import com.sg8.storage.UuidPosition
import net.minecraft.nbt.CompoundTag
import java.util.UUID

open class TournamentStore(uuid: UUID) : DefaultStore<Tournament>(uuid = uuid) {

    override fun loadFromNbt(nbt: CompoundTag): TournamentStore {
        for (dataCompound in loadInstanceDataSetFromNbt(nbt = nbt)) {
            val tournament = Tournament.loadFromNbt(nbt = dataCompound)
            tournament.storeCoordinates.set(
                StoreCoordinates(store = this, position = UuidPosition(uuid = tournament.uuid))
            )
            this.instances[tournament.uuid] = tournament.initialize()
        }
        initialize()
        return this
    }

    override fun loadFromJson(json: JsonObject) = TODO("Not yet implemented")

}
