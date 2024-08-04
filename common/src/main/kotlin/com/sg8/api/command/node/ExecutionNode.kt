package com.sg8.api.command.node

import com.sg8.api.command.CommandContext


data class ExecutionNode(val handler: (CommandContext) -> Int)
