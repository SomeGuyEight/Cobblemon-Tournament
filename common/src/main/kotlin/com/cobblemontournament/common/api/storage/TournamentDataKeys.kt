package com.cobblemontournament.common.api.storage

object TournamentDataKeys {

    const val ACTIVE_STORE_KEY = "active_store_key"
    const val INACTIVE_STORE_KEY = "inactive_store_key"

    private const val BUILDER_ = "tournament_builder_"
    private const val TOURNAMENT_ = "tournament_"
    private const val ROUND_ = "round_"
    private const val MATCH_ = "match_"
    private const val PLAYER_ = "player_"

    private const val DATA = "data"
//    const val TOURNAMENT_BUILDER_DATA_KEY = "$BUILDER_$DATA"
//    const val TOURNAMENT_DATA_KEY = "$TOURNAMENT_$DATA"
    const val ROUND_DATA_KEY = "$ROUND_$DATA"
//    const val MATCH_DATA_KEY = "$MATCH_$DATA"
//    const val PLAYER_DATA_KEY = "$PLAYER_$DATA"

    private const val PROPERTIES = "properties"
    const val TOURNAMENT_BUILDER_PROPERTIES_KEY = "$BUILDER_$PROPERTIES"
    const val TOURNAMENT_PROPERTIES_KEY = "$TOURNAMENT_$PROPERTIES"
    const val ROUND_PROPERTIES_KEY = "$ROUND_$PROPERTIES"
    const val MATCH_PROPERTIES_KEY = "$MATCH_$PROPERTIES"
    const val PLAYER_PROPERTIES_KEY = "$PLAYER_$PROPERTIES"

    private const val ID = "id"
    const val TOURNAMENT_BUILDER_ID_KEY = "$BUILDER_$ID"
    const val TOURNAMENT_ID_KEY = "$TOURNAMENT_$ID"
    const val ROUND_ID_KEY = "$ROUND_$ID"
    const val MATCH_ID_KEY = "$MATCH_$ID"
    const val PLAYER_ID_KEY = "$PLAYER_$ID"

    private const val NAME = "name"
    const val TOURNAMENT_BUILDER_NAME_KEY = "$BUILDER_$NAME"
    const val TOURNAMENT_NAME_KEY = "$TOURNAMENT_$NAME"
//    const val ROUND_NAME_KEY = "$ROUND_$NAME"
//    const val MATCH_NAME_KEY = "$MATCH_$NAME"
    const val PLAYER_NAME_KEY = "$PLAYER_$NAME"

    private const val STATUS = "status"
    const val TOURNAMENT_STATUS_KEY = "$TOURNAMENT_$STATUS"
    const val MATCH_STATUS_KEY = "$MATCH_$STATUS"

    private const val TYPE = "type"
    const val TOURNAMENT_TYPE_KEY = "$TOURNAMENT_$TYPE"
    const val ROUND_TYPE_KEY = "$ROUND_$TYPE"

    // tournament specific key constants
    const val GROUP_SIZE_KEY = "group_size"
    const val TEAM_SIZE_KEY = "team_size"
    const val MAX_PARTICIPANTS_KEY = "max_participants"
    const val CHALLENGE_FORMAT_KEY = "challenge_format"
    const val MIN_LEVEL_KEY = "min_level"
    const val MAX_LEVEL_KEY = "max_level"
    const val SHOW_PREVIEW_KEY = "show_preview"

    // round specific key constants
    const val ROUND_INDEX_KEY = "round_index"
    const val ROUND_MATCH_INDEX_TO_ID_KEY = "round_match_index_to_id"
    const val ROUND_MAP_KEY = "round_map"

    // match specific key constants
    const val TOURNAMENT_MATCH_INDEX_KEY = "tournament_match_index"
    const val ROUND_MATCH_INDEX_KEY = "round_match_index"
    const val MATCH_CONNECTIONS_KEY = "match_connections"
    const val VICTOR_NEXT_MATCH_KEY = "victor_next_match"
    const val DEFEATED_NEXT_MATCH_KEY = "defeated_next_match"
    const val VICTOR_ID_KEY = "victor_id"
    const val PLAYER_ID_TO_TEAM_INDEX_KEY = "player_id_to_team_index"
    const val TEAM_INDEX_KEY = "team_index"

    // player specific key constants
    const val PLAYER_SET_KEY = "player_set"
    const val ACTOR_TYPE_KEY = "actor_type"
    const val SEED_KEY = "seed"
    const val ORIGINAL_SEED_KEY = "original_seed"
    const val FINAL_PLACEMENT_KEY = "final_placement"
    const val CURRENT_MATCH_ID_KEY = "current_match_id"
    const val POKEMON_TEAM_ID_KEY = "pokemon_team_id"
    const val POKEMON_FINAL_KEY = "pokemon_final"
    const val LOCK_POKEMON_ON_SET_KEY = "lock_pokemon_on_set"
}
