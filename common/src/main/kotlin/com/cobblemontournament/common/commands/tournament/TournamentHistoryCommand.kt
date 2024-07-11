package com.cobblemontournament.common.commands.tournament

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.OVERVIEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.nodes.tournament.TournamentHistoryNode
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import org.slf4j.helpers.Util

object TournamentHistoryCommand
{
    /**
     * [TOURNAMENT] - [TOURNAMENT] - [HISTORY] - [TOURNAMENT_NAME]
     *
     * [OVERVIEW] - [tournamentHistory]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [TOURNAMENT]        ->
     *      literal     [HISTORY]           ->
     *      argument    [TOURNAMENT_NAME] , StringType ->
     *      literal     [OVERVIEW]          ->
     *      function    [tournamentHistory]
     *
     *      * - optional
     */
    @JvmStatic
    fun register(
        dispatcher  : CommandDispatcher <CommandSourceStack>,
        registry    : CommandBuildContext,
        selection   : CommandSelection
    )
    {
        dispatcher.register(
            TournamentHistoryNode.node(
                Commands.literal( OVERVIEW )
                    .executes { ctx ->
                        tournamentHistory( ctx )
                    }
            ) )
    }

    @JvmStatic
    private fun tournamentHistory(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        //val ( _, tournament ) = CommandUtil.getNodesAndTournament( ctx )
//        for ( entry in nodeEntries ) {
//            when ( entry.key ) {
//                TOURNAMENT_NAME -> {
//                    val ( instance, _ ) = TournamentStoreManager.getTournamentByName( entry.value )
//                    tournament = instance
//                }
//            }
//        }

        val tournament = TournamentStoreManager.getTournament( TournamentStoreManager.inactiveStoreKey )
        val player = ctx.source.player
        val text = if ( tournament == null ) {
            CommandUtil.failedCommand( reason = "Tournament was null" )
        } else if (player == null) {
            CommandUtil.failedCommand( reason = "Server Player was null" )
        } else {
            tournament.printOverviewInChat( player )
            return Command.SINGLE_SUCCESS
        }

        if (player != null) {
            player.displayClientMessage( text ,false)
        } else {
            Util.report( text.string )
        }
        return 0
    }

}
