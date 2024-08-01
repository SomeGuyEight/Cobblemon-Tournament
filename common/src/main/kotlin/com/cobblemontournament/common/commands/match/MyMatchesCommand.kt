package com.cobblemontournament.common.commands.match

import com.cobblemontournament.common.api.match.MatchManager
import com.sg8.api.command.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.sg8.api.command.getServerPlayerOrDisplayFail
import com.sg8.api.command.node.ExecutionNode
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[MY_MATCHES]
 */
object MyMatchesCommand {

    val executionNode = ExecutionNode { myMatches(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher
            .register(MyMatchesNode
                .nest(Commands
                    .literal(ALL_MATCHES)
                    .executes(this.executionNode.handler)
                )
            )
    }

    private fun myMatches(ctx: CommandContext): Int {
        val player = ctx.getServerPlayerOrDisplayFail() ?: return 0

        MatchManager.displayAllPlayerMatches(player)

        return Command.SINGLE_SUCCESS
    }

}
