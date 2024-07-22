package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.match.TournamentMatch
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class MatchStore(storeID: UUID) : DefaultStore<TournamentMatch>(storeID) {

    override fun initializeSubclass() { }

    override fun loadFromNbt(nbt: CompoundTag): MatchStore {
        for (dataCompound in instancesDataSetNBT(nbt)) {
            val match = TournamentMatch().loadFromNBT(dataCompound)
            match.storeCoordinates.set(StoreCoordinates(
                store = this,
                position = UuidPosition(match.uuid))
            )
            instances[match.uuid] = match.initialize()
        }
        initialize()
        return this
    }

}
