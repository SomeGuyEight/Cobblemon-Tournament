package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.GENERATE_TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.NEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.PRINT_DEBUG
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.api.tournament.TournamentData
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util

object GenerateTournamentCommand
{
    /**
     * [TOURNAMENT] - [BUILDER] -  [ACTIVE] - [BUILDER_NAME] - [GENERATE_TOURNAMENT]
     *
     * [NEW]+[TOURNAMENT_NAME] - [PRINT_DEBUG]* -> [generateTournament]
     *
     *      literal     [TOURNAMENT]            ->
     *      literal     [BUILDER]               ->
     *      argument    [ACTIVE] , StringType   ->
     *      literal     [BUILDER_NAME]          ->
     *      literal     [GENERATE_TOURNAMENT]   ->
     *      argument    [NEW]+[TOURNAMENT_NAME] , StringType ->
     *      * argument  [PRINT_DEBUG] , BooleanType ->
     *      function    [generateTournament]
     *
     *      * == optional
     */
    @JvmStatic
    fun register(
        dispatcher  : CommandDispatcher <CommandSourceStack>, )
//        registry    : CommandBuildContext,
//        selection   : CommandSelection )
    {
        dispatcher.register(
            ActiveBuilderNode.node(
                Commands.literal( GENERATE_TOURNAMENT )
                    .then(Commands.argument( "$NEW$TOURNAMENT_NAME", StringArgumentType.string() )
                        .executes {
                            ctx -> generateTournament( ctx )
                        }
                        //.then() // TODO other optional
                    ))
        )
    }

    @JvmStatic
    fun generateTournament(
        ctx : CommandContext <CommandSourceStack>,
    ): Int {
        var tournamentData: TournamentData? = null

        val ( nodeEntries, tournamentBuilder ) = CommandUtil.getNodesAndTournamentBuilder( ctx )
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                "$NEW$TOURNAMENT_NAME" -> tournamentData = tournamentBuilder?.toTournament( entry.value )
            }
        }

        var success = 0
        val text: MutableComponent
        if ( tournamentBuilder == null ) {
            text = CommandUtil.failedCommand( "Tournament Builder was null" )
        } else if ( tournamentData == null ) {
            text = CommandUtil.failedCommand( "Tournament Data was null" )
        } else {
            text = CommandUtil.successfulCommand( "GENERATED Tournament \"${tournamentData.tournament.name}\"" )
            success = Command.SINGLE_SUCCESS
        }

        val player = ctx.source.player
        if ( player != null ) {
            player.displayClientMessage( text ,false )
        } else {
            Util.report( text.string )
        }
        return success
    }

}
