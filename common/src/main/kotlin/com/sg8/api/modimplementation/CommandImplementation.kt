package com.sg8.api.modimplementation

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands


interface CommandImplementation {

    fun registerCommands(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registry: CommandBuildContext,
        selection: Commands.CommandSelection
    )

}
