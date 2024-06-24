package com.turtlehoarder.cobblemonchallenge.fabric;


import com.turtlehoarder.cobblemonchallenge.fabric.config.ChallengeConfig;
import com.turtlehoarder.cobblemonchallenge.common.CobblemonChallenge;
import com.turtlehoarder.cobblemonchallenge.common.CobblemonChallengeCommonInterface;
import com.turtlehoarder.cobblemonchallenge.common.command.ChallengeCommand;
import com.turtlehoarder.cobblemonchallenge.common.event.ChallengeEventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CobblemonChallengeFabric extends CobblemonChallengeCommonInterface implements ModInitializer {

    public static String MODID = CobblemonChallenge.MODID;
    
    @Override
    public void onInitialize() {
        ChallengeConfig.registerConfigs();
        CobblemonChallenge challenge = new CobblemonChallenge();
        challenge.initializeChallenge(this);
    }

    @Override
    @SuppressWarnings("deprecation") // TODO address this
    public int getIntConfig(String configName) {
        return Integer.parseInt(ChallengeConfig.CONFIG.get(configName));
    }

    @Override
    @SuppressWarnings("deprecation") // TODO address this
    public boolean getBooleanConfig(String configName) {
        return Boolean.parseBoolean(ChallengeConfig.CONFIG.get(configName));
    }

    @Override
    public void registerEvents() {
        ChallengeEventHandler.registerEvents();
    }

    @Override
    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> ChallengeCommand.register(commandDispatcher));
    }
}