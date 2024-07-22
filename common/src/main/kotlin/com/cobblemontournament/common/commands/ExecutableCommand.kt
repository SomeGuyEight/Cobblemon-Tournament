package com.cobblemontournament.common.commands

import com.cobblemontournament.common.commands.nodes.ExecutionNode

interface ExecutableCommand {
    val executionNode: ExecutionNode
}
