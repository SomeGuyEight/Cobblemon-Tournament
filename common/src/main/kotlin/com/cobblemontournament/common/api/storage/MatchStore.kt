package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.match.TournamentMatch
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MatchStore(id: UUID): DefaultStore<TournamentMatch>(id)
{
    override fun initializeSubclass() { }

    override fun loadFromNBT(nbt: CompoundTag): MatchStore
    {
        instancesDataSetNBT(nbt).forEach { data ->
            val match = TournamentMatch().loadFromNBT(data)
            match.storeCoordinates.set(StoreCoordinates(this, UuidPosition(match.uuid)))
            instances[match.uuid] = match.initialize()
        }
        initialize()
        return this
    }

}
