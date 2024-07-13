package com.cobblemontournament.common.commands

import com.cobblemontournament.common.commands.builder.*
import com.cobblemontournament.common.commands.match.MyMatchesCommand
import com.cobblemontournament.common.commands.tournament.*
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.CommandSelection

object TournamentCommands
{
    @JvmStatic
    fun register(
        dispatcher  : CommandDispatcher <CommandSourceStack>,
        registry    : CommandBuildContext,
        selection   : CommandSelection )
    {
        CreateTournamentBuilderCommand.register( dispatcher ) //, registry, selection )
        UpdateBuilderCommand.register( dispatcher ) //, registry, selection )
        GenerateTournamentCommand.register( dispatcher ) //, registry, selection )
        PrintBuilderInfoCommand.register( dispatcher ) //, registry, selection )
        RegisterPlayerCommand.register( dispatcher ) //, registry, selection )
        UnregisterPlayerCommand.register( dispatcher ) //, registry, selection )
        UpdatePlayerCommand.register( dispatcher ) //, registry, selection )

        MyActiveInfoCommand.register(dispatcher ) //, registry, selection )
        MyActiveCurrentMatchCommand.register(dispatcher ) //, registry, selection )
        TournamentHistoryCommand.register( dispatcher ) //, registry, selection )

        MyMatchesCommand.register( dispatcher ) //, registry, selection )

        //TestCommands.register( dispatcher, registry, selection )
        //RegisterFakePlayerCommand.register( dispatcher, registry, selection )
        //UnregisterFakePlayerCommand.register( dispatcher, registry, selection )
        //TestChatFormating.register( dispatcher, registry, selection )
    }

}
