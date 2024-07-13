package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.commands.nodes.builder.ActivePlayersBuilderNode
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.EXECUTE
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.UNREGISTER
import com.cobblemontournament.common.api.PlayerManager
import com.cobblemontournament.common.util.ChatUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object UnregisterPlayerCommand
{
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
    @JvmStatic
    fun register(
        dispatcher  : CommandDispatcher <CommandSourceStack>, )
//        registry    : CommandBuildContext,
//        selection   : CommandSelection )
    {
        dispatcher.register(
            ActivePlayersBuilderNode.player(
                Commands.literal( UNREGISTER )
                    .then( Commands.argument( PLAYER_NAME, StringArgumentType.string() )
                        .suggests { ctx, builder ->
                            PlayerNameSuggestionProvider().getSuggestions( ctx, builder )
                        }
                        .then( Commands.literal( EXECUTE )
                            .executes {
                                ctx -> unregisterPlayer( ctx )
                            } )
                    ) ) )
    }

    @JvmStatic
    fun unregisterPlayer(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        var unregisteredPlayer  : ServerPlayer?         = null

        val ( nodeEntries, tournamentBuilder ) = CommandUtil.getNodesAndTournamentBuilder( ctx )
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                PLAYER_NAME ->  {
                    val properties = tournamentBuilder?.getPlayer( entry.value ) ?: continue
                    unregisteredPlayer = PlayerManager.getServerPlayer( properties.playerID ) ?: continue
                    tournamentBuilder.removePlayer( playerID = unregisteredPlayer.uuid )
                }
            }
        }

        var success = 0
        val text: MutableComponent
        if ( tournamentBuilder == null ) {
            text = CommandUtil.failedCommand(
                reason = "Tournament Builder was null" )
        } else if ( unregisteredPlayer == null ) {
            text = CommandUtil.failedCommand(
                reason = "Server Player was null" )
        } else {
            text = CommandUtil.successfulCommand(
                action = "UNREGISTERED ${unregisteredPlayer.name.string} from \"${tournamentBuilder.name}\"" )
            success = Command.SINGLE_SUCCESS
        }

        val player = ctx.source.player
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