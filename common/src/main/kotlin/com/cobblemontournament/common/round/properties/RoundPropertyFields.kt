package com.cobblemontournament.common.round.properties

import com.cobblemontournament.common.round.RoundType
import com.someguy.storage.properties.PropertyFields
import org.slf4j.helpers.Util
import java.util.UUID

interface RoundPropertyFields : PropertyFields
{
    val roundID             : UUID
    val tournamentID        : UUID
    val roundIndex          : Int
    val roundType           : RoundType
    val indexedMatchMap     : Map<Int, UUID>

    override fun printProperties()
    {
        Util.report("Round Details - ID: $roundID")
        Util.report("- Tournament ID: $tournamentID")
        Util.report("- Round Type: $roundType")
        Util.report("- Round Index: $roundIndex")
        Util.report("- Match Count: ${indexedMatchMap.size}")
    }

}
