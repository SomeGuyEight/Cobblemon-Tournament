package com.cobblemontournament.common.util

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.tournament.TournamentType
import java.util.UUID

object TournamentUtil
{
//    private val errorFormated               = formatEnum( TournamentStatus.ERROR.name )
//    private val unknownFormated             = formatEnum( TournamentStatus.UNKNOWN.name )
//    private val notReadyFormated            = formatEnum( TournamentStatus.NOT_READY.name )
//    private val pendingFormated             = formatEnum( TournamentStatus.PENDING.name )
//    private val readyFormated               = formatEnum( TournamentStatus.READY.name )
//    private val completeFormated            = formatEnum( TournamentStatus.COMPLETE.name )
//    private val inProgressFormated          = formatEnum( TournamentStatus.IN_PROGRESS.name )
//    private val finalizedFormated           = formatEnum( TournamentStatus.FINALIZED.name )
//
//    private val singleEliminationFormated   = formatEnum( TournamentType.SINGLE_ELIMINATION.name )
//    private val doubleEliminationFormated   = formatEnum( TournamentType.DOUBLE_ELIMINATION.name )
//    private val roundRobinFormated          = formatEnum( TournamentType.ROUND_ROBIN.name )
//    private val vgcFormated                 = formatEnum( TournamentType.VGC.name )
//
//    private val standard6v6Formated         = formatEnum( ChallengeFormat.STANDARD_6V6.name )
//
//    private val playerFormated              = formatEnum( ActorType.PLAYER.name )
//    private val npcFormated                 = formatEnum( ActorType.NPC.name )
//    private val wildFormated                = formatEnum( ActorType.WILD.name )

    private val tournamentStatusMap = createEnumMap( TournamentStatus::class.java )
    private val tournamentTypeMap   = createEnumMap( TournamentType::class.java )
    private val challengeFormatMap  = createEnumMap( ChallengeFormat::class.java )
    private val actorTypeMap        = createEnumMap( ActorType::class.java )

    private fun <E: Enum<E>> createEnumMap(
        enumClass : Class<E>
    ): Map<String,E>
    {
        val map = mutableMapOf <String,E>()
        for (constant in enumClass.enumConstants) {
            map[formatEnum(constant.name)] = constant
        }
        return map
    }

    /** [value] .[filterNot] `{ ' ' || '_' || '-' }` .[lowercase] */
    private fun formatEnum(
        value: String
    ): String {
        return value.filterNot { it == ' ' || it == '_' || it == '-' }.lowercase()
    }


    /** Compares [value] .[formatEnum] to all [TournamentStatus] .entries .[formatEnum]
     *
     * if `match found` -> returns `TournamentStatus`
     *
     * if `no match found` -> returns `null`
     */
    fun getTournamentStatusOrNull( value: String ) = tournamentStatusMap[ formatEnum( value ) ]

    /** Compares [value] .[formatEnum] to all [TournamentType] .entries .[formatEnum]
     *
     * if `match found` -> returns `TournamentType`
     *
     * if `no match found` -> returns `null`
     */
    fun getTournamentTypeOrNull( value: String ) = tournamentTypeMap[ formatEnum( value ) ]
//    fun getTournamentTypeOrNull(
//        value: String
//    ): TournamentType?
//    {
//        return when ( formatEnum( value ) ) {
//            singleEliminationFormated   -> TournamentType.SINGLE_ELIMINATION
//            doubleEliminationFormated   -> TournamentType.DOUBLE_ELIMINATION
//            roundRobinFormated          -> TournamentType.ROUND_ROBIN
//            vgcFormated                 -> TournamentType.VGC
//            else                        -> null
//        }
//    }


    /** Compares [value] .[formatEnum] to all [ChallengeFormat] .entries .[formatEnum]
     *
     * if `match found` -> returns `ChallengeFormat`
     *
     * if `no match found` -> returns `null`
     */
    fun getChallengeFormatOrNull( value: String ) = challengeFormatMap[ formatEnum( value ) ]

//    fun getChallengeFormatOrNull(
//        value: String
//    ): ChallengeFormat?
//    {
//        return when ( formatEnum( value ) ) {
//            standard6v6Formated     -> ChallengeFormat.STANDARD_6V6
//            else                    -> null
//        }
//    }

    /** Compares [value] .[formatEnum] to all [ActorType] .entries .[formatEnum]
     *
     * if `match found` -> returns `ActorType`
     *
     * if `no match found` -> returns `null`
     */
    fun getActorTypeOrNull( value: String ) = actorTypeMap[ formatEnum( value ) ]
//    fun getActorTypeOrNull(
//        value: String
//    ): ActorType?
//    {
//        return when ( formatEnum( value) ) {
//            playerFormated          -> ActorType.PLAYER
//            npcFormated             -> ActorType.NPC
//            wildFormated            -> ActorType.WILD
//            else                    -> null
//        }
//    }

    fun shallowRoundsCopy(
        matchMap: Map <UUID,TournamentRound>
    ): MutableMap <UUID,TournamentRound> {
        val copy = mutableMapOf <UUID,TournamentRound>()
        matchMap.forEach { copy[it.key] = it.value }
        return copy
    }

    fun shallowMatchesCopy(
        matchMap: Map <UUID,TournamentMatch>
    ): MutableMap <UUID,TournamentMatch> {
        val copy = mutableMapOf <UUID,TournamentMatch>()
        matchMap.forEach { copy[it.key] = it.value }
        return copy
    }

    fun shallowPlayersCopy(
        playerMap: Map <UUID,TournamentPlayer>
    ): MutableMap <UUID,TournamentPlayer> {
        val copy = mutableMapOf <UUID,TournamentPlayer>()
        playerMap.forEach { copy[it.key] = it.value }
        return copy
    }

    fun previousMatchIndices(
        roundMatchIndex : Int,
        roundIndex      : Int
    ): Pair <Int?,Int?> {
        return if ( roundIndex > 0 ) {
            Pair( (roundMatchIndex * 2), (roundMatchIndex * 2) + 1 )
        } else Pair( null, null )
    }

    fun victorNextMatchIndex(
        roundMatchIndex : Int,
        roundIndex      : Int,
        roundCount      : Int
    ): Int? {
        return if ( ( roundIndex + 1 ) < roundCount ) {
            roundMatchIndex shr 1
        } else null
    }

    fun defeatedNextMatchIndex(
        tournamentType: TournamentType
    ): Int? {
        return when ( tournamentType ) {
            TournamentType.SINGLE_ELIMINATION   -> null
            TournamentType.DOUBLE_ELIMINATION   -> TODO("Need to implement when DOUBLE_ELIMINATION added")
            TournamentType.ROUND_ROBIN          -> TODO("Need to implement when ROUND_ROBIN added")
            TournamentType.VGC                  -> TODO("Need to implement when VGC added")
        }
    }

    fun <K,V> copy(
        map: Map<K,V>
    ): MutableMap <K,V> {
        val copy = mutableMapOf <K,V>()
        map.forEach { copy[it.key] = it.value }
        return copy
    }
}
