package com.cobblemontournament.common.commands.nodes

import com.cobblemontournament.common.commands.CommandContext

data class ExecutionNode(val action: (CommandContext) -> Int)
