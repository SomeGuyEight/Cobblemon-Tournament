package com.cobblemontournament.common.config

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.util.StoreUtil
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.slf4j.Logger
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.PrintWriter
import java.nio.file.Path

object ConfigProvider
{
    val LOGGER: Logger = CobblemonTournament.LOGGER

    @Suppress("unused")
    private var broken: Boolean = false

    @JvmStatic
    fun registerConfigs(
        path: Path
    )
    {
        CobblemonTournament.LOGGER.info("Loading Tournament Configs")
        val file = path.resolve("${Config.CONFIG_FILE_NAME}-config.json").toFile()
        registerConfig(path,file)
    }

    private fun registerConfig(
        path: Path,
        file: File
    )
    {
        val name = file.nameWithoutExtension
        val identifier = "Config '$name'"

        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        val json = StoreUtil.getJsonOrCreateFile(file,gson)

        if (json == null) {
            LOGGER.info("{} is missing, generating default one...",identifier)
            try {
                createConfig(JsonObject(),file)
            } catch (e: IOException) {
                LOGGER.error("{} failed to generate!",identifier)
                LOGGER.trace(e.stackTrace.toString())
                broken = true
            }
        } else {
            try {
                loadConfig(json,file)
            } catch (e: IOException) {
                LOGGER.error("{} failed to load!",identifier)
                LOGGER.trace(e.stackTrace.toString())
                broken = true
            }
        }
    }

    private fun createConfig(
        json: JsonObject,
        file: File
    )
    {
        val pw: PrintWriter
        try {
            pw = PrintWriter(file)
        } catch (e: FileNotFoundException) {
            throw RuntimeException(e)
        }

        json.addProperty(Config.TOURNAMENT_TYPE_KEY,Config.defaultTournamentType().toString())
        json.addProperty(Config.CHALLENGE_FORMAT_KEY,Config.defaultChallengeFormat().toString())
        json.addProperty(Config.TEAM_SIZE_KEY,Config.defaultTeamSize())
        json.addProperty(Config.GROUP_SIZE_KEY,Config.defaultGroupSize())
        json.addProperty(Config.MAX_PARTICIPANTS_KEY,Config.defaultMaxParticipants())
        json.addProperty(Config.MIN_LEVEL_KEY,Config.defaultMinLevel())
        json.addProperty(Config.MAX_LEVEL_KEY,Config.defaultMaxLevel())
        json.addProperty(Config.SHOW_PREVIEW_KEY,Config.defaultShowPreview())
        json.addProperty(Config.SAVE_INTERVAL_SECONDS_KEY,Config.saveIntervalSeconds())

        pw.write(json.toString())
        pw.flush()
        pw.close()
    }

    private fun loadConfig(
        json: JsonObject,
        file: File
    )
    {
        val type = TournamentUtil.getTournamentTypeOrNull(json.get(Config.TOURNAMENT_TYPE_KEY).toString().filterNot { it == '"' })
        val format = TournamentUtil.getChallengeFormatOrNull(json.get(Config.CHALLENGE_FORMAT_KEY).toString().filterNot { it == '"' })
        val maxParticipants = json.get(Config.MAX_PARTICIPANTS_KEY).toString().filterNot { it == '"' }.toIntOrNull()
        val teamSize = json.get(Config.TEAM_SIZE_KEY).toString().filterNot { it == '"' }.toIntOrNull()
        val groupSize = json.get(Config.GROUP_SIZE_KEY).toString().filterNot { it == '"' }.toIntOrNull()
        val minLevel = json.get(Config.MIN_LEVEL_KEY).toString().filterNot { it == '"' }.toIntOrNull()
        val maxLevel = json.get(Config.MAX_LEVEL_KEY).toString().filterNot { it == '"' }.toIntOrNull()
        val showPreview = json.get(Config.SHOW_PREVIEW_KEY).toString().filterNot { it == '"' }.toBooleanStrictOrNull()
        val saveIntervalSec = json.get(Config.SAVE_INTERVAL_SECONDS_KEY).toString().filterNot { it == '"' }.toIntOrNull()

        Config.initialize(type,format,maxParticipants,teamSize,groupSize,minLevel,maxLevel,showPreview,saveIntervalSec)
    }

}