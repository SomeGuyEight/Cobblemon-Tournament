package com.cobblemontournament.common.config

import com.cobblemontournament.common.tournament.TournamentType
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat

@Suppress("MemberVisibilityCanBePrivate","MayBeConstant")
data object Config
{
    const val CONFIG_FILE_NAME                  = "cobblemon-tournament"
    const val TOURNAMENT_TYPE_KEY 	            = "tournament_type"
    const val CHALLENGE_FORMAT_KEY              = "challenge_format"
    const val MAX_PARTICIPANTS_KEY              = "max_participants"
    const val TEAM_SIZE_KEY                     = "team_size"
    const val GROUP_SIZE_KEY 		            = "group_size"
    const val MIN_LEVEL_KEY                     = "min_level"
    const val MAX_LEVEL_KEY                     = "max_level"
    const val SHOW_PREVIEW_KEY                  = "show_preview"
    const val SAVE_INTERVAL_SECONDS_KEY         = "save_interval_seconds"

    private val DEFAULT_TOURNAMENT_TYPE         = TournamentType.SINGLE_ELIMINATION
    private val DEFAULT_CHALLENGE_FORMAT        = ChallengeFormat.STANDARD_6V6
    private val DEFAULT_MAX_PARTICIPANTS        = 32
    private val DEFAULT_TEAM_SIZE 		        = 1
    private val DEFAULT_GROUP_SIZE 		        = 4
    private val DEFAULT_MIN_LEVEL 		        = 50
    private val DEFAULT_MAX_LEVEL 		        = 50
    private val DEFAULT_SHOW_PREVIEW 	        = true
    private val DEFAULT_SAVE_INTERVAL_SECONDS   = 30

    private var defaultTournamentType   : TournamentType = DEFAULT_TOURNAMENT_TYPE
    private var defaultChallengeFormat  : ChallengeFormat   = DEFAULT_CHALLENGE_FORMAT
    private var defaultMaxParticipants  : Int               = DEFAULT_MAX_PARTICIPANTS
    private var defaultTeamSize         : Int               = DEFAULT_TEAM_SIZE
    private var defaultGroupSize        : Int               = DEFAULT_GROUP_SIZE
    private var defaultMinLevel         : Int               = DEFAULT_MIN_LEVEL
    private var defaultMaxLevel         : Int               = DEFAULT_MAX_LEVEL
    private var defaultShowPreview      : Boolean           = DEFAULT_SHOW_PREVIEW
    private var saveIntervalSeconds     : Int               = DEFAULT_SAVE_INTERVAL_SECONDS

    fun defaultTournamentType()     : TournamentType = defaultTournamentType
    fun defaultChallengeFormat()    : ChallengeFormat   = defaultChallengeFormat
    fun defaultMaxParticipants()    : Int               = defaultMaxParticipants
    fun defaultTeamSize()           : Int               = defaultTeamSize
    fun defaultGroupSize()          : Int               = defaultGroupSize
    fun defaultMinLevel()           : Int               = defaultMinLevel
    fun defaultMaxLevel()           : Int               = defaultMaxLevel
    fun defaultShowPreview()        : Boolean           = defaultShowPreview
    fun saveIntervalSeconds()       : Int               = saveIntervalSeconds

    @JvmStatic
    fun initialize(
        defaultTournamentType   : TournamentType?,
        defaultChallengeFormat  : ChallengeFormat?,
        defaultMaxParticipants  : Int?,
        defaultTeamSize         : Int?,
        defaultGroupSize        : Int?,
        defaultMinLevel         : Int?,
        defaultMaxLevel         : Int?,
        defaultShowPreview      : Boolean?,
        saveIntervalSeconds     : Int?)
    {
        this.defaultTournamentType  = defaultTournamentType     ?: DEFAULT_TOURNAMENT_TYPE
        this.defaultChallengeFormat = defaultChallengeFormat    ?: DEFAULT_CHALLENGE_FORMAT
        this.defaultMaxParticipants = defaultMaxParticipants    ?: DEFAULT_MAX_PARTICIPANTS
        this.defaultTeamSize        = defaultTeamSize           ?: DEFAULT_TEAM_SIZE
        this.defaultGroupSize       = defaultGroupSize          ?: DEFAULT_GROUP_SIZE
        this.defaultMinLevel        = defaultMinLevel           ?: DEFAULT_MIN_LEVEL
        this.defaultMaxLevel        = defaultMaxLevel           ?: DEFAULT_MAX_LEVEL
        this.defaultShowPreview     = defaultShowPreview        ?: DEFAULT_SHOW_PREVIEW
        this.saveIntervalSeconds    = saveIntervalSeconds       ?: DEFAULT_SAVE_INTERVAL_SECONDS
    }

}