package com.cobblemontournament.common.commands

import com.cobblemontournament.common.commands.builder.*
import com.cobblemontournament.common.commands.match.MyMatchesCommand
import com.cobblemontournament.common.commands.tournament.*
import com.mojang.brigadier.CommandDispatcher
import com.sg8.api.modimplementation.CommandImplementation
import net.minecraft.commands.*
import net.minecraft.commands.Commands.CommandSelection

object TournamentCommands : CommandImplementation {

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

        //TestCommands.register(dispatcher)
        //TestChatFormating.register(dispatcher)
        //RegisterFakePlayerCommand.register(dispatcher)
        //UnregisterFakePlayerCommand.register(dispatcher)
    }

}
