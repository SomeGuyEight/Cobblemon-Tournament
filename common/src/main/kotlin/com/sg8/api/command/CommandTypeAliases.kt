package com.sg8.api.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack


typealias CommandContext = CommandContext<CommandSourceStack>
typealias LiteralArgumentBuilder = LiteralArgumentBuilder<CommandSourceStack>
