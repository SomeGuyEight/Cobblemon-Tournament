package com.cobblemontournament.forge.config

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.config.TournamentConfig
import com.cobblemontournament.common.util.TournamentUtil
import net.minecraftforge.common.ForgeConfigSpec

object TournamentConfigForge
{
    private var DEFAULT_TOURNAMENT_TYPE     : ForgeConfigSpec.ConfigValue<String>?  = null
    private var DEFAULT_CHALLENGE_FORMAT    : ForgeConfigSpec.ConfigValue<String>?  = null
    private var DEFAULT_MAX_PARTICIPANTS    : ForgeConfigSpec.ConfigValue<Int>?     = null
    private var DEFAULT_TEAM_SIZE           : ForgeConfigSpec.ConfigValue<Int>?     = null
    private var DEFAULT_GROUP_SIZE          : ForgeConfigSpec.ConfigValue<Int>?     = null
    private var DEFAULT_MIN_LEVEL           : ForgeConfigSpec.ConfigValue<Int>?     = null
    private var DEFAULT_MAX_LEVEL           : ForgeConfigSpec.ConfigValue<Int>?     = null
    private var DEFAULT_SHOW_PREVIEW        : ForgeConfigSpec.ConfigValue<Boolean>? = null
    private var SAVE_INTERVAL_SECONDS       : ForgeConfigSpec.ConfigValue<Int>?     = null
    private var DEFAULT_BUILDER_PERMISSION                  : ForgeConfigSpec.ConfigValue<Boolean>? = null
    private var DEFAULT_BUILDER_INFO_PERMISSION             : ForgeConfigSpec.ConfigValue<Boolean>? = null
    private var DEFAULT_BUILDER_EDIT_PERMISSION             : ForgeConfigSpec.ConfigValue<Boolean>? = null
    private var DEFAULT_GENERATE_TOURNAMENT_PERMISSION      : ForgeConfigSpec.ConfigValue<Boolean>? = null
    private var DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION   : ForgeConfigSpec.ConfigValue<Boolean>? = null

    fun initialize(
        builder: ForgeConfigSpec.Builder
    ): TournamentConfigForge
    {
        // no need to pass to ConfigProvider, b/c Forge handles the config properties file internally
        CobblemonTournament.LOGGER.info("Loading Tournament Configs")

        builder.push(TournamentConfig.CONFIG_FILE_NAME)
        DEFAULT_TOURNAMENT_TYPE = builder
            .comment( "Amazing Tournament-Type description..." )
            .define( TournamentConfig.TOURNAMENT_TYPE_KEY, TournamentConfig.defaultTournamentType().name )

        val typeString = DEFAULT_TOURNAMENT_TYPE?.default
        val type = if ( typeString != null ) {
            TournamentUtil.getTournamentTypeOrNull( typeString )
        } else null

        DEFAULT_CHALLENGE_FORMAT = builder
            .comment( "Amazing Challenge-Format description..." )
            .define( TournamentConfig.CHALLENGE_FORMAT_KEY, TournamentConfig.defaultChallengeFormat().name )

        val formatString = DEFAULT_CHALLENGE_FORMAT?.default
        val format = if ( formatString != null ) {
            TournamentUtil.getChallengeFormatOrNull( formatString )
        } else null

        DEFAULT_MAX_PARTICIPANTS = builder
            .comment( "Amazing Max-Participants description..." )
            .define( TournamentConfig.MAX_PARTICIPANTS_KEY, TournamentConfig.defaultMaxParticipants() )

        DEFAULT_TEAM_SIZE = builder
            .comment( "Amazing Team-Size description..." )
            .define(TournamentConfig.TEAM_SIZE_KEY, TournamentConfig.defaultTeamSize())

        DEFAULT_GROUP_SIZE = builder
            .comment( "Amazing Group-Size description..." )
            .define( TournamentConfig.GROUP_SIZE_KEY, TournamentConfig.defaultGroupSize() )

        DEFAULT_MIN_LEVEL = builder
            .comment( "Amazing Min-Level description..." )
            .define( TournamentConfig.MIN_LEVEL_KEY, TournamentConfig.defaultMinLevel() )

        DEFAULT_MAX_LEVEL = builder
            .comment( "Amazing Max-Level description..." )
            .define( TournamentConfig.MAX_LEVEL_KEY, TournamentConfig.defaultMaxLevel() )

        DEFAULT_SHOW_PREVIEW = builder
            .comment( "Amazing Show-Preview description..." )
            .define( TournamentConfig.SHOW_PREVIEW_KEY, TournamentConfig.defaultShowPreview() )

        SAVE_INTERVAL_SECONDS = builder
            .comment( "Amazing Save-Interval-Seconds description..." )
            .define( TournamentConfig.SAVE_INTERVAL_SECONDS_KEY, TournamentConfig.saveIntervalSeconds() )

        DEFAULT_BUILDER_PERMISSION = builder
            .comment( "Amazing Builder-Permission description..." )
            .define( TournamentConfig.BUILDER_PERMISSION_KEY, TournamentConfig.defaultBuilderPermission() )

        DEFAULT_BUILDER_INFO_PERMISSION = builder
            .comment( "Amazing Builder-Info-Permission description..." )
            .define( TournamentConfig.BUILDER_INFO_PERMISSION_KEY, TournamentConfig.defaultBuilderInfoPermission() )

        DEFAULT_BUILDER_EDIT_PERMISSION = builder
            .comment( "Amazing Builder-Edit-Permission description..." )
            .define( TournamentConfig.BUILDER_EDIT_PERMISSION_KEY, TournamentConfig.defaultBuilderEditPermission() )

        DEFAULT_GENERATE_TOURNAMENT_PERMISSION = builder
            .comment( "Amazing Generate-Tournament-Permission description..." )
            .define( TournamentConfig.GENERATE_TOURNAMENT_PERMISSION_KEY, TournamentConfig.defaultGenerateTournamentPermission() )

        DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION = builder
            .comment( "Amazing Force-Match-Completion-Permission description..." )
            .define( TournamentConfig.FORCE_MATCH_COMPLETION_PERMISSION_KEY, TournamentConfig.defaultForceMatchCompletionPermission() )

        TournamentConfig.initialize(
            defaultTournamentType   = type,
            defaultChallengeFormat  = format,
            defaultMaxParticipants  = DEFAULT_MAX_PARTICIPANTS?.default,
            defaultTeamSize         = DEFAULT_TEAM_SIZE?.default,
            defaultGroupSize        = DEFAULT_GROUP_SIZE?.default,
            defaultMinLevel         = DEFAULT_MIN_LEVEL?.default,
            defaultMaxLevel         = DEFAULT_MAX_LEVEL?.default,
            defaultShowPreview      = DEFAULT_SHOW_PREVIEW?.default,
            saveIntervalSeconds     = SAVE_INTERVAL_SECONDS?.default,
            defaultBuilderPermission                = DEFAULT_BUILDER_PERMISSION?.default,
            defaultBuilderInfoPermission            = DEFAULT_BUILDER_INFO_PERMISSION?.default,
            defaultBuilderEditPermission            = DEFAULT_BUILDER_EDIT_PERMISSION?.default,
            defaultGenerateTournamentPermission     = DEFAULT_GENERATE_TOURNAMENT_PERMISSION?.default,
            defaultForceMatchCompletionPermission   = DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION?.default )

        return this
    }
}
