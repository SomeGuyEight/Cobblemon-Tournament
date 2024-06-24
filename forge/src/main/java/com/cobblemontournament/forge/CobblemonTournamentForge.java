package com.cobblemontournament.forge;

import com.cobblemontournament.common.CobblemonTournament;
import com.cobblemontournament.common.CommonInterface;
import com.cobblemontournament.common.testing.command.TestTournamentCommand;
import com.cobblemontournament.common.tournament.TournamentType;
import com.cobblemontournament.forge.config.TournamentConfigForge;
import com.turtlehoarder.cobblemonchallenge.common.battle.ChallengeFormat;
import com.turtlehoarder.cobblemonchallenge.common.event.ChallengeEventHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("unused")
@Mod(CobblemonTournament.MOD_ID)
public class CobblemonTournamentForge extends CommonInterface
{
    private static final TournamentConfigForge config;
    private static final ForgeConfigSpec commonSpec;
    
    static
    {
        final Pair<TournamentConfigForge,ForgeConfigSpec> common = new ForgeConfigSpec.Builder()
                .configure(TournamentConfigForge::new);
        config = common.getLeft();
        commonSpec = common.getRight();
    }
    
    public CobblemonTournamentForge()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,commonSpec);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverInitialize);
    }
    
    public void serverInitialize(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            CobblemonTournament.initialize(this);
            
            // here while challenge is integrated in testing project
            // TODO: remove when able to use as dependency
//            var instance = new CobblemonChallenge();
//            instance.initializeChallenge(new CobblemonChallengeForge());
        });
    }
    
    public void commands(RegisterCommandsEvent e)
    {
        //TournamentCommand.register(e.getDispatcher());
        TestTournamentCommand.register(e.getDispatcher());
    }
    
    @Override
    public void initializeConfig()
    {
        config.initialize(this);
    }
    
    @Override
    public int getIntConfig(String configName)
    {
        return config.getIntConfig(configName);
    }
    
    @Override
    public boolean getBooleanConfig(String configName)
    {
        return config.getBooleanConfig(configName);
    }
    
    @Override
    public TournamentType getTournamentTypeConfig(String configName)
    {
        return config.getTournamentTypeConfig(configName);
    }
    
    @Override
    public ChallengeFormat getChallengeFormatConfig(String configName)
    {
        return config.getChallengeFormatConfig(configName);
    }

    @Override
    public void registerEvents()
    {
        ChallengeEventHandler.registerEvents();
        // ?? old ??
        //DistExecutor.safeCallWhenOn(Dist.DEDICATED_SERVER,() -> ChallengeEventHandler::registerEvents);
        {
            CobblemonTournament.registerEvents();
        }
    }
    
    @Override
    public void registerCommands()
    {
        // empty b/c commands registered in constructor
    }
}