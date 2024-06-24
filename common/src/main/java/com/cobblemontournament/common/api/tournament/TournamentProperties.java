package com.cobblemontournament.common.api.tournament;

import com.cobblemontournament.common.config.TournamentConfig;
import com.cobblemontournament.common.tournament.TournamentType;
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat;
import org.jetbrains.annotations.NotNull;

@NotNull
public final class TournamentProperties
{
    public static final TournamentType DEFAULT_TOURNAMENT_TYPE = TournamentConfig.DEFAULT_TOURNAMENT_TYPE;
    public static final int DEFAULT_GROUP_SIZE = TournamentConfig.DEFAULT_GROUP_SIZE;
    public static final int DEFAULT_MAX_PARTICIPANTS = TournamentConfig.DEFAULT_MAX_PARTICIPANTS;
    public static final ChallengeFormat DEFAULT_CHALLENGE_FORMAT = TournamentConfig.DEFAULT_CHALLENGE_FORMAT;
    public static final int DEFAULT_CHALLENGE_MIN_LEVEL = TournamentConfig.DEFAULT_MIN_LEVEL;
    public static final int DEFAULT_CHALLENGE_MAX_LEVEL = TournamentConfig.DEFAULT_MAX_LEVEL;
    public static final boolean DEFAULT_SHOW_PREVIEW = TournamentConfig.DEFAULT_SHOW_PREVIEW;

    public TournamentProperties()
    {
        tournamentType = DEFAULT_TOURNAMENT_TYPE;
        groupSize = DEFAULT_GROUP_SIZE;
        maxParticipants = DEFAULT_MAX_PARTICIPANTS;
        challengeFormat = DEFAULT_CHALLENGE_FORMAT;
        minLevel = DEFAULT_CHALLENGE_MIN_LEVEL;
        maxLevel = DEFAULT_CHALLENGE_MAX_LEVEL;
        showPreview = DEFAULT_SHOW_PREVIEW;
    }
    public TournamentProperties (
            TournamentType type,
            Integer groupSize,
            Integer maxParticipants,
            ChallengeFormat format,
            Integer minLevel,
            Integer maxLevel,
            Boolean showPreview
    ) {
        this.tournamentType = (type != null) ? type : DEFAULT_TOURNAMENT_TYPE;
        this.groupSize = groupSize != null ? groupSize : DEFAULT_GROUP_SIZE;
        this.maxParticipants = maxParticipants != null ? maxParticipants : DEFAULT_MAX_PARTICIPANTS;
        this.challengeFormat = format != null ? format : DEFAULT_CHALLENGE_FORMAT;
        this.minLevel = minLevel != null ? minLevel : DEFAULT_CHALLENGE_MIN_LEVEL;
        this.maxLevel = maxLevel != null ? maxLevel : DEFAULT_CHALLENGE_MAX_LEVEL;
        this.showPreview = showPreview != null ? showPreview : DEFAULT_SHOW_PREVIEW;
    }

    private final TournamentType tournamentType;
    private final Integer groupSize;
    private final Integer maxParticipants;
    private final ChallengeFormat challengeFormat;
    private final int minLevel;
    private final int maxLevel;
    private final boolean showPreview;

    @NotNull public TournamentType getTournamentType(){ return tournamentType; }
    public int getGroupSize(){ return groupSize; }
    public int getMaxPlayerCount(){ return maxParticipants; }
    @NotNull public ChallengeFormat getChallengeFormat(){ return challengeFormat; }
    public int getMinLevel(){ return minLevel; }
    public int getMaxLevel(){ return maxLevel; }
    public boolean getShowPreview(){ return showPreview; }
}
