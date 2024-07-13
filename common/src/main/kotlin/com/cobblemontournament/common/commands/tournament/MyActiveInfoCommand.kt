package com.cobblemontournament.common.commands.tournament

import com.cobblemontournament.common.commands.nodes.tournament.MyActiveTournamentInfoNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.INFO
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.OVERVIEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.RESULTS
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import org.slf4j.helpers.Util

object MyActiveInfoCommand
{
    /**
     * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] - [TOURNAMENT_NAME]
     *
     * [INFO] - * arguments - [tournamentInfo]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [TOURNAMENT]        ->
     *      literal     [MY_ACTIVE]         ->
     *      argument    [TOURNAMENT_NAME] , StringType ->
     *      literal     [INFO]              ->
     *      * arguments                     ->
     *      function    [tournamentInfo]
     *
     *      * - optional
     */
    @JvmStatic
    fun register(
        dispatcher  : CommandDispatcher <CommandSourceStack>, )
//        registry    : CommandBuildContext,
//        selection   : CommandSelection )
    {
        dispatcher.register(
            MyActiveTournamentInfoNode.node(
                Commands.literal( OVERVIEW )
                    .executes { ctx ->
                        tournamentInfo( ctx )
                    }
            ) )
        dispatcher.register(
            MyActiveTournamentInfoNode.node(
                Commands.literal( RESULTS )
                    .executes { ctx ->
                        tournamentInfo( ctx )
                    }
            ) )
    }

    @JvmStatic
    private fun tournamentInfo(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        var overview = false
        var results = false
        val ( nodeEntries, tournament ) = CommandUtil.getNodesAndTournament( ctx )
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
