package com.cobblemontournament.common.util

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.tournament.TournamentStatus
import com.cobblemontournament.common.tournament.TournamentType

object TournamentUtil {

    private val tournamentStatusMap by lazy { createEnumMap(TournamentStatus::class.java) }
    private val tournamentTypeMap by lazy { createEnumMap(TournamentType::class.java) }
    private val challengeFormatMap by lazy { createEnumMap(ChallengeFormat::class.java) }
    private val actorTypeMap by lazy { createEnumMap(ActorType::class.java) }

    private fun <E : Enum<E>> createEnumMap(enumClass: Class<E>): Map<String, E> {
        val map = mutableMapOf<String,E>()
        for (constant in enumClass.enumConstants) {
            map[(formatEnum(constant.name))] = constant
        }
        return map
    }

    /** [value] .[filterNot] `{ ' ' || '_' || '-' }` .[lowercase] */
    private fun formatEnum(value: String): String {
        return value
            .filterNot { it == ' ' || it == '_' || it == '-' }
            .lowercase()
    }

    fun getTournamentStatusOrNull(value: String?): TournamentStatus? =
        if (value != null) tournamentStatusMap[(formatEnum(value))] else null

    fun getTournamentTypeOrNull(value: String?): TournamentType? =
        if (value != null) tournamentTypeMap[(formatEnum(value))] else null

    fun getChallengeFormatOrNull(value: String?): ChallengeFormat? =
        if (value != null) challengeFormatMap[(formatEnum(value))] else null

    fun getActorTypeOrNull(value: String?): ActorType? =
        if (value != null) actorTypeMap[(formatEnum(value))] else null

    fun previousMatchIndices(roundMatchIndex: Int, roundIndex: Int): Pair<Int?, Int?> {
        return if (roundIndex > 0) {
            ((roundMatchIndex * 2) to ((roundMatchIndex * 2) + 1))
        } else {
            (null to null)
        }
    }

    fun victorNextMatchIndex(roundMatchIndex: Int, roundIndex: Int, roundCount: Int): Int? =
        if ((roundIndex + 1) < roundCount) (roundMatchIndex shr 1) else null

    fun defeatedNextMatchIndex(tournamentType: TournamentType): Int? {
        return when (tournamentType) {
            TournamentType.SINGLE_ELIMINATION -> null
            TournamentType.DOUBLE_ELIMINATION -> {
                TODO("Implement when DOUBLE_ELIMINATION functionality added")
            }
            TournamentType.ROUND_ROBIN -> {
                TODO("Implement when ROUND_ROBIN functionality added")
            }
            TournamentType.VGC -> TODO("Implement when VGC functionality added")
        }
    }

    fun <K, V> shallowCopy(map: Map<K, V>): MutableMap<K, V> {
        val copy = mutableMapOf<K, V>()
        map.forEach { copy[it.key] = it.value }
        return copy
    }

}
