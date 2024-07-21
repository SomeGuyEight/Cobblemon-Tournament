package com.someguy.api

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

interface CommandManager
{
    fun registerCommands(
        dispatcher  : CommandDispatcher<CommandSourceStack>,
        registry    : CommandBuildContext,
        selection   : Commands.CommandSelection
    )
}