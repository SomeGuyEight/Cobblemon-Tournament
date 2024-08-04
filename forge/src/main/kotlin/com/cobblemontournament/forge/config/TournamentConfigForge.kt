package com.cobblemontournament.forge.config

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.api.storage.DataKeys
import com.cobblemontournament.common.config.TournamentConfig
import com.cobblemontournament.common.tournament.TournamentType
import com.sg8.util.getConstantOrNull
import net.minecraftforge.common.ForgeConfigSpec.Builder
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue


object TournamentConfigForge {

    private var DEFAULT_TOURNAMENT_TYPE: ConfigValue<String>? = null
    private var DEFAULT_CHALLENGE_FORMAT: ConfigValue<String>? = null
    private var DEFAULT_MAX_PARTICIPANTS: ConfigValue<Int>? = null
    private var DEFAULT_TEAM_SIZE: ConfigValue<Int>? = null
    private var DEFAULT_GROUP_SIZE: ConfigValue<Int>? = null
    private var DEFAULT_MIN_LEVEL: ConfigValue<Int>? = null
    private var DEFAULT_MAX_LEVEL: ConfigValue<Int>? = null
    private var DEFAULT_SHOW_PREVIEW: ConfigValue<Boolean>? = null
    private var SAVE_INTERVAL_SECONDS: ConfigValue<Int>? = null
    private var DEFAULT_BUILDER_PERMISSION: ConfigValue<Boolean>? = null
    private var DEFAULT_BUILDER_INFO_PERMISSION: ConfigValue<Boolean>? = null
    private var DEFAULT_BUILDER_EDIT_PERMISSION: ConfigValue<Boolean>? = null
    private var DEFAULT_GENERATE_TOURNAMENT_PERMISSION: ConfigValue<Boolean>? = null
    private var DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION: ConfigValue<Boolean>? = null

    fun initialize(builder: Builder): TournamentConfigForge {
        CobblemonTournament.LOGGER.info("Loading Tournament Configs")
        builder.push(TournamentConfig.CONFIG_FILE_NAME)

        DEFAULT_TOURNAMENT_TYPE = builder
            .comment("Amazing Tournament-Type description...")
            .define(DataKeys.TOURNAMENT_TYPE, TournamentConfig.defaultTournamentType().name)

        val type = DEFAULT_TOURNAMENT_TYPE?.default?.getConstantOrNull<TournamentType>()

        DEFAULT_CHALLENGE_FORMAT = builder
            .comment("Amazing Challenge-Format description...")
            .define(DataKeys.CHALLENGE_FORMAT, TournamentConfig.defaultChallengeFormat().name)

        val format = DEFAULT_CHALLENGE_FORMAT?.default?.getConstantOrNull<ChallengeFormat>()

        DEFAULT_MAX_PARTICIPANTS = builder
            .comment("Amazing Max-Participants description...")
            .define(DataKeys.MAX_PARTICIPANTS, TournamentConfig.defaultMaxParticipants() )

        DEFAULT_TEAM_SIZE = builder
            .comment("Amazing Team-Size description...")
            .define(DataKeys.TEAM_SIZE, TournamentConfig.defaultTeamSize())

        DEFAULT_GROUP_SIZE = builder
            .comment("Amazing Group-Size description...")
            .define(DataKeys.GROUP_SIZE, TournamentConfig.defaultGroupSize())

        DEFAULT_MIN_LEVEL = builder
            .comment("Amazing Min-Level description...")
            .define(DataKeys.MIN_LEVEL, TournamentConfig.defaultMinLevel())

        DEFAULT_MAX_LEVEL = builder
            .comment("Amazing Max-Level description...")
            .define(DataKeys.MAX_LEVEL, TournamentConfig.defaultMaxLevel())

        DEFAULT_SHOW_PREVIEW = builder
            .comment("Amazing Show-Preview description...")
            .define(DataKeys.SHOW_PREVIEW, TournamentConfig.defaultShowPreview())

        SAVE_INTERVAL_SECONDS = builder
            .comment("Amazing Save-Interval-Seconds description...")
            .define(DataKeys.SAVE_INTERVAL_SECONDS, TournamentConfig.saveIntervalSeconds())

        DEFAULT_BUILDER_PERMISSION = builder
            .comment("Amazing Builder-Permission description...")
            .define(DataKeys.BUILDER_PERMISSION, TournamentConfig.defaultBuilderPermission())

        DEFAULT_BUILDER_INFO_PERMISSION = builder
            .comment("Amazing Builder-Info-Permission description...")
            .define(DataKeys.BUILDER_INFO_PERMISSION, TournamentConfig.defaultBuilderInfoPermission())

        DEFAULT_BUILDER_EDIT_PERMISSION = builder
            .comment("Amazing Builder-Edit-Permission description...")
            .define(DataKeys.BUILDER_EDIT_PERMISSION, TournamentConfig.defaultBuilderEditPermission())

        DEFAULT_GENERATE_TOURNAMENT_PERMISSION = builder
            .comment("Amazing Generate-Tournament-Permission description...")
            .define(
                DataKeys.GENERATE_TOURNAMENT_PERMISSION,
                TournamentConfig.defaultGenerateTournamentPermission()
            )

        DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION = builder
            .comment("Amazing Force-Match-Completion-Permission description...")
            .define(
                DataKeys.FORCE_MATCH_COMPLETION_PERMISSION,
                TournamentConfig.defaultForceMatchCompletionPermission()
            )

        TournamentConfig.initialize(
            defaultTournamentType = type,
            defaultChallengeFormat = format,
            defaultMaxParticipants = DEFAULT_MAX_PARTICIPANTS?.default,
            defaultTeamSize = DEFAULT_TEAM_SIZE?.default,
            defaultGroupSize = DEFAULT_GROUP_SIZE?.default,
            defaultMinLevel = DEFAULT_MIN_LEVEL?.default,
            defaultMaxLevel = DEFAULT_MAX_LEVEL?.default,
            defaultShowPreview = DEFAULT_SHOW_PREVIEW?.default,
            saveIntervalSeconds = SAVE_INTERVAL_SECONDS?.default,
            defaultBuilderPermission = DEFAULT_BUILDER_PERMISSION?.default,
            defaultBuilderInfoPermission = DEFAULT_BUILDER_INFO_PERMISSION?.default,
            defaultBuilderEditPermission = DEFAULT_BUILDER_EDIT_PERMISSION?.default,
            defaultGenerateTournamentPermission = DEFAULT_GENERATE_TOURNAMENT_PERMISSION?.default,
            defaultForceMatchCompletionPermission = DEFAULT_FORCE_MATCH_COMPLETION_PERMISSION?.default,
        )

        return this
    }
}
