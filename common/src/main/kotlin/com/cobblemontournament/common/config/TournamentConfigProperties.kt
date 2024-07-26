package com.cobblemontournament.common.config

import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.tournament.TournamentType

data class TournamentConfigProperties(
    var defaultTournamentType: TournamentType = DEFAULT_TOURNAMENT_TYPE,
    var defaultChallengeFormat: ChallengeFormat = DEFAULT_CHALLENGE_FORMAT,
    var defaultMaxParticipants: Int = DEFAULT_MAX_PARTICIPANTS,
    var defaultTeamSize: Int = DEFAULT_TEAM_SIZE,
    var defaultGroupSize: Int = DEFAULT_GROUP_SIZE,
    var defaultMinLevel: Int = DEFAULT_MIN_LEVEL,
    var defaultMaxLevel: Int = DEFAULT_MAX_LEVEL,
    var defaultShowPreview: Boolean = DEFAULT_SHOW_PREVIEW,
    var saveIntervalSeconds: Int = DEFAULT_SAVE_INTERVAL_SECONDS,
    var defaultBuilderPermission: Boolean = DEFAULT_BUILDER_PERMISSION,
    var defaultBuilderInfoPermission: Boolean = DEFAULT_BUILDER_INFO_PERMISSION,
    var defaultBuilderEditPermission: Boolean = DEFAULT_BUILDER_EDIT_PERMISSION,
    var defaultGenerateTournamentPermission: Boolean = DEFAULT_GENERATE_TOURNAMENT_PERMISSION,
    var defaultForceMatchCompletionPermission: Boolean = DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION,
)
