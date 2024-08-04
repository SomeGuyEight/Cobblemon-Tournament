package com.cobblemontournament.common.api.storage.store

import com.cobblemontournament.common.match.TournamentMatch
import com.google.gson.JsonObject
import com.sg8.storage.DefaultStore
import com.sg8.storage.StoreCoordinates
import com.sg8.storage.UuidPosition
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MatchStore(uuid: UUID) : DefaultStore<TournamentMatch>(uuid = uuid) {

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
