package com.cobblemontournament.common.tournament.properties

import com.cobblemontournament.common.tournament.TournamentType
import com.someguy.storage.properties.PropertyFields
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat
import org.slf4j.helpers.Util
import java.util.UUID

interface TournamentPropertyFields : PropertyFields
{
    val name                : String
    var tournamentID        : UUID
    val tournamentType      : TournamentType
    val challengeFormat     : ChallengeFormat
    val maxParticipants     : Int
    val teamSize            : Int
    val groupSize           : Int
    val minLevel            : Int
    val maxLevel            : Int
    val showPreview         : Boolean
    val totalRounds         : Int
    val totalMatches        : Int
    val totalPlayers        : Int
    val players             : Map<UUID,String>

    override fun printProperties()
    {
        Util.report("Tournament Debug - ID: $tournamentID")
        Util.report("   Properties:")
        Util.report("     - Tournament Type: $tournamentType")
        Util.report("     - Challenge Format: $challengeFormat")
        Util.report("     - Max Participants: $maxParticipants")
        Util.report("     - Team Size: $teamSize")
        Util.report("     - Group Size: $groupSize")
        Util.report("     - Min Level: $minLevel")
        Util.report("     - Max Level: $maxLevel")
        Util.report("     - Show Preview: $showPreview")
        Util.report("   Details:")
        Util.report("     - Total Rounds: $totalRounds")
        Util.report("     - Total Matches: $totalMatches")
        Util.report("     - Total Players: $totalPlayers")
        Util.report("   Players:")
        players.forEach {
            Util.report("     - Name: ${it.value} [ID:${it.key}]")
        }

    }
}
