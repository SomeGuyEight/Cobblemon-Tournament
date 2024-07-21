package com.cobblemontournament.common.commands.builder

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
import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderNameNode
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util

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
object GenerateTournamentCommand : ExecutableCommand
{
    override val executionNode get() = ExecutionNode { generateTournament( ctx = it ) }

    @JvmStatic
    fun register( dispatcher: CommandDispatcher <CommandSourceStack> )
    {
        dispatcher.register(
            ActiveBuilderNameNode.nest(
                Commands.literal( GENERATE_TOURNAMENT )
                    .then(Commands.argument( "$NEW$TOURNAMENT_NAME", StringArgumentType.string() )
                        .executes( this.executionNode.node )
                        // TODO other optional parameters
                    ) )
        )
    }

    @JvmStatic
    fun generateTournament(
        ctx : CommandContext <CommandSourceStack>,
    ): Int
    {
        val ( nodeEntries, tournamentBuilder ) = CommandUtil
            .getNodesAndTournamentBuilder(
                ctx     = ctx,
                storeID = null )

        var tournamentData: TournamentData? = null
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
