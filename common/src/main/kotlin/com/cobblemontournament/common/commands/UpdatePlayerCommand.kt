package com.cobblemontournament.common.commands

import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.commands.suggestions.ActorTypeSuggestionProvider
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderPlayersNode
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.commands.util.NodeKeys.ACTOR_TYPE
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.NEW
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_ENTITY
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_SEED
import com.cobblemontournament.common.commands.util.NodeKeys.SEED
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.util.NodeKeys.UPDATE
import com.cobblemontournament.common.player.properties.MutablePlayerProperties
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.TournamentUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object UpdatePlayerCommand
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [BUILDER_NAME] -> [PLAYER] ->
     * [UPDATE] -> [PLAYER_NAME] -> [updatePlayer]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [BUILDER]           ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [PLAYER]            ->
     *      literal     [UPDATE]            ->
     *      argument    [PLAYER_NAME] , StringType ->
     *      * arguments
     *      method      [updatePlayer]
     *
     *      * - optional
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
                Commands.literal(UPDATE)
                    .then(Commands.argument(PLAYER_NAME, StringArgumentType.string())
                        .suggests { ctx, builder ->
                            PlayerNameSuggestionProvider().getSuggestions(ctx,builder)
                        }
                        .then(Commands.literal(ACTOR_TYPE)
                            .then( Commands.argument("$NEW$ACTOR_TYPE",StringArgumentType.string())
                                .suggests(ActorTypeSuggestionProvider())
                                .executes { ctx -> updatePlayer( ctx = ctx) }
                            ))
                        .then(Commands.literal(SEED)
                            .then(Commands.argument("$NEW$PLAYER_SEED", IntegerArgumentType.integer(-1))
                                .executes { ctx -> updatePlayer( ctx = ctx) }
                            ))
                    ))
        )
    }

    @JvmStatic
    fun updatePlayer(
        ctx : CommandContext<CommandSourceStack>
    ): Int
    {
        var player : ServerPlayer? = null
        var tournamentBuilder: TournamentBuilder? = null
        var playerProperties: MutablePlayerProperties? = null

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input)
        for (entry in nodeEntries) {
            when (entry.key) {
                BUILDER_NAME -> {
                    val (builder, _) = TournamentManager.getTournamentBuilderByName(entry.value)
                    tournamentBuilder = builder
                }
                PLAYER_ENTITY -> {
                    player = EntityArgument.getPlayer( ctx, entry.key)
                    playerProperties = tournamentBuilder?.getMutableSeededPlayers()?.firstOrNull { it.playerID == player.uuid }
                }
                "$NEW$PLAYER_SEED" -> playerProperties?.seed = Integer.parseInt( entry.value)
                "$NEW$ACTOR_TYPE" -> {
                    playerProperties?.actorType = TournamentUtil.getActorTypeOrNull( entry.value)?: continue
                }
            }
        }

        if (tournamentBuilder == null) {
            Util.report("Failed to UPDATE Player b/c Tournament Builder was null")
            return 0
        } else if (playerProperties == null || player == null) {
            Util.report("Failed to UPDATE Player with ${tournamentBuilder.name} b/c Player Properties were null")
            return 0
        } else {
            Util.report("Successfully UPDATED Player ${playerProperties.name} with Tournament Builder ${tournamentBuilder.name}.")
            return Command.SINGLE_SUCCESS
        }
    }

}