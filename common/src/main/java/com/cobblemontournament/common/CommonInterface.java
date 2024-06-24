package com.cobblemontournament.common;

import com.cobblemontournament.common.config.TournamentConfig;
import com.cobblemontournament.common.tournament.TournamentType;
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat;

public abstract class CommonInterface
{
    public TournamentConfig config;
    public abstract void initializeConfig();
    public abstract int getIntConfig(String configName);
    public abstract boolean getBooleanConfig(String configName);
    public abstract TournamentType getTournamentTypeConfig(String configName);
    public abstract ChallengeFormat getChallengeFormatConfig(String configName);
    
    public abstract void registerEvents();
    public abstract void registerCommands();
}
