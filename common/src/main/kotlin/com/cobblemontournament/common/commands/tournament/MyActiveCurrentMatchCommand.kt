package com.cobblemontournament.common.commands.tournament

import com.cobblemontournament.common.api.MatchManager
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.CURRENT_MATCH
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.nodes.tournament.MyActiveTournamentNode
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import org.slf4j.helpers.Util

object MyActiveCurrentMatchCommand
{
    /**
     * [TOURNAMENT] - [TOURNAMENT] - [MY_ACTIVE] - [TOURNAMENT_NAME]
     *
     * [CURRENT_MATCH] - * arguments - [myCurrentMatches]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [TOURNAMENT]        ->
     *      literal     [MY_ACTIVE]         ->
     *      argument    [TOURNAMENT_NAME] , StringType ->
     *      literal     [CURRENT_MATCH]     ->
     *      function    [myActiveCommand]
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
            MyActiveTournamentNode.node(
                Commands.literal( CURRENT_MATCH )
                    .executes { ctx ->
                        myCurrentMatches( ctx )
                    }
            ))
    }

    @JvmStatic
    private fun myCurrentMatches(
        ctx: CommandContext<CommandSourceStack>
    ): Int
    {
        val ( _, tournament ) = CommandUtil.getNodesAndTournament( ctx )
//        for ( entry in nodeEntries ) {
//            when ( entry.key ) {
//                TOURNAMENT_NAME -> {
//                    val ( instance, _ ) = TournamentStoreManager.getTournamentByName(entry.value)
//                    tournament = instance
//                }
//            }
//        }

        val player = ctx.source.player
        val text = if ( tournament == null ) {
            CommandUtil.failedCommand( reason = "Tournament was null" )
        } else if (player == null) {
            CommandUtil.failedCommand( reason = "Server Player was null" )
        } else {
            MatchManager.handleMatchChallengeRequest( tournament = tournament, challenger = player )
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
