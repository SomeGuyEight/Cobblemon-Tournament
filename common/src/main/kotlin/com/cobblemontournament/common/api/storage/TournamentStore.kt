package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.tournament.Tournament
import com.google.gson.JsonObject
import com.someguy.storage.util.StoreID
import com.someguy.storage.StoreCoordinates
import com.someguy.storage.position.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag

open class TournamentStore(storeID: StoreID) : DefaultStore<Tournament>(storeID = storeID) {

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
