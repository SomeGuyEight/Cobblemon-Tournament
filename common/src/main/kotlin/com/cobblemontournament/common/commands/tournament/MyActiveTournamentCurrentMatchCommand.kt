package com.cobblemontournament.common.commands.tournament

import com.cobblemontournament.common.api.WatchedMatches
import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.CURRENT_MATCH
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.nodes.tournament.MyActiveTournamentNameNode
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import org.slf4j.helpers.Util

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
object MyActiveTournamentCurrentMatchCommand : ExecutableCommand {

    override val executionNode get() = ExecutionNode { myCurrentMatches(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(MyActiveTournamentNameNode
            .nest(Commands
                .literal(CURRENT_MATCH)
                .executes(this.executionNode.node)
            ))
    }

    private fun myCurrentMatches(ctx: CommandContext<CommandSourceStack>): Int {
        val tournament = CommandUtil.getNodesAndTournament(
            ctx = ctx,
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
        ).second

        val player = ctx.source.player
        val text = if ( tournament == null ) {
            CommandUtil.failedCommand(reason = "Tournament was null")
        } else if ( player == null ) {
            CommandUtil.failedCommand(reason = "Server Player was null")
        } else {
            WatchedMatches.handleMatchChallengeRequest(tournament = tournament, challenger = player)
            return Command.SINGLE_SUCCESS
        }

        if ( player != null ) {
            player.displayClientMessage(text , false)
        } else {
            Util.report(text.string)
        }

        return 0
    }

}
