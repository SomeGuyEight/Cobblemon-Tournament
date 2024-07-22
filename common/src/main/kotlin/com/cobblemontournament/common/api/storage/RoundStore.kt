package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.round.TournamentRound
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class RoundStore(storeID: UUID) : DefaultStore<TournamentRound>(storeID)
{
    override fun initializeSubclass() { }

    override fun loadFromNbt(nbt: CompoundTag): RoundStore {
        for (dataCompound in instancesDataSetNBT(nbt)) {
            val round = TournamentRound().loadFromNBT(dataCompound)
            round.storeCoordinates.set(StoreCoordinates(
                store = this,
                position = UuidPosition(round.uuid))
            )
            instances[round.uuid] = round.initialize()
        }
        initialize()
        return this
    }

}
