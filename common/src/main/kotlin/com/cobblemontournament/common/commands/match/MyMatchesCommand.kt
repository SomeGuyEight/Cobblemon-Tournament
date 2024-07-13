package com.cobblemontournament.common.commands.match

import com.cobblemontournament.common.api.MatchManager
import com.cobblemontournament.common.commands.nodes.NodeKeys.ALL_MATCHES
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.MY_MATCHES
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.match.MyMatchesNode
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import org.slf4j.helpers.Util

object MyMatchesCommand
{
    /**
     * [TOURNAMENT] - [MY_MATCHES] - * arguments - [myMatches]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [TOURNAMENT]        ->
     *      literal     [MY_MATCHES]        ->
     *      * arguments                     ->
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
            MyMatchesNode.node(
                Commands.literal( ALL_MATCHES )
                    .executes { ctx ->
                        myMatches( ctx )
                    }
            ).executes { ctx ->
                myMatches( ctx )
            }
        )
    }

    @JvmStatic
    private fun myMatches(
        ctx: CommandContext<CommandSourceStack>
    ): Int
    {
        var allMatches = false
        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input )
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                ALL_MATCHES -> allMatches = true
            }
        }

        val player = ctx.source.player
        val text = if (player == null) {
            CommandUtil.failedCommand( reason = "Server Player was null" )
        } else {//if (allMatches) {
            MatchManager.displayAllPlayerMatches( player )
            return Command.SINGLE_SUCCESS
        }

        Util.report( text.string )
        return 0
    }

}
