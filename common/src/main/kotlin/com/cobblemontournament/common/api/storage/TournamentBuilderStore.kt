package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class TournamentBuilderStore(
    uuid: UUID
) : DefaultStore<TournamentBuilder>(uuid)
{
    override fun initializeSubclass() { }

    override fun loadFromNBT(nbt: CompoundTag): TournamentBuilderStore
    {
        instancesDataSetNBT(nbt).forEach { data ->
            val builder = TournamentBuilder().loadFromNBT(data)
            builder.storeCoordinates.set(StoreCoordinates(this, UuidPosition(builder.uuid)))
            instances[builder.uuid] = builder.initialize()
        }
        initialize()
        return this
    }

}