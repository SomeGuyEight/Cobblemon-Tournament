package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderPlayersNode
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.UNREGISTER
import com.cobblemontournament.common.api.PlayerManager
import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.util.ChatUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util

/**
 * [TOURNAMENT] - [BUILDER] - [BUILDER_NAME] - [PLAYER]
 *
 * [UNREGISTER] - [PLAYER_NAME] -> [unregisterPlayer]
 *
 *      literal     [TOURNAMENT]        ->
 *      literal     [BUILDER]           ->
 *      argument    [BUILDER_NAME] , StringType ->
 *      literal     [PLAYER]            ->
 *      argument    [UNREGISTER]        ->
 *      argument    [PLAYER_NAME] , StringType ->
 *      method      [unregisterPlayer]
 */
object UnregisterPlayerCommand : ExecutableCommand
{
    override val executionNode get() = ExecutionNode { unregisterPlayer( ctx = it ) }

    @JvmStatic
    fun register( dispatcher: CommandDispatcher <CommandSourceStack> )
    {
        dispatcher.register(
            ActiveBuilderPlayersNode.nest(
                Commands.literal( UNREGISTER )
                    .then( Commands.argument( PLAYER_NAME, StringArgumentType.string() )
                        .suggests { ctx, builder ->
                            PlayerNameSuggestionProvider().getSuggestions( ctx, builder )
                        }
                        .executes( this.executionNode.node )
                    ) ) )
    }

    @JvmStatic
    fun unregisterPlayer(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        val ( nodeEntries, tournamentBuilder ) = CommandUtil
            .getNodesAndTournamentBuilder(
                ctx = ctx,
                storeID = TournamentStoreManager.activeStoreKey )

        // use player properties so removing an offline player is possible
        var playerProps: PlayerProperties? = null
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                PLAYER_NAME ->  playerProps = tournamentBuilder?.getPlayer( entry.value )
            }
        }

        var success = 0
        val text: MutableComponent
        if ( tournamentBuilder == null ) {
            text = CommandUtil.failedCommand(
                reason = "Tournament Builder was null" )
        } else if ( playerProps == null ) {
            text = CommandUtil.failedCommand(
                reason = "Player Properties were null" )
        } else if ( !tournamentBuilder.removePlayer( playerID = playerProps.playerID ) ) {
            text = CommandUtil.failedCommand(
                reason = "Function 'removePlayer( PlayerID )' \"${tournamentBuilder.name}\" returned false." )
        } else {
            text = CommandUtil.successfulCommand(
                action = "UNREGISTERED ${playerProps.name} from \"${tournamentBuilder.name}\"" )
            success = Command.SINGLE_SUCCESS
        }

        val player = ctx.source.player
        val unregisteredPlayer = if ( playerProps != null ) {
            PlayerManager.getServerPlayer( playerProps.playerID )
        } else null
        if ( player != null ) {
            player.displayClientMessage( text ,false )
            if ( unregisteredPlayer != null && unregisteredPlayer != player && tournamentBuilder != null ) {
                ChatUtil.displayInPlayerChat(
                    player = unregisteredPlayer,
                    text   = "You were successfully UNREGISTERED from Tournament Builder \"${tournamentBuilder.name}\"!",
                    color  = ChatUtil.white )
            }
        } else {
            Util.report( text.string )
        }
        return success
    }
}
