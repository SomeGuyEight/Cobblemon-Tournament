package com.cobblemontournament.common.config

import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.tournament.TournamentType

val DEFAULT_TOURNAMENT_TYPE = TournamentType.SINGLE_ELIMINATION
val DEFAULT_CHALLENGE_FORMAT = ChallengeFormat.STANDARD_6V6
const val DEFAULT_MAX_PARTICIPANTS = 32
const val DEFAULT_TEAM_SIZE = 1
const val DEFAULT_GROUP_SIZE = 4
const val DEFAULT_MIN_LEVEL = 50
const val DEFAULT_MAX_LEVEL = 50
const val DEFAULT_SHOW_PREVIEW = true
// TODO set save interval back to 30 when done testing storage
const val DEFAULT_SAVE_INTERVAL_SECONDS = 10
const val DEFAULT_BUILDER_PERMISSION = true
const val DEFAULT_BUILDER_INFO_PERMISSION = true
const val DEFAULT_BUILDER_EDIT_PERMISSION = true
const val DEFAULT_GENERATE_TOURNAMENT_PERMISSION = true
const val DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION = true
