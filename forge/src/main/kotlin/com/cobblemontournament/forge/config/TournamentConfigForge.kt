package com.cobblemontournament.forge.config
// TODO WIP
//
//import com.cobblemontournament.common.CobblemonTournament
//import com.cobblemontournament.common.config.Config
//import com.cobblemontournament.common.config.Config.defaultChallengeFormat
//import com.cobblemontournament.common.config.Config.defaultGroupSize
//import com.cobblemontournament.common.config.Config.defaultMaxLevel
//import com.cobblemontournament.common.config.Config.defaultMaxParticipants
//import com.cobblemontournament.common.config.Config.defaultMinLevel
//import com.cobblemontournament.common.config.Config.defaultShowPreview
//import com.cobblemontournament.common.config.Config.defaultTeamSize
//import com.cobblemontournament.common.config.Config.defaultTournamentType
//import com.cobblemontournament.common.config.Config.initialize
//import com.cobblemontournament.common.config.Config.saveIntervalSeconds
//import net.minecraftforge.common.ForgeConfigSpec
//
//object TournamentConfigForge {
//
//    fun initialize(
//        builder: ForgeConfigSpec.Builder
//    ): ForgeConfigSpec {
//        // no need to pass to ConfigProvider, b/c Forge handles the config properties file internally
//        CobblemonTournament.LOGGER.info("Loading Tournament Configs")
//        builder.push(Config.CONFIG_FILE_NAME)
//
//        var type = defaultTournamentType()
//        var format = defaultChallengeFormat()
//        var maxParticipants = defaultMaxParticipants()
//        var teamSize = defaultTeamSize()
//        var groupSize = defaultGroupSize()
//        var minLevel = defaultMinLevel()
//        var maxLevel = defaultMaxLevel()
//        var showPreview = defaultShowPreview()
//        var saveIntervalSec = saveIntervalSeconds()
//        var comment = "Tournament type description..."
//        type = builder.comment(comment).define(Config.TOURNAMENT_TYPE_KEY, type).get()
//
//        comment = "Challenge format description..."
//        format = builder.comment(comment).define(Config.CHALLENGE_FORMAT_KEY, format).get()
//
//        comment = "Max participants description..."
//        maxParticipants = builder.comment(comment).define(Config.MAX_PARTICIPANTS_KEY, maxParticipants).get()
//
//        comment = "Team size description..."
//        teamSize = builder.comment(comment).define(Config.TEAM_SIZE_KEY, teamSize).get()
//
//        comment = "Group size description..."
//        groupSize = builder.comment(comment).define(Config.GROUP_SIZE_KEY, groupSize).get()
//
//        comment = "Min level description..."
//        minLevel = builder.comment(comment).define(Config.MIN_LEVEL_KEY, minLevel).get()
//
//        comment = "Max level description..."
//        maxLevel = builder.comment(comment).define(Config.MAX_LEVEL_KEY, maxLevel).get()
//
//        comment = "Show preview description..."
//        showPreview = builder.comment(comment).define(Config.SHOW_PREVIEW_KEY, showPreview).get()
//
//        comment = "Save interval seconds description..."
//        saveIntervalSec = builder.comment(comment).define(Config.SAVE_INTERVAL_SECONDS_KEY, saveIntervalSec).get()
//
//        initialize(type, format, maxParticipants, teamSize, groupSize, minLevel, maxLevel, showPreview, saveIntervalSec)
//        return builder.build()
//    }
//}