package com.cobblemontournament.common.config

import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.tournament.TournamentType

data class TournamentConfigProperties(
    var defaultTournamentType: TournamentType = TournamentConfig.DEFAULT_TOURNAMENT_TYPE,
    var defaultChallengeFormat: ChallengeFormat = TournamentConfig.DEFAULT_CHALLENGE_FORMAT,
    var defaultMaxParticipants: Int = TournamentConfig.DEFAULT_MAX_PARTICIPANTS,
    var defaultTeamSize: Int = TournamentConfig.DEFAULT_TEAM_SIZE,
    var defaultGroupSize: Int = TournamentConfig.DEFAULT_GROUP_SIZE,
    var defaultMinLevel: Int = TournamentConfig.DEFAULT_MIN_LEVEL,
    var defaultMaxLevel: Int = TournamentConfig.DEFAULT_MAX_LEVEL,
    var defaultShowPreview: Boolean = TournamentConfig.DEFAULT_SHOW_PREVIEW,
    var saveIntervalSeconds: Int = TournamentConfig.DEFAULT_SAVE_INTERVAL_SECONDS,
    var defaultBuilderPermission: Boolean = TournamentConfig.DEFAULT_BUILDER_PERMISSION,
    var defaultBuilderInfoPermission: Boolean = TournamentConfig.DEFAULT_BUILDER_INFO_PERMISSION,
    var defaultBuilderEditPermission: Boolean = TournamentConfig.DEFAULT_BUILDER_EDIT_PERMISSION,
    var defaultGenerateTournamentPermission: Boolean = TournamentConfig.DEFAULT_GENERATE_TOURNAMENT_PERMISSION,
    var defaultForceMatchCompletionPermission: Boolean = TournamentConfig.DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION,
) {
    /* TODO FUTURE implement better configs with description, valid values, etc */
}
