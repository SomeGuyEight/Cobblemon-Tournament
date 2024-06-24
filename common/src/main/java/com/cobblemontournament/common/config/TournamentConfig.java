package com.cobblemontournament.common.config;

import com.cobblemontournament.common.CommonInterface;
import com.cobblemontournament.common.tournament.TournamentType;
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat;

@SuppressWarnings("unused")
public abstract class TournamentConfig
{
    public static final String DEFAULT_TOURNAMENT_TYPE_CONFIG_NAME 	= "defaultTournamentType";
    public static final String DEFAULT_GROUP_SIZE_CONFIG_NAME 		= "defaultGroupSize";
    public static final String DEFAULT_MAX_PARTICIPANTS_CONFIG_NAME = "defaultMaxParticipants";
    public static final String DEFAULT_CHALLENGE_FORMAT_CONFIG_NAME = "defaultChallengeFormat";
    public static final String DEFAULT_MIN_LEVEL_CONFIG_NAME 		= "defaultMinLevel";
    public static final String DEFAULT_MAX_LEVEL_CONFIG_NAME 		= "defaultMaxLevel";
    public static final String DEFAULT_SHOW_PREVIEW_CONFIG_NAME 	= "defaultShowPreview";
    
    public static TournamentType    DEFAULT_TOURNAMENT_TYPE;
    public static int               DEFAULT_GROUP_SIZE;
    public static int               DEFAULT_MAX_PARTICIPANTS;
    public static ChallengeFormat   DEFAULT_CHALLENGE_FORMAT;
    public static int               DEFAULT_MIN_LEVEL;
    public static int               DEFAULT_MAX_LEVEL;
    public static boolean           DEFAULT_SHOW_PREVIEW;
    
    public void initialize(CommonInterface implementation)
    {
        DEFAULT_TOURNAMENT_TYPE 	= implementation.getTournamentTypeConfig(DEFAULT_TOURNAMENT_TYPE_CONFIG_NAME);
        DEFAULT_GROUP_SIZE 			= implementation.getIntConfig(DEFAULT_GROUP_SIZE_CONFIG_NAME);
        DEFAULT_MAX_PARTICIPANTS 	= implementation.getIntConfig(DEFAULT_MAX_PARTICIPANTS_CONFIG_NAME);
        DEFAULT_CHALLENGE_FORMAT 	= implementation.getChallengeFormatConfig(DEFAULT_CHALLENGE_FORMAT_CONFIG_NAME);
        DEFAULT_MIN_LEVEL 			= implementation.getIntConfig(DEFAULT_MIN_LEVEL_CONFIG_NAME);
        DEFAULT_MAX_LEVEL 			= implementation.getIntConfig(DEFAULT_MAX_LEVEL_CONFIG_NAME);
        DEFAULT_SHOW_PREVIEW 		= implementation.getBooleanConfig(DEFAULT_SHOW_PREVIEW_CONFIG_NAME);
    }
}
