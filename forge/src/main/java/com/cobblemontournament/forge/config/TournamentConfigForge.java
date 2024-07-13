package com.cobblemontournament.forge.config;

import com.cobblemontournament.common.CobblemonTournament;
import com.cobblemontournament.common.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings( { "unused", "MemberVisibilityCanBePrivate", } )
public final class TournamentConfigForge
{
    @SuppressWarnings("")
    public TournamentConfigForge(ForgeConfigSpec.Builder builder)
    {
        // no need to pass to ConfigProvider, b/c Forge handles the config properties file internally
        CobblemonTournament.INSTANCE.getLOGGER().info("Loading Tournament Configs");
        builder.push(Config.CONFIG_FILE_NAME);
        
        var type            = Config.INSTANCE.defaultTournamentType();
        var format          = Config.INSTANCE.defaultChallengeFormat();
        var maxParticipants = Config.INSTANCE.defaultMaxParticipants();
        var teamSize        = Config.INSTANCE.defaultTeamSize();
        var groupSize       = Config.INSTANCE.defaultGroupSize();
        var minLevel        = Config.INSTANCE.defaultMinLevel();
        var maxLevel        = Config.INSTANCE.defaultMaxLevel();
        var showPreview     = Config.INSTANCE.defaultShowPreview();
        var saveIntervalSec = Config.INSTANCE.saveIntervalSeconds();
        
        String comment;
        comment = "Tournament type description...";
        type = builder.comment(comment).define(Config.TOURNAMENT_TYPE_KEY,type).get();
        
        comment = "Challenge format description...";
        format = builder.comment(comment).define(Config.CHALLENGE_FORMAT_KEY,format).get();
        
        comment = "Max participants description...";
        maxParticipants = builder.comment(comment).define(Config.MAX_PARTICIPANTS_KEY,maxParticipants).get();
        
        comment = "Team size description...";
        teamSize = builder.comment(comment).define(Config.TEAM_SIZE_KEY,teamSize).get();
        
        comment = "Group size description...";
        groupSize = builder.comment(comment).define(Config.GROUP_SIZE_KEY,groupSize).get();
        
        comment = "Min level description...";
        minLevel = builder.comment(comment).define(Config.MIN_LEVEL_KEY,minLevel).get();
        
        comment = "Max level description...";
        maxLevel = builder.comment(comment).define(Config.MAX_LEVEL_KEY,maxLevel).get();
        
        comment = "Show preview description...";
        showPreview = builder.comment(comment).define(Config.SHOW_PREVIEW_KEY,showPreview).get();
        
        comment = "Save interval seconds description...";
        saveIntervalSec = builder.comment(comment).define(Config.SAVE_INTERVAL_SECONDS_KEY,saveIntervalSec).get();
        
        //Config.initialize(type,format,maxParticipants,teamSize,groupSize,minLevel,maxLevel,showPreview,saveIntervalSec);
    }
    
}