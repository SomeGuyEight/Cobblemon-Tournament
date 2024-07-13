package com.cobblemontournament.common.commands.builder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.commands.nodes.builder.ActivePlayersBuilderNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER_ENTITY
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER_SEED
import com.cobblemontournament.common.commands.nodes.NodeKeys.REGISTER
import com.cobblemontournament.common.commands.nodes.NodeKeys.SEED
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.util.ChatUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object RegisterPlayerCommand
{
    /**
     * [TOURNAMENT] - [BUILDER] - [BUILDER_NAME] - [PLAYER]
     *
     * [PLAYER_ENTITY] - * arguments -> [registerPlayer]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [BUILDER]           ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [PLAYER]            ->
     *      literal     [REGISTER]          ->
     *      argument    [PLAYER_ENTITY] , EntityType ->
     *      * arguments
     *      method      [registerPlayer]
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
            ActivePlayersBuilderNode.player(
                Commands.literal( REGISTER )
                    .then( Commands.argument( PLAYER_ENTITY, EntityArgument.player() )
                        .executes {
                            ctx -> registerPlayer( ctx = ctx)
                        }
                        .then( Commands.literal( SEED )
                            .then( Commands.argument( PLAYER_SEED, IntegerArgumentType.integer( -1 ) )
                                .executes {
                                    ctx -> registerPlayer( ctx = ctx )
                                } ) )
                    ) ) )
    }

    @JvmStatic
    fun registerPlayer(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        var registeredPlayer    : ServerPlayer?         = null
        var playerProperties    : PlayerProperties?     = null

        val ( nodeEntries, tournamentBuilder ) = CommandUtil.getNodesAndTournamentBuilder( ctx )
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                PLAYER_ENTITY -> {
                    registeredPlayer = EntityArgument.getPlayer( ctx, entry.key )
                    val success = tournamentBuilder?.addPlayer(
                        playerID    = registeredPlayer.uuid,
                        playerName  = registeredPlayer.name.string,
                        actorType   = ActorType.PLAYER,
                    )
                    if ( success == true ) {
                        playerProperties = tournamentBuilder.getPlayer( playerID = registeredPlayer.uuid )
                    }
                }
                PLAYER_SEED -> {
                    if ( playerProperties != null ) {
                        val seed = Integer.parseInt( entry.value )
                        playerProperties.seed = seed
                        playerProperties.originalSeed = seed
                    }
                }
            }
        }

        var success = 0
        val text: MutableComponent
        if ( tournamentBuilder == null ) {
            text = CommandUtil.failedCommand(
                reason = "Tournament Builder was null" )
        } else if ( registeredPlayer == null ) {
            text = CommandUtil.failedCommand(
                reason = "Server Player was null" )
        } else if ( playerProperties == null ) {
            text = CommandUtil.failedCommand(
                reason = "Player already registered OR registration failed inside builder" )
        } else {
            text = CommandUtil.successfulCommand(
                action = "REGISTERED ${registeredPlayer.name.string} with Tournament Builder \"${tournamentBuilder.name}\"" )
            success = Command.SINGLE_SUCCESS
        }

        val player = ctx.source.player
        if ( player != null ) {
            player.displayClientMessage( text ,false )
            if ( registeredPlayer != null && registeredPlayer != player && tournamentBuilder != null ) {
                ChatUtil.displayInPlayerChat(
                    player = registeredPlayer,
                    text   = "You were successfully REGISTERED with Tournament Builder \"${tournamentBuilder.name}\"!" )
            }
        } else {
            Util.report( text.string )
        }
        return success
    }

}
