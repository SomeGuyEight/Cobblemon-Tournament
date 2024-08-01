package com.cobblemontournament.common.commands.tournament

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
 * [TOURNAMENT]-[TOURNAMENT]-([ACTIVE] or [HISTORY])-[TOURNAMENT_NAME]-[INFO]
 */
object TournamentInfoCommand {

    val executionNode by lazy { ExecutionNode { tournamentInfo(ctx = it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val overviewStack = Commands
            .literal(OVERVIEW)
            .executes(this.executionNode.handler)

        val resultsStack = Commands
            .literal(RESULTS)
            .executes(this.executionNode.handler)

        dispatcher.register(ActiveTournamentInfoNode.nest(overviewStack))
        dispatcher.register(ActiveTournamentInfoNode.nest(resultsStack))
        dispatcher.register(TournamentHistoryInfoNode.nest(overviewStack))
        dispatcher.register(TournamentHistoryInfoNode.nest(resultsStack))
    }

    private fun tournamentInfo(ctx: CommandContext): Int {
        val tournament = ctx.getTournamentOrDisplayFail() ?: return 0

        val player = ctx.getServerPlayerOrDisplayFail() ?: return 0

        if (ctx.nodes.any { it.node.name == OVERVIEW }) {
            tournament.displayOverviewInChat(player)
        } else if (ctx.nodes.any { it.node.name == RESULTS }) {
            tournament.displayResultsInChat(player)
        }

        return Command.SINGLE_SUCCESS
    }

}
