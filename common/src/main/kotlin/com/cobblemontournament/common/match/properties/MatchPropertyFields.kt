package com.cobblemontournament.common.match.properties

import com.cobblemontournament.common.match.MatchStatus
import com.someguy.storage.properties.PropertyFields
import org.slf4j.helpers.Util
import java.util.UUID

interface MatchPropertyFields : PropertyFields
{
    val matchID                 : UUID
    val tournamentID            : UUID
    val roundID                 : UUID
    val tournamentMatchIndex    : Int
    val roundMatchIndex         : Int
    val matchStatus             : MatchStatus
    val victorID                : UUID?
    val playerMap               : Map<UUID,Int>

    override fun printProperties()
    {
        Util.report("Match Details - ID: $matchID")
        Util.report("- Tournament ID: $tournamentID")
        Util.report("- Round ID: $roundID")
        Util.report("- Tournament Match Index: $tournamentMatchIndex")
        Util.report("- Round Match Index: $roundMatchIndex")
        Util.report("- Victor: $victorID")
        Util.report("- Status: $matchStatus")

        val sorted = mutableListOf<Pair<UUID,Int>>()
        playerMap.forEach {
            sorted.add(Pair(it.key, it.value))
        }
        sorted.sortedBy { it.second }
        sorted.forEach {
            Util.report("- Team: ${it.second} - Player: ${it.first}")
        }
    }

}