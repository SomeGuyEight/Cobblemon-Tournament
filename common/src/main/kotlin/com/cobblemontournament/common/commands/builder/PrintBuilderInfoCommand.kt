package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
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
import com.cobblemontournament.common.commands.nodes.builder.BuilderHistoryInfoNode
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import org.slf4j.helpers.Util

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
object PrintBuilderInfoCommand : ExecutableCommand
{
    override val executionNode get() = ExecutionNode { printInfo( ctx = it ) }

    @JvmStatic
    fun register( dispatcher: CommandDispatcher <CommandSourceStack> )
    {
        val overviewStack = Commands
            .literal( OVERVIEW )
            .executes( this.executionNode.node )

        val builderPropertiesStack = Commands
            .literal( BUILDER_PROPERTIES )
            .executes( this.executionNode.node )

        val playerPropertiesStack = Commands
            .literal( PLAYER_PROPERTIES )
            .executes( this.executionNode.node )

        dispatcher.register( ActiveBuilderInfoNode.nest( overviewStack ) )
        dispatcher.register( ActiveBuilderInfoNode.nest( builderPropertiesStack ) )
        dispatcher.register( ActiveBuilderInfoNode.nest( playerPropertiesStack ) )

        dispatcher.register( BuilderHistoryInfoNode.nest( overviewStack ) )
        dispatcher.register( BuilderHistoryInfoNode.nest( builderPropertiesStack ) )
        dispatcher.register( BuilderHistoryInfoNode.nest( playerPropertiesStack ) )
    }

    @JvmStatic
    fun printInfo(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        var printBuilderInfo    = false
        var printPlayerInfo     = false
        var printOverview       = false

        val ( nodeEntries, tournamentBuilder ) = CommandUtil
            .getNodesAndTournamentBuilder(
                ctx = ctx,
                storeID = null )

        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                BUILDER_PROPERTIES -> printBuilderInfo = true
                PLAYER_PROPERTIES -> printPlayerInfo = true
                OVERVIEW -> printOverview = true
            }
        }

        val player = ctx.source.player

        if ( printBuilderInfo && player != null ) {
            tournamentBuilder?.displayPropertiesInChatSlim( player )
        } else if ( printBuilderInfo ) {
            tournamentBuilder?.printProperties()
        }

        if ( printPlayerInfo && player != null ) {
            tournamentBuilder?.displayPlayerInfoInChat(
                player      = player,
                spacing     = "  ",
                displaySeed = true )
        } else if ( printPlayerInfo ) {
            tournamentBuilder?.printPlayerInfo()
        }

        if ( printOverview && player != null ) {
            tournamentBuilder?.displayPropertiesInChat( player )
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
