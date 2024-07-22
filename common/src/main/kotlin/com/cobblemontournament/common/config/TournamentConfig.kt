package com.cobblemontournament.common.config

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.challenge.ChallengeFormat
import com.cobblemontournament.common.tournament.TournamentType
import com.google.gson.GsonBuilder
import org.slf4j.Logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object TournamentConfig {

    const val TOURNAMENT_TYPE_KEY = "tournament_type"
    const val CHALLENGE_FORMAT_KEY = "challenge_format"
    const val MAX_PARTICIPANTS_KEY = "max_participants"
    const val TEAM_SIZE_KEY = "team_size"
    const val GROUP_SIZE_KEY = "group_size"
    const val MIN_LEVEL_KEY = "min_level"
    const val MAX_LEVEL_KEY = "max_level"
    const val SHOW_PREVIEW_KEY = "show_preview"
    const val SAVE_INTERVAL_SECONDS_KEY = "save_interval_seconds"
    const val BUILDER_PERMISSION_KEY = "builder_permission"
    const val BUILDER_INFO_PERMISSION_KEY = "builder_info_permission"
    const val BUILDER_EDIT_PERMISSION_KEY = "builder_edit_permission"
    const val GENERATE_TOURNAMENT_PERMISSION_KEY = "generate_tournament_permission"
    const val FORCE_MATCH_COMPLETION_PERMISSION_KEY = "force_match_completion"

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

    const val CONFIG_FILE_NAME = "cobblemon-tournament"
    private const val CONFIG_PATH = "config/$CONFIG_FILE_NAME-config.json"

    private val properties = TournamentConfigProperties()

    private val LOGGER: Logger = CobblemonTournament.LOGGER

    private val GSON = lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()
    }

    @JvmStatic fun defaultTournamentType() = properties.defaultTournamentType
    @JvmStatic fun defaultChallengeFormat() = properties.defaultChallengeFormat
    @JvmStatic fun defaultMaxParticipants() = properties.defaultMaxParticipants
    @JvmStatic fun defaultTeamSize() = properties.defaultTeamSize
    @JvmStatic fun defaultGroupSize() = properties.defaultGroupSize
    @JvmStatic fun defaultMinLevel() = properties.defaultMinLevel
    @JvmStatic fun defaultMaxLevel() = properties.defaultMaxLevel
    @JvmStatic fun defaultShowPreview() = properties.defaultShowPreview
    @JvmStatic fun saveIntervalSeconds() = properties.saveIntervalSeconds
    @JvmStatic fun defaultBuilderPermission() = properties.defaultBuilderPermission
    @JvmStatic fun defaultBuilderInfoPermission() = properties.defaultBuilderInfoPermission
    @JvmStatic fun defaultBuilderEditPermission() = properties.defaultBuilderEditPermission
    @JvmStatic fun defaultGenerateTournamentPermission() = properties.defaultGenerateTournamentPermission
    @JvmStatic fun defaultForceMatchCompletionPermission() = properties.defaultForceMatchCompletionPermission

    @JvmStatic
    fun initialize(
        defaultTournamentType: TournamentType?,
        defaultChallengeFormat: ChallengeFormat?,
        defaultMaxParticipants: Int?,
        defaultTeamSize: Int?,
        defaultGroupSize: Int?,
        defaultMinLevel: Int?,
        defaultMaxLevel: Int?,
        defaultShowPreview: Boolean?,
        saveIntervalSeconds: Int?,
        defaultBuilderPermission: Boolean? = null,
        defaultBuilderInfoPermission: Boolean? = null,
        defaultBuilderEditPermission: Boolean? = null,
        defaultGenerateTournamentPermission: Boolean? = null,
        defaultForceMatchCompletionPermission: Boolean? = null,
    ) {
        properties.defaultTournamentType = defaultTournamentType ?: DEFAULT_TOURNAMENT_TYPE
        properties.defaultChallengeFormat = defaultChallengeFormat ?: DEFAULT_CHALLENGE_FORMAT
        properties.defaultMaxParticipants = defaultMaxParticipants ?: DEFAULT_MAX_PARTICIPANTS
        properties.defaultTeamSize = defaultTeamSize ?: DEFAULT_TEAM_SIZE
        properties.defaultGroupSize = defaultGroupSize ?: DEFAULT_GROUP_SIZE
        properties.defaultMinLevel = defaultMinLevel ?: DEFAULT_MIN_LEVEL
        properties.defaultMaxLevel = defaultMaxLevel ?: DEFAULT_MAX_LEVEL
        properties.defaultShowPreview = defaultShowPreview ?: DEFAULT_SHOW_PREVIEW
        properties.saveIntervalSeconds = saveIntervalSeconds ?: DEFAULT_SAVE_INTERVAL_SECONDS
        properties.defaultBuilderPermission = defaultBuilderPermission ?: DEFAULT_BUILDER_PERMISSION
        properties.defaultBuilderInfoPermission = defaultBuilderInfoPermission ?: DEFAULT_BUILDER_INFO_PERMISSION
        properties.defaultBuilderEditPermission = defaultBuilderEditPermission ?: DEFAULT_BUILDER_EDIT_PERMISSION
        properties.defaultGenerateTournamentPermission = defaultGenerateTournamentPermission ?: DEFAULT_GENERATE_TOURNAMENT_PERMISSION
        properties.defaultForceMatchCompletionPermission = defaultForceMatchCompletionPermission ?: DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION
    }

    fun registerConfigs() {
        CobblemonTournament.LOGGER.info("Loading Tournament Configs")
        tryLoadConfig()
    }

    private fun tryLoadConfig() {
        val configFile = File(CONFIG_PATH)
        configFile.parentFile.mkdirs()

        if (configFile.exists()) {
            try {
                val fileReader = FileReader(configFile)
                GSON.value.fromJson(fileReader, properties::class.java)
                fileReader.close()
            } catch (e: Exception) {
                LOGGER.error("Failed to load '$CONFIG_FILE_NAME' config from Path: '$CONFIG_PATH'. Using default values.")
                e.printStackTrace()
            }
        } else {
            saveConfig()
            LOGGER.info("Saved new '$CONFIG_FILE_NAME' config to '$CONFIG_PATH'")
        }
    }

    private fun saveConfig() {
        val fileWriter = FileWriter(File(CONFIG_PATH))
        GSON.value.toJson(properties, fileWriter)
        fileWriter.flush()
        fileWriter.close()
    }

}
