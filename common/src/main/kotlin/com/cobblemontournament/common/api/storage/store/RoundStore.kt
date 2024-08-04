package com.cobblemontournament.common.api.storage.store

import com.cobblemontournament.common.round.TournamentRound
import com.google.gson.JsonObject
import com.sg8.storage.DefaultStore
import com.sg8.storage.StoreCoordinates
import com.sg8.storage.UuidPosition
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class RoundStore(uuid: UUID) : DefaultStore<TournamentRound>(uuid = uuid) {

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
