package com.cobblemontournament.common.config

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.util.TournamentUtil
import com.someguy.storage.util.StoreUtil
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
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
        path: Path )
    {
        CobblemonTournament.LOGGER.info( "Loading Tournament Configs" )
        val file = path.resolve( "${Config.CONFIG_FILE_NAME}-config.json" ).toFile()
        registerConfig( path, file )
    }

    private fun registerConfig(
        path: Path,
        file: File )
    {
        val name = file.nameWithoutExtension
        val identifier = "Config '$name'"

        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        val json = StoreUtil.getJsonOrCreateFile( file, gson )

        if ( json == null ) {
            LOGGER.info( "{} is missing, generating default one...", identifier )
            try {
                createConfig( JsonObject(), file )
            } catch ( e: IOException ) {
                LOGGER.error( "{} failed to generate!", identifier )
                LOGGER.trace( e.stackTrace.toString() )
                broken = true
            }
        } else {
            try {
                loadConfig( json, file )
            } catch ( e: IOException ) {
                LOGGER.error( "{} failed to load!", identifier )
                LOGGER.trace( e.stackTrace.toString() )
                broken = true
            }
        }
    }

    private fun createConfig(
        json: JsonObject,
        file: File )
    {
        val pw: PrintWriter
        try {
            pw = PrintWriter( file )
        } catch ( e: FileNotFoundException ) {
            throw RuntimeException( e )
        }

        json.addProperty( Config.TOURNAMENT_STATUS_KEY      , Config.defaultTournamentStatus().toString() )
        json.addProperty( Config.TOURNAMENT_TYPE_KEY        , Config.defaultTournamentType().toString() )
        json.addProperty( Config.CHALLENGE_FORMAT_KEY       , Config.defaultChallengeFormat().toString() )
        json.addProperty( Config.TEAM_SIZE_KEY              , Config.defaultTeamSize() )
        json.addProperty( Config.GROUP_SIZE_KEY             , Config.defaultGroupSize() )
        json.addProperty( Config.MAX_PARTICIPANTS_KEY       , Config.defaultMaxParticipants() )
        json.addProperty( Config.MIN_LEVEL_KEY              , Config.defaultMinLevel() )
        json.addProperty( Config.MAX_LEVEL_KEY              , Config.defaultMaxLevel() )
        json.addProperty( Config.SHOW_PREVIEW_KEY           , Config.defaultShowPreview() )
        json.addProperty( Config.SAVE_INTERVAL_SECONDS_KEY  , Config.saveIntervalSeconds() )

        pw.write( json.toString() )
        pw.flush()
        pw.close()
    }

    private fun loadConfig(
        json: JsonObject,
        file: File )
    {
        val statusElement: JsonElement? = json.get( Config.TOURNAMENT_STATUS_KEY )
        val status = if (statusElement != null) {
            TournamentUtil.getTournamentStatusOrNull( statusElement.toString().filterNot { it == '"' } )
        } else Config.defaultTournamentStatus()

        val typeElement: JsonElement? = json.get( Config.TOURNAMENT_TYPE_KEY )
        val type = if ( typeElement != null ) {
            TournamentUtil.getTournamentTypeOrNull( typeElement.toString().filterNot { it == '"' } )
        } else Config.defaultTournamentType()

        val formatElement: JsonElement? = json.get( Config.CHALLENGE_FORMAT_KEY )
        val format = if ( formatElement != null ) {
            TournamentUtil.getChallengeFormatOrNull( formatElement.toString().filterNot { it == '"' } )
        } else Config.defaultChallengeFormat()

        val maxParticipantsElement: JsonElement? = json.get( Config.MAX_PARTICIPANTS_KEY )
        val maxParticipants = if ( maxParticipantsElement != null ) {
            maxParticipantsElement.toString().filterNot { it == '"' }.toIntOrNull()
        } else Config.defaultMaxParticipants()

        val teamSizeElement: JsonElement? = json.get( Config.TEAM_SIZE_KEY )
        val teamSize = if ( teamSizeElement != null ) {
            teamSizeElement.toString().filterNot { it == '"' }.toIntOrNull()
        } else Config.defaultTeamSize()

        val groupSizeElement: JsonElement? = json.get( Config.GROUP_SIZE_KEY )
        val groupSize = if ( groupSizeElement != null ) {
            groupSizeElement.toString().filterNot { it == '"' }.toIntOrNull()
        } else Config.defaultGroupSize()

        val minLevelElement: JsonElement? = json.get( Config.MIN_LEVEL_KEY )
        val minLevel = if ( minLevelElement != null ) {
            minLevelElement.toString().filterNot { it == '"' }.toIntOrNull()
        } else Config.defaultMinLevel()

        val maxLevelElement: JsonElement? = json.get( Config.MAX_LEVEL_KEY )
        val maxLevel = if ( maxLevelElement != null ) {
            maxLevelElement.toString().filterNot { it == '"' }.toIntOrNull()
        } else Config.defaultMaxLevel()

        val showPreviewElement: JsonElement? = json.get( Config.SHOW_PREVIEW_KEY )
        val showPreview = if ( showPreviewElement != null ) {
            showPreviewElement.toString().filterNot { it == '"' }.toBooleanStrictOrNull()
        } else Config.defaultShowPreview()

        val saveIntervalSecElement: JsonElement? = json.get( Config.SAVE_INTERVAL_SECONDS_KEY )
        val saveIntervalSec = if ( saveIntervalSecElement != null ) {
            saveIntervalSecElement.toString().filterNot { it == '"' }.toIntOrNull()
        } else Config.saveIntervalSeconds()

        Config.initialize(
            defaultTournamentStatus = status,
            defaultTournamentType   = type,
            defaultChallengeFormat  = format,
            defaultMaxParticipants  = maxParticipants,
            defaultTeamSize         = teamSize,
            defaultGroupSize        = groupSize,
            defaultMinLevel         = minLevel,
            defaultMaxLevel         = maxLevel,
            defaultShowPreview      = showPreview,
            saveIntervalSeconds     = saveIntervalSec )
    }

}
