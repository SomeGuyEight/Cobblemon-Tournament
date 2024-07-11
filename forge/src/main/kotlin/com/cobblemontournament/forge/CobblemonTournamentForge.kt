package com.cobblemontournament.forge

import com.cobblemontournament.common.CTModImplementation
import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.commands.TournamentCommands
import com.cobblemontournament.forge.config.TournamentConfigForge
import com.turtlehoarder.cobblemonchallenge.common.event.ChallengeEventHandler
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(CobblemonTournament.MOD_ID)
object CobblemonTournamentForge: CTModImplementation
{
    private val config: TournamentConfigForge
    private val commonSpec: ForgeConfigSpec

    init
    {
        val pair = ForgeConfigSpec.Builder().configure {
            builder: ForgeConfigSpec.Builder -> TournamentConfigForge( builder)
        }
        config = pair.left
        commonSpec = pair.right
        ModLoadingContext.get().registerConfig( ModConfig.Type.COMMON, commonSpec)
        FMLJavaModLoadingContext.get().modEventBus.addListener {
                event: FMLCommonSetupEvent -> this.serverInitialize( event)
        }
        MinecraftForge.EVENT_BUS.addListener {
                event: RegisterCommandsEvent -> this.commands( event)
        }
    }

    fun serverInitialize(
        event: FMLCommonSetupEvent
    ) {
        event.enqueueWork { CobblemonTournament.initialize( implementation = this) }
    }

    override fun initializeConfig() {
//        FMLJavaModLoadingContext.get().modEventBus.addListener {
//                event: FMLCommonSetupEvent -> this.serverInitialize( event)
//        }
    }

    override fun registerEvents() {
        ChallengeEventHandler.registerEvents()
        // ?? old ??
        //DistExecutor.safeCallWhenOn(Dist.DEDICATED_SERVER,() -> ChallengeEventHandler::registerEvents);
        //{
        CobblemonTournament.registerEvents()
        //}
    }

    override fun registerCommands() {
//        MinecraftForge.EVENT_BUS.addListener {
//                event: RegisterCommandsEvent -> this.commands( event)
//        }
    }

    fun commands(e: RegisterCommandsEvent) = TournamentCommands::register

}
