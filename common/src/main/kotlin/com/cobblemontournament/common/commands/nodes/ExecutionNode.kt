package com.cobblemontournament.common.commands.nodes

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack

data class ExecutionNode(val node: (CommandContext<CommandSourceStack>) -> Int)
