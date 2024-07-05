package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.deepCopy
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.loadPlayersFromNBT
import com.cobblemontournament.common.player.properties.PlayerPropertiesHelper.savePlayersToNBT
import com.cobblemontournament.common.tournament.properties.MutableTournamentProperties
import com.cobblemontournament.common.util.TournamentDataKeys
import com.someguy.storage.properties.PropertiesHelper
import net.minecraft.nbt.CompoundTag

object TournamentBuilderPropertiesHelper: PropertiesHelper<TournamentBuilderPropertyFields, TournamentBuilderProperties, MutableTournamentBuilderProperties>
{
    const val DEFAULT_TOURNAMENT_BUILDER_NAME = "Tournament Builder"

    override fun deepCopyHelper(
        properties : TournamentBuilderPropertyFields
    ): TournamentBuilderProperties
    {
        return TournamentBuilderProperties(
            name                    = properties.name,
            tournamentBuilderID     = properties.tournamentBuilderID,
            tournamentProperties    = properties.tournamentProperties.deepMutableCopy(),
            seededPlayers           = deepCopy(properties.seededPlayers),
            unseededPlayers         = deepCopy(properties.seededPlayers))
    }

    override fun deepMutableCopyHelper(
        properties : TournamentBuilderPropertyFields
    ): MutableTournamentBuilderProperties
    {
        return MutableTournamentBuilderProperties(
            name                    = properties.name,
            tournamentBuilderID     = properties.tournamentBuilderID,
            tournamentProperties    = properties.tournamentProperties.deepMutableCopy(),
            seededPlayers           = deepCopy(properties.seededPlayers),
            unseededPlayers         = deepCopy(properties.seededPlayers))
    }

    override fun setFromPropertiesHelper(
        mutable: MutableTournamentBuilderProperties,
        from: TournamentBuilderPropertyFields
    ): MutableTournamentBuilderProperties
    {
        mutable.name                  = from.name
        mutable.tournamentBuilderID   = from.tournamentBuilderID
        mutable.tournamentProperties  = from.tournamentProperties.deepMutableCopy()
        mutable.seededPlayers         = deepCopy(from.seededPlayers)
        mutable.unseededPlayers       = deepCopy(from.unseededPlayers)
        return mutable
    }

    override fun setFromNBTHelper(
        mutable: MutableTournamentBuilderProperties,
        nbt: CompoundTag,
    ): MutableTournamentBuilderProperties
    {
        mutable.name                    = nbt.getString(        TournamentDataKeys.TOURNAMENT_BUILDER_NAME)
        mutable.tournamentBuilderID     = nbt.getUUID(          TournamentDataKeys.TOURNAMENT_BUILDER_ID)
        mutable.tournamentProperties    = getMutableTournamentProperties( nbt = nbt)
        mutable.seededPlayers           = loadPlayersFromNBT(   nbt.getCompound(TournamentDataKeys.SEEDED_PLAYERS))
        mutable.unseededPlayers         = loadPlayersFromNBT(   nbt.getCompound(TournamentDataKeys.UNSEEDED_PLAYERS))
        return mutable
    }

    override fun saveToNBTHelper(
        properties: TournamentBuilderPropertyFields,
        nbt: CompoundTag
    ): CompoundTag
    {
        nbt.putString(  TournamentDataKeys.TOURNAMENT_BUILDER_NAME  , properties.name)
        nbt.putUUID(    TournamentDataKeys.TOURNAMENT_BUILDER_ID    , properties.tournamentBuilderID)
        nbt.put( TournamentDataKeys.TOURNAMENT_PROPERTIES, properties.tournamentProperties.saveToNBT(CompoundTag()))
        nbt.put( TournamentDataKeys.SEEDED_PLAYERS, savePlayersToNBT( properties.seededPlayers,CompoundTag()))
        nbt.put( TournamentDataKeys.UNSEEDED_PLAYERS, savePlayersToNBT( properties.unseededPlayers,CompoundTag()))
        return nbt
    }

    override fun loadFromNBT(
        nbt: CompoundTag
    ): TournamentBuilderProperties
    {
        return TournamentBuilderProperties(
            name                    = nbt.getString(        TournamentDataKeys.TOURNAMENT_BUILDER_NAME),
            tournamentBuilderID     = nbt.getUUID(          TournamentDataKeys.TOURNAMENT_BUILDER_ID),
            tournamentProperties    = getMutableTournamentProperties( nbt = nbt),
            seededPlayers           = loadPlayersFromNBT(   nbt.getCompound(TournamentDataKeys.SEEDED_PLAYERS)),
            unseededPlayers         = loadPlayersFromNBT(   nbt.getCompound(TournamentDataKeys.UNSEEDED_PLAYERS)),
        )
    }

    override fun loadMutableFromNBT(
        nbt: CompoundTag,
    ): MutableTournamentBuilderProperties
    {
        return MutableTournamentBuilderProperties(
            name                    = nbt.getString(        TournamentDataKeys.TOURNAMENT_BUILDER_NAME),
            tournamentBuilderID     = nbt.getUUID(          TournamentDataKeys.TOURNAMENT_BUILDER_ID),
            tournamentProperties    = getMutableTournamentProperties( nbt = nbt),
            seededPlayers           = loadPlayersFromNBT(   nbt.getCompound( TournamentDataKeys.SEEDED_PLAYERS)),
            unseededPlayers         = loadPlayersFromNBT(   nbt.getCompound( TournamentDataKeys.UNSEEDED_PLAYERS))
        )
    }


    // private function to improve readability above

    private fun getMutableTournamentProperties(
        nbt: CompoundTag
    ): MutableTournamentProperties {
        return MutableTournamentProperties.loadMutableFromNBT( nbt.getCompound( TournamentDataKeys.TOURNAMENT_PROPERTIES))
    }

}