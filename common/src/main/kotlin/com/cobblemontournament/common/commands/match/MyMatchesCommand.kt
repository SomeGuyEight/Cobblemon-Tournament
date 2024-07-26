package com.cobblemontournament.common.commands.match

import com.cobblemontournament.common.api.MatchManager
import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.util.getServerPlayerOrDisplayFail
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[MY_MATCHES]
 */
object MyMatchesCommand {

    val executionNode by lazy { ExecutionNode { myMatches(ctx = it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher
            .register(MyMatchesNode
                .nest(Commands
                    .literal(ALL_MATCHES)
                    .executes(this.executionNode.action)
                )
            )
    }

    private fun myMatches(ctx: CommandContext): Int {
        val player = ctx.getServerPlayerOrDisplayFail() ?: return 0

        MatchManager.displayAllPlayerMatches(player)

        return Command.SINGLE_SUCCESS
    }

}
