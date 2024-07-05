package com.cobblemontournament.common.tournamentbuilder.properties

import com.cobblemontournament.common.player.properties.MutablePlayerProperties
import com.cobblemontournament.common.tournament.properties.MutableTournamentProperties
import com.someguy.storage.properties.PropertyFields
import org.slf4j.helpers.Util
import java.util.UUID

interface TournamentBuilderPropertyFields : PropertyFields
{
    val name                    : String
    val tournamentBuilderID     : UUID
    val tournamentProperties    : MutableTournamentProperties
    val seededPlayers           : MutableSet<MutablePlayerProperties>
    val unseededPlayers         : MutableSet<MutablePlayerProperties>

    override fun printProperties()
    {
        Util.report("Tournament Builder Details - ID: $tournamentBuilderID")
        Util.report("- Name: $name")
        Util.report("- Tournament Properties:")
        tournamentProperties.printProperties()
        Util.report("- Seeded Players:")
        seededPlayers.forEach { it.printProperties() }
        Util.report("- Unseeded Players:")
        unseededPlayers.forEach { it.printProperties() }
    }

}
