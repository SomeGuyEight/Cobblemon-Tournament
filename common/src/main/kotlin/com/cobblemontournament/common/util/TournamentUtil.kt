package com.cobblemontournament.common.util

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.tournament.TournamentType

object TournamentUtil
{
    private val tournamentStatusMap = lazy { createEnumMap( TournamentStatus::class.java ) }
    private val tournamentTypeMap   = lazy { createEnumMap( TournamentType::class.java ) }
    private val challengeFormatMap  = lazy { createEnumMap( ChallengeFormat::class.java ) }
    private val actorTypeMap        = lazy { createEnumMap( ActorType::class.java ) }

    private fun <E: Enum<E>> createEnumMap(
        enumClass: Class<E>
    ): Map <String,E>
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
    fun getTournamentStatusOrNull(
        value: String?
    ): TournamentStatus? {
        return if ( value != null ) {
            tournamentStatusMap.value[ formatEnum( value ) ]
        } else null
    }

    /** Compares [value] .[formatEnum] to all [TournamentType] .entries .[formatEnum]
     *
     * if `match found` -> returns `TournamentType`
     *
     * if `no match found` -> returns `null`
     */
    fun getTournamentTypeOrNull(
        value: String?
    ): TournamentType? {
        return if ( value != null ) {
            tournamentTypeMap.value[ formatEnum( value ) ]
        } else null
    }

    /** Compares [value] .[formatEnum] to all [ChallengeFormat] .entries .[formatEnum]
     *
     * if `match found` -> returns `ChallengeFormat`
     *
     * if `no match found` -> returns `null`
     */
    fun getChallengeFormatOrNull(
        value: String?
    ): ChallengeFormat? {
        return if ( value != null ) {
            challengeFormatMap.value[ formatEnum( value ) ]
        } else null
    }

    /** Compares [value] .[formatEnum] to all [ActorType] .entries .[formatEnum]
     *
     * if `match found` -> returns `ActorType`
     *
     * if `no match found` -> returns `null`
     */
    fun getActorTypeOrNull(
        value: String?
    ): ActorType? {
        return if ( value != null ) {
            actorTypeMap.value[ formatEnum( value ) ]
        } else null
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
            TournamentType.DOUBLE_ELIMINATION   -> TODO("Implement when DOUBLE_ELIMINATION functionality added")
            TournamentType.ROUND_ROBIN          -> TODO("Implement when ROUND_ROBIN functionality added")
            TournamentType.VGC                  -> TODO("Implement when VGC functionality added")
        }
    }

    fun <K,V> shallowCopy(
        map: Map<K,V>
    ): MutableMap <K,V> {
        val copy = mutableMapOf <K,V>()
        map.forEach { copy[it.key] = it.value }
        return copy
    }
}
