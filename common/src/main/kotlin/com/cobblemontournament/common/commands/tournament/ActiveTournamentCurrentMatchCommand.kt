package com.cobblemontournament.common.commands.tournament

import com.cobblemontournament.common.api.match.MatchManager
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.sg8.api.command.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.util.getTournamentOrDisplayFail
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.sg8.api.command.getServerPlayerOrDisplayFail
import com.sg8.api.command.node.ExecutionNode
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[TOURNAMENT]-[ACTIVE]-[TOURNAMENT_NAME]-[CURRENT_MATCH]
 */
object ActiveTournamentCurrentMatchCommand {

    val executionNode by lazy { ExecutionNode { myCurrentMatches(ctx = it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(ActiveTournamentNameNode
            .nest(Commands
                .literal(CURRENT_MATCH)
                .executes(this.executionNode.handler)
            )
        )
    }

    private fun myCurrentMatches(ctx: CommandContext): Int {
        val tournament = ctx.getTournamentOrDisplayFail(
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
        ) ?: return 0

        val player = ctx.getServerPlayerOrDisplayFail() ?: return 0

        MatchManager.handleMatchChallengeRequest(tournament = tournament, challenger = player)

        return Command.SINGLE_SUCCESS
    }

}
