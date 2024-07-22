package com.cobblemontournament.common.api.storage

import com.cobblemontournament.common.tournament.Tournament
import com.someguy.storage.coordinates.StoreCoordinates
import com.someguy.storage.position.simple.UuidPosition
import com.someguy.storage.store.DefaultStore
import net.minecraft.nbt.CompoundTag
import java.util.UUID

open class TournamentStore(storeID: UUID) : DefaultStore<Tournament>(storeID) {

    override fun initializeSubclass() { }

    override fun loadFromNbt(nbt: CompoundTag): TournamentStore {
        for (dataCompound in instancesDataSetNBT(nbt)) {
            val tournament = Tournament().loadFromNBT(dataCompound)
            tournament.storeCoordinates.set(StoreCoordinates(
                store = this,
                position = UuidPosition(tournament.uuid))
            )
            this.instances[tournament.uuid] = tournament.initialize()
        }
        initialize()
        return this
    }

}
