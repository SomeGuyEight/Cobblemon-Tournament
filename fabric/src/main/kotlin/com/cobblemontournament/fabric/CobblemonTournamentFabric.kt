package com.cobblemontournament.fabric
//
//import com.cobblemontournament.common.CobblemonTournament
//import com.cobblemontournament.common.CobblemonTournament.initialize
//import com.cobblemontournament.common.CommonInterface
//import com.cobblemontournament.common.commands.TournamentCommands
//import com.cobblemontournament.common.config.ConfigProvider
//import com.turtlehoarder.cobblemonchallenge.fabric.CobblemonChallengeFabric
//import net.fabricmc.api.ModInitializer
//import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
//import net.fabricmc.loader.api.FabricLoader
//
//object CobblemonTournamentFabric : ModInitializer, CommonInterface
//{
//    override fun onInitialize()
//    {
//        initialize( implementation = this)
//
//        // here while challenge is integrated in testing project
//        // : remove when able to use as dependency
//        CobblemonChallengeFabric().onInitialize()
//    }
//
//    override fun initializeConfig() {
//        ConfigProvider.registerConfigs( FabricLoader.getInstance().configDir)
//    }
//
//    override fun registerEvents() {
//        CobblemonTournament.registerEvents()
//    }
//
//    override fun registerCommands() {
//        CommandRegistrationCallback.EVENT.register(TournamentCommands::register)
//    }
//
//}
