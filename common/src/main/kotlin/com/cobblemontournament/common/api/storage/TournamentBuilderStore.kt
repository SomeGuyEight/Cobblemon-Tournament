package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class TournamentBuilderStore(storeID: UUID) : DefaultStore<TournamentBuilder>(storeID) {

    override fun initializeSubclass() { }

    override fun loadFromNbt(nbt: CompoundTag): TournamentBuilderStore {
        for (dataCompound in instancesDataSetNBT(nbt)) {
            val builder = TournamentBuilder().loadFromNBT(dataCompound)
            builder.storeCoordinates.set(StoreCoordinates(
                store = this,
                position = UuidPosition(builder.uuid))
            )
            instances[builder.uuid] = builder.initialize()
        }
        initialize()
        return this
    }

}