package com.cobblemontournament.common.config

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.tournament.TournamentType
import com.google.gson.GsonBuilder
import org.slf4j.Logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter


object TournamentConfig {

    const val CONFIG_FILE_NAME = "cobblemon-tournament"
    private const val CONFIG_PATH = ("config/$CONFIG_FILE_NAME-config.json")

    private val properties = TournamentConfigProperties()

    private val LOGGER: Logger = CobblemonTournament.LOGGER

    private val GSON by lazy { GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create() }

    @JvmStatic fun defaultTournamentType(): TournamentType {
        return properties.defaultTournamentType
    }
    @JvmStatic fun defaultChallengeFormat(): ChallengeFormat {
        return properties.defaultChallengeFormat
    }
    @JvmStatic fun defaultMaxParticipants(): Int = properties.defaultMaxParticipants
    @JvmStatic fun defaultTeamSize(): Int = properties.defaultTeamSize
    @JvmStatic fun defaultGroupSize(): Int = properties.defaultGroupSize
    @JvmStatic fun defaultMinLevel(): Int = properties.defaultMinLevel
    @JvmStatic fun defaultMaxLevel(): Int = properties.defaultMaxLevel
    @JvmStatic fun defaultShowPreview(): Boolean = properties.defaultShowPreview
    @JvmStatic fun saveIntervalSeconds(): Int = properties.saveIntervalSeconds
    @JvmStatic fun defaultBuilderPermission(): Boolean = properties.defaultBuilderPermission
    @JvmStatic fun defaultBuilderInfoPermission(): Boolean {
        return properties.defaultBuilderInfoPermission
    }
    @JvmStatic fun defaultBuilderEditPermission(): Boolean {
        return properties.defaultBuilderEditPermission
    }
    @JvmStatic fun defaultGenerateTournamentPermission(): Boolean {
        return properties.defaultGenerateTournamentPermission
    }
    @JvmStatic fun defaultForceMatchCompletionPermission(): Boolean {
        return properties.defaultForceMatchCompletionPermission
    }

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
        properties.defaultBuilderPermission =
            defaultBuilderPermission ?: DEFAULT_BUILDER_PERMISSION
        properties.defaultBuilderInfoPermission =
            defaultBuilderInfoPermission ?: DEFAULT_BUILDER_INFO_PERMISSION
        properties.defaultBuilderEditPermission =
            defaultBuilderEditPermission ?: DEFAULT_BUILDER_EDIT_PERMISSION
        properties.defaultGenerateTournamentPermission =
            defaultGenerateTournamentPermission ?: DEFAULT_GENERATE_TOURNAMENT_PERMISSION
        properties.defaultForceMatchCompletionPermission =
            defaultForceMatchCompletionPermission ?: DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION
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
                GSON.fromJson(fileReader, properties::class.java)
                fileReader.close()
            } catch (e: Exception) {
                LOGGER.error(
                    "Failed to load '$CONFIG_FILE_NAME' config from Path: '$CONFIG_PATH'. " +
                            "Using default values."
                )
                e.printStackTrace()
            }
        } else {
            saveConfig()
            LOGGER.info("Saved new '$CONFIG_FILE_NAME' config to '$CONFIG_PATH'")
        }
    }

    private fun saveConfig() {
        val fileWriter = FileWriter(File(CONFIG_PATH))
        GSON.toJson(properties, fileWriter)
        fileWriter.flush()
        fileWriter.close()
    }

}
