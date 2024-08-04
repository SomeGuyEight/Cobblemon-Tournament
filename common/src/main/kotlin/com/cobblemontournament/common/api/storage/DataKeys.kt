package com.cobblemontournament.common.api.storage

import com.sg8.storage.DataKeys as StoreDataKeys


object DataKeys {

    const val NULL = StoreDataKeys.NULL
    const val NAME = StoreDataKeys.NAME

    const val SET = StoreDataKeys.SET
    const val LIST = StoreDataKeys.LIST
    const val MAP = StoreDataKeys.MAP
    const val SIZE = StoreDataKeys.SIZE
    const val ELEMENT = StoreDataKeys.ELEMENT
    const val ENTRY = StoreDataKeys.ENTRY
    const val KEY = StoreDataKeys.KEY
    const val VALUE = StoreDataKeys.VALUE
    const val PAIR = StoreDataKeys.PAIR
    const val TRIPLE = StoreDataKeys.TRIPLE
    const val FIRST = StoreDataKeys.FIRST
    const val SECOND = StoreDataKeys.SECOND
    const val THIRD = StoreDataKeys.THIRD

    const val UUID = StoreDataKeys.UUID

    const val STORE_DATA = StoreDataKeys.STORE_DATA
    const val STORE_ID = StoreDataKeys.STORE_ID
    const val INSTANCE_ID = StoreDataKeys.INSTANCE_ID


    const val ACTIVE_STORE = "active_store_key"
    const val INACTIVE_STORE = "inactive_store_key"

    const val ROUND_DATA = "round_data"

    const val TOURNAMENT_BUILDER_PROPERTIES = "tournament_builder_properties"
    const val TOURNAMENT_PROPERTIES = "tournament_properties"
    const val ROUND_PROPERTIES = "round_properties"
    const val MATCH_PROPERTIES = "match_properties"
    const val PLAYER_PROPERTIES = "player_properties"

    const val TOURNAMENT_BUILDER_ID = "tournament_builder_id"
    const val TOURNAMENT_ID = "tournament_id"
    const val ROUND_ID = "round_id"
    const val MATCH_ID = "match_id"
    const val PLAYER_ID = "player_id"

    const val TOURNAMENT_BUILDER_NAME = "tournament_builder_name"
    const val TOURNAMENT_NAME = "tournament_name"
    const val PLAYER_NAME = "player_name"

    // tournament-specific key constants
    const val TOURNAMENT_TYPE = "tournament_type"
    const val TOURNAMENT_STATUS = "tournament_status"
    const val GROUP_SIZE = "group_size"
    const val TEAM_SIZE = "team_size"
    const val MAX_PARTICIPANTS = "max_participants"
    const val CHALLENGE_FORMAT = "challenge_format"
    const val MIN_LEVEL = "min_level"
    const val MAX_LEVEL = "max_level"
    const val SHOW_PREVIEW = "show_preview"

    // round specific key constants
    const val ROUND_TYPE = "round_type"
    const val ROUND_INDEX = "round_index"
    const val ROUND_MATCH_INDEX_TO_ID = "round_match_index_to_id"
    const val ROUND_MAP = "round_map"

    // match specific key constants
    const val MATCH_STATUS = "match_status"
    const val TOURNAMENT_MATCH_INDEX = "tournament_match_index"
    const val ROUND_MATCH_INDEX = "round_match_index"
    const val MATCH_CONNECTIONS = "match_connections"
    const val MATCH_CONNECTIONS_PROPERTIES = "match_connections_properties"
    const val VICTOR_NEXT_MATCH = "victor_next_match"
    const val DEFEATED_NEXT_MATCH = "defeated_next_match"
    const val PREVIOUS_MATCH_MAP = "previous_match_map"
    const val VICTOR_ID = "victor_id"
    const val PLAYER_TEAM_MAP = "player_team_map"
    const val TEAM_INDEX = "team_index"

    // player-specific key constants
    const val PLAYER_SET = "player_set"
    const val ACTOR_TYPE = "actor_type"
    const val SEED = "seed"
    const val ORIGINAL_SEED = "original_seed"
    const val FINAL_PLACEMENT = "final_placement"
    const val CURRENT_MATCH_ID = "current_match_id"
    const val POKEMON_TEAM_ID = "pokemon_team_id"
    const val POKEMON_FINAL = "pokemon_final"
    const val LOCK_POKEMON_ON_SET = "lock_pokemon_on_set"

    const val SAVE_INTERVAL_SECONDS = "save_interval_seconds"
    const val BUILDER_PERMISSION = "builder_permission"
    const val BUILDER_INFO_PERMISSION = "builder_info_permission"
    const val BUILDER_EDIT_PERMISSION = "builder_edit_permission"
    const val GENERATE_TOURNAMENT_PERMISSION = "generate_tournament_permission"
    const val FORCE_MATCH_COMPLETION_PERMISSION = "force_match_completion_permission"
}
