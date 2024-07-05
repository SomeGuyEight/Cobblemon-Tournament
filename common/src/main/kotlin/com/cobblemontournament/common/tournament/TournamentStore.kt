package com.cobblemontournament.common.tournament

import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

open class TournamentStore(id: UUID): DefaultStore<Tournament>(id)
{
    override fun initializeSubclass() { }

    override fun loadFromNBT(nbt: CompoundTag): TournamentStore
    {
        instancesDataSetNBT(nbt).forEach { data ->
            val tournament = Tournament().loadFromNBT(data)
            tournament.storeCoordinates.set(StoreCoordinates(this, UuidPosition(tournament.uuid)))
            this.instances[tournament.uuid] = tournament.initialize()
        }
        initialize()
        return this
    }

}
