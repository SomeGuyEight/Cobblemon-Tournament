package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderInfoNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_PROPERTIES
import com.cobblemontournament.common.commands.nodes.NodeKeys.INFO
import com.cobblemontournament.common.commands.nodes.NodeKeys.OVERVIEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER_PROPERTIES
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import org.slf4j.helpers.Util

object PrintBuilderInfoCommand
{
    /**
     * [TOURNAMENT] - [BUILDER] - [ACTIVE] - [BUILDER_NAME] - [INFO] -
     *
     * * arguments -> [printInfo]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      literal     [ACTIVE]        ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [INFO]          ->
     *      * arguments                 ->
     *      function    [printInfo]
     *
     *      * arguments fork
     */
    @JvmStatic
    fun register(
        dispatcher  : CommandDispatcher <CommandSourceStack>,
        registry    : CommandBuildContext,
        selection   : CommandSelection )
    {
        dispatcher.register(
            ActiveBuilderInfoNode.getInfo(
                Commands.literal( PLAYER_PROPERTIES )
                    .executes {
                        ctx -> printInfo( ctx )
                    }
            ) )

        dispatcher.register(
            ActiveBuilderInfoNode.getInfo(
                Commands.literal( BUILDER_PROPERTIES )
                    .executes {
                        ctx -> printInfo( ctx )
                    }
            ) )

        dispatcher.register(
            ActiveBuilderInfoNode.getInfo(
                Commands.literal( OVERVIEW )
                    .executes {
                        ctx -> printInfo( ctx )
                    }
            ) )
    }

    @JvmStatic
    private fun printInfo(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        var printBuilderInfo    = false
        var printPlayerInfo     = false
        var printOverview       = false

        val ( nodeEntries, tournamentBuilder ) = CommandUtil.getNodesAndTournamentBuilder( ctx )
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                BUILDER_PROPERTIES -> printBuilderInfo = true
                PLAYER_PROPERTIES -> printPlayerInfo = true
                OVERVIEW -> printOverview = true
            }
        }

        val player = ctx.source.player

        if ( printBuilderInfo && player != null ) {
            tournamentBuilder?.printPropertiesInChat( player )
        } else if ( printBuilderInfo ) {
            tournamentBuilder?.printProperties()
        }

        if ( printPlayerInfo && player != null ) {
            tournamentBuilder?.printPlayerInfoInChat( player )
        } else if ( printPlayerInfo ) {
            tournamentBuilder?.printPlayerInfo()
        }

        if ( printOverview && player != null ) {
            tournamentBuilder?.printPropertiesInChat( player )
            tournamentBuilder?.printPlayerInfoInChat( player )
        } else if ( printOverview ) {
            tournamentBuilder?.printProperties()
            tournamentBuilder?.printPlayerInfo()
        }

        if ( tournamentBuilder != null ) {
            return Command.SINGLE_SUCCESS
        }

        val text = CommandUtil.failedCommand( reason = "Tournament Builder was null" )
        if ( player != null ) {
            player.displayClientMessage( text ,false )
        } else {
            Util.report( text.string )
        }
        return 0
    }

}
