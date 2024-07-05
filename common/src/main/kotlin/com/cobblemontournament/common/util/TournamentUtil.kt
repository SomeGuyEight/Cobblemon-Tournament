package com.cobblemontournament.common.util

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.tournament.TournamentType
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat

object TournamentUtil
{
    private val singleEliminationFormated   = formatEnum(TournamentType.SINGLE_ELIMINATION.name)
    private val doubleEliminationFormated   = formatEnum(TournamentType.DOUBLE_ELIMINATION.name)
    private val roundRobinFormated          = formatEnum(TournamentType.ROUND_ROBIN.name)
    private val vgcFormated                 = formatEnum(TournamentType.VGC.name)
    private val standard6v6Formated         = formatEnum(ChallengeFormat.STANDARD_6V6.name)
    private val playerFormated              = formatEnum(ActorType.PLAYER.name)
    private val npcFormated                 = formatEnum(ActorType.NPC.name)
    private val wildFormated                = formatEnum(ActorType.WILD.name)

    /**
     * [value] .[filterNot] `{ ' ' || '_' || '-' }` .[lowercase]
     */
    private fun formatEnum(
        value: String
    ): String {
        return value.filterNot { it == ' ' || it == '_' || it == '-' }.lowercase()
    }

    /**
     * Compares [value] .[formatEnum] to all [TournamentType] .entries .[formatEnum]
     *
     * if `match found` -> returns `TournamentType`
     *
     * if `no match found` -> returns `null`
     */
    fun getTournamentTypeOrNull(
        value: String
    ): TournamentType?
    {
        return when (formatEnum(value)) {
            singleEliminationFormated   -> TournamentType.SINGLE_ELIMINATION
            doubleEliminationFormated   -> TournamentType.SINGLE_ELIMINATION
            roundRobinFormated          -> TournamentType.SINGLE_ELIMINATION
            vgcFormated                 -> TournamentType.SINGLE_ELIMINATION
            else                        -> null
        }
    }

    /**
     * Compares [value] .[formatEnum] to all [ChallengeFormat] .entries .[formatEnum]
     *
     * if `match found` -> returns `ChallengeFormat`
     *
     * if `no match found` -> returns `null`
     */
    fun getChallengeFormatOrNull(
        value: String
    ): ChallengeFormat?
    {
        return when (formatEnum(value)) {
            standard6v6Formated     -> ChallengeFormat.STANDARD_6V6
            else                    -> null
        }
    }

    /**
     * Compares [value] .[formatEnum] to all [ActorType] .entries .[formatEnum]
     *
     * if `match found` -> returns `ActorType`
     *
     * if `no match found` -> returns `null`
     */
    fun getActorTypeOrNull(
        value: String
    ): ActorType?
    {
        return when ( formatEnum( value)) {
            playerFormated          -> ActorType.PLAYER
            npcFormated             -> ActorType.NPC
            wildFormated            -> ActorType.WILD
            else                    -> null
        }
    }

}
