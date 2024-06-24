package com.cobblemontournament.fabric.config;

import com.mojang.datafixers.util.Pair;

public class TournamentConfigProvider implements SimpleConfig.DefaultConfig
{
    private String configContents = "";
    //private final List<Pair<?,?>> configsList = new ArrayList<>();
    
    public void addKeyValuePair(Pair<String, ?> keyValuePair) {
        //configsList.add(keyValuePair);
        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + "\n";
    }
    
    @Override
    public String get(String namespace)
    {
        return configContents;
    }
}
