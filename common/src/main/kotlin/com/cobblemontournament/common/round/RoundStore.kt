package com.cobblemontournament.common.round

import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class RoundStore(id: UUID): DefaultStore<TournamentRound>(id)
{
    override fun initializeSubclass() { }

    override fun loadFromNBT(nbt: CompoundTag): RoundStore
    {
        instancesDataSetNBT(nbt).forEach { data ->
            val round = TournamentRound().loadFromNBT(data)
            round.storeCoordinates.set(StoreCoordinates(this, UuidPosition(round.uuid)))
            instances[round.uuid] = round.initialize()
        }
        initialize()
        return this
    }

}
