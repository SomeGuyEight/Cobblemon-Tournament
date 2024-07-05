package com.cobblemontournament.common.commands

import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderPlayersNode
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.EXECUTE
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_ENTITY
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.util.NodeKeys.UNREGISTER
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object UnregisterPlayerCommand
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [BUILDER_NAME] -> [PLAYER]
     *
     * -> [UNREGISTER] -> [PLAYER_NAME] -> [unregisterPlayer]
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
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registry: CommandBuildContext,
        selection: CommandSelection
    )
    {
        dispatcher.register(
            ActiveBuilderPlayersNode.player(
                Commands.literal(UNREGISTER)
                    .then(Commands.argument(PLAYER_NAME, StringArgumentType.string())
                        .suggests { ctx, builder ->
                            PlayerNameSuggestionProvider().getSuggestions(ctx,builder)
                        }
                        .then(Commands.literal(EXECUTE)
                            .executes { ctx -> unregisterPlayer( ctx) }
                        )))
        )
    }

    @JvmStatic
    fun unregisterPlayer(
        ctx : CommandContext<CommandSourceStack>
    ): Int
    {
        var player : ServerPlayer? = null
        var tournamentBuilder: TournamentBuilder? = null

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input)
        for (entry in nodeEntries) {
            when (entry.key) {
                BUILDER_NAME -> {
                    val (builder, _) = TournamentManager.getTournamentBuilderByName(entry.value)
                    tournamentBuilder = builder
                }
                PLAYER_ENTITY ->  {
                    player = EntityArgument.getPlayer( ctx, entry.key)
                    tournamentBuilder?.removePlayer(playerID = player.uuid)
                }
            }
        }

        if (tournamentBuilder == null) {
            Util.report("Failed to UNREGISTER Player b/c Tournament Builder was null")
            return 0
        } else if (player == null) {
            Util.report("Failed to UNREGISTER Player with ${tournamentBuilder.name} b/c Player Properties were null")
            return 0
        } else {
            Util.report("Successfully UNREGISTERED Player ${player.name} with Tournament Builder ${tournamentBuilder.name}.")
            return Command.SINGLE_SUCCESS
        }
    }

}