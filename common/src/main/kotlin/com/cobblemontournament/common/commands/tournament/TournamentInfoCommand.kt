package com.cobblemontournament.common.commands.tournament

import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.tournament.MyActiveTournamentInfoNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.INFO
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.OVERVIEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.RESULTS
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.nodes.tournament.TournamentHistoryInfoNode
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import org.slf4j.helpers.Util

/**
 * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] or [HISTORY]
 *
 * [TOURNAMENT_NAME] - [INFO] - * arguments - [tournamentInfo]
 *
 *      literal     [TOURNAMENT]        ->
 *      literal     [TOURNAMENT]        ->
 *      literal     [MY_ACTIVE] or [HISTORY] ->
 *      argument    [TOURNAMENT_NAME] , StringType ->
 *      literal     [INFO]              ->
 *      * arguments                     ->
 *      function    [tournamentInfo]
 *
 *      * - optional
 */
object TournamentInfoCommand : ExecutableCommand
{
    override val executionNode get() = ExecutionNode { tournamentInfo( ctx = it ) }

    @JvmStatic
    fun register( dispatcher: CommandDispatcher <CommandSourceStack> )
    {
        val overviewStack = Commands
            .literal( OVERVIEW )
            .executes( this.executionNode.node )

        val resultsStack = Commands
            .literal( RESULTS )
            .executes( this.executionNode.node )

        dispatcher.register( MyActiveTournamentInfoNode.nest( overviewStack ) )
        dispatcher.register( MyActiveTournamentInfoNode.nest( resultsStack ) )
        dispatcher.register( TournamentHistoryInfoNode.nest( overviewStack ) )
        dispatcher.register( TournamentHistoryInfoNode.nest( resultsStack ) )
    }

    @JvmStatic
    private fun tournamentInfo(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        val ( nodeEntries, tournament ) = CommandUtil
            .getNodesAndTournament(
                ctx     = ctx,
                storeID = null )

        var overview = false
        var results = false
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                OVERVIEW -> overview = true
                RESULTS -> results = true
            }
        }

        val player = ctx.source.player
        val text = if ( tournament == null ) {
            CommandUtil.failedCommand( reason = "Tournament was null" )
        } else if (player == null) {
            CommandUtil.failedCommand( reason = "Server Player was null" )
        } else if (overview) {
            tournament.displayOverviewInChat( player )
            return Command.SINGLE_SUCCESS
        } else if (results) {
            tournament.displayResultsInChat( player )
            return Command.SINGLE_SUCCESS
        } else {
            CommandUtil.failedCommand( reason = "Tournament Info for an unknown reason" )
        }

        if (player != null) {
            player.displayClientMessage( text ,false)
        } else {
            Util.report( text.string )
        }
        return 0
    }
}
