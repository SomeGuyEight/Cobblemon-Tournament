package com.cobblemontournament.common.config

import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.tournament.TournamentType

data class TournamentConfigProperties(
    var defaultTournamentType                   : TournamentType    = TournamentConfig.DEFAULT_TOURNAMENT_TYPE,
    var defaultChallengeFormat                  : ChallengeFormat   = TournamentConfig.DEFAULT_CHALLENGE_FORMAT,
    var defaultMaxParticipants                  : Int               = TournamentConfig.DEFAULT_MAX_PARTICIPANTS,
    var defaultTeamSize                         : Int               = TournamentConfig.DEFAULT_TEAM_SIZE,
    var defaultGroupSize                        : Int               = TournamentConfig.DEFAULT_GROUP_SIZE,
    var defaultMinLevel                         : Int               = TournamentConfig.DEFAULT_MIN_LEVEL,
    var defaultMaxLevel                         : Int               = TournamentConfig.DEFAULT_MAX_LEVEL,
    var defaultShowPreview                      : Boolean           = TournamentConfig.DEFAULT_SHOW_PREVIEW,
    var saveIntervalSeconds                     : Int               = TournamentConfig.DEFAULT_SAVE_INTERVAL_SECONDS,
    var defaultBuilderPermission                : Boolean           = TournamentConfig.DEFAULT_BUILDER_PERMISSION,
    var defaultBuilderInfoPermission            : Boolean           = TournamentConfig.DEFAULT_BUILDER_INFO_PERMISSION,
    var defaultBuilderEditPermission            : Boolean           = TournamentConfig.DEFAULT_BUILDER_EDIT_PERMISSION,
    var defaultGenerateTournamentPermission     : Boolean           = TournamentConfig.DEFAULT_GENERATE_TOURNAMENT_PERMISSION,
    var defaultForceMatchCompletionPermission   : Boolean           = TournamentConfig.DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION )
{
    /* TODO FUTURE implement better configs with description, valid values, etc
    var defaultTournamentType = TournamentTypeConfigEntry(
        name        = "Default Tournament Type",
        description = "The Tournament Type used to format the generated tournament.",
        default     = DEFAULT_TOURNAMENT_TYPE,
        validValues = TournamentType.entries,
        range       = null,
        current     = DEFAULT_TOURNAMENT_TYPE )

    var defaultChallengeFormat = ChallengeFormatConfigEntry(
        name        = "Default Challenge Format",
        description = "The battle format to be used in the tournament challenges.",
        default     = DEFAULT_CHALLENGE_FORMAT,
        validValues = ChallengeFormat.entries,
        range       = null,
        current     = DEFAULT_CHALLENGE_FORMAT )

    var defaultMaxParticipants = IntConfigEntry(
        name        = "Default Max Participants",
        description = "The maximum quantity of players permitted to register.",
        default     = DEFAULT_MAX_PARTICIPANTS,
        validValues = null,
        range       = Pair( 2, Int.MAX_VALUE ),
        current     = DEFAULT_MAX_PARTICIPANTS )

    var defaultTeamSize = IntConfigEntry(
        name        = "Default Team Size",
        description = "The maximum number of players per team.",
        default     = DEFAULT_TEAM_SIZE,
        validValues = null,
        range       = Pair( 1, Int.MAX_VALUE ),
        current     = DEFAULT_TEAM_SIZE )

    var defaultGroupSize = IntConfigEntry(
        name        = "Default Group Size",
        description = "The number of players per group. Examples: Round Robin Tournament Groups or groups for the first phase of a VGC Tournament.",
        default     = DEFAULT_GROUP_SIZE,
        validValues = null,
        range       = Pair( 2, Int.MAX_VALUE ),
        current     = DEFAULT_GROUP_SIZE )
     */
}
