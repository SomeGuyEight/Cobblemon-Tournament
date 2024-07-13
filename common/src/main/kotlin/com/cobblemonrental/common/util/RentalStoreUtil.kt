package com.cobblemonrental.common.util

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.storage.adapter.flatfile.NBTStoreAdapter
import com.cobblemon.mod.common.api.storage.factory.FileBackedPokemonStoreFactory
import com.cobblemon.mod.common.util.fromJson
import com.cobblemonrental.common.api.storage.pokemon.RentalPokemonStore
import com.cobblemonrental.common.api.storage.team.RentalTeamStore
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import java.io.BufferedReader
import java.util.*

class RentalStoreUtil
{
    companion object
    {
        @JvmStatic
        val instance: RentalStoreUtil = RentalStoreUtil()
    }

    fun initializePokemonStore(storeID: UUID?, name :String?): RentalPokemonStore
    {
        return RentalPokemonStore(storeID?: UUID.randomUUID(),name?:"Rental Pokemon Store")
    }

    fun initializeTeamStore(storeID: UUID?, name :String?): RentalTeamStore
    {
        return RentalTeamStore(storeID?: UUID.randomUUID(),name?:"Rental Team Store")
    }

    fun initializeFactory(rootFolder: String): FileBackedPokemonStoreFactory<CompoundTag>
    {
        return FileBackedPokemonStoreFactory(
                NBTStoreAdapter(rootFolder,useNestedFolders = true,folderPerClass = true),
                true)
    }

    fun registerFactory(priority: Priority, factory: FileBackedPokemonStoreFactory<CompoundTag>)
    {
        Cobblemon.storage.registerFactory(priority,factory)
    }

    fun getJSON(gson: Gson,br: BufferedReader):JsonObject?
    {
        try {
            val json = gson.fromJson<JsonObject>(br)
            br.close()
            return json
        } catch (e: Exception){
            return null
        }
    }
}