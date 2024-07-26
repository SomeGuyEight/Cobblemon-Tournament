package com.cobblemontournament.common.commands

import com.cobblemontournament.common.commands.builder.*
import com.cobblemontournament.common.commands.match.MyMatchesCommand
import com.cobblemontournament.common.commands.tournament.*
import com.mojang.brigadier.CommandDispatcher
import com.someguy.mod.commands.CommandManager
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.CommandSelection

object TournamentCommands : CommandManager {

    override fun registerCommands(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registry: CommandBuildContext,
        selection: CommandSelection,
    ) {
        ActivateBuilderCommand.register(dispatcher)
        CreateTournamentBuilderCommand.register(dispatcher)
        DeactivateBuilderCommand.register(dispatcher)
        PrintBuilderInfoCommand.register(dispatcher)
        UpdateBuilderCommand.register(dispatcher)

        RegisterPlayerCommand.register(dispatcher)
        UnregisterPlayerCommand.register(dispatcher)
        UpdatePlayerCommand.register(dispatcher)

        GenerateTournamentCommand.register(dispatcher)

        MyMatchesCommand.register(dispatcher)

        ActiveTournamentCurrentMatchCommand.register(dispatcher)
        TournamentInfoCommand.register(dispatcher)

        //TestCommands.register( dispatcher, registry, selection )
        //TestChatFormating.register( dispatcher, registry, selection )
        //RegisterFakePlayerCommand.register( dispatcher, registry, selection )
        //UnregisterFakePlayerCommand.register( dispatcher, registry, selection )
    }

}
