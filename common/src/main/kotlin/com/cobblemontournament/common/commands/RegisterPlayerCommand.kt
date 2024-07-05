package com.cobblemontournament.common.commands

import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.commands.suggestions.ActorTypeSuggestionProvider
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderPlayersNode
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.commands.util.NodeKeys.ACTOR_TYPE
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_ENTITY
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_SEED
import com.cobblemontournament.common.commands.util.NodeKeys.REGISTER
import com.cobblemontournament.common.commands.util.NodeKeys.SEED
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.util.NodeKeys.TYPE
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

object RegisterPlayerCommand
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [BUILDER_NAME] -> [PLAYER] -> [PLAYER_ENTITY] -> [registerPlayer]
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
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registry: CommandBuildContext,
        selection: CommandSelection
    )
    {
        dispatcher.register(
            ActiveBuilderPlayersNode.player(
                Commands.literal(REGISTER)
                    .then(Commands.argument( PLAYER_ENTITY, EntityArgument.player())
                        .executes { ctx: CommandContext<CommandSourceStack> ->
                            registerPlayer( ctx = ctx)
                        }
                        .then(Commands.literal(TYPE)
                            .then( Commands.argument(ACTOR_TYPE, StringArgumentType.string())
                                .suggests(ActorTypeSuggestionProvider())
                                .executes { ctx -> registerPlayer( ctx = ctx) }
                            ))
                        .then(Commands.literal(SEED)
                            .then(Commands.argument(PLAYER_SEED, IntegerArgumentType.integer(-1))
                                .executes { ctx -> registerPlayer( ctx = ctx) }
                            ))
                        )))
    }

    @JvmStatic
    fun registerPlayer(
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
                    val ( builder, _ ) = TournamentManager.getTournamentBuilderByName( entry.value)
                    tournamentBuilder = builder
                }
                PLAYER_ENTITY -> {
                    player = EntityArgument.getPlayer( ctx, entry.key)
                    val success = tournamentBuilder?.addPlayer(
                        playerID = player.uuid,
                        playerName = player.name.toString()
                    )
                    if (success == true) {
                        playerProperties = tournamentBuilder!!.getPlayer(playerID = player.uuid)
                    }
                }
                PLAYER_SEED -> playerProperties?.seed = Integer.parseInt( entry.value)
                ACTOR_TYPE -> {
                    playerProperties?.actorType = TournamentUtil.getActorTypeOrNull( entry.value)?: continue
                }
            }
        }

        if (tournamentBuilder == null) {
            Util.report("Failed to REGISTER Player b/c Tournament Builder was null")
            return 0
        } else if (playerProperties == null || player == null) {
            Util.report("Failed to REGISTER Player with ${tournamentBuilder.name} b/c Player Properties were null")
            return 0
        } else {
            // FUTURE TODO implement method to handle NPC vs ServerPlayer
            Util.report("Successfully REGISTERED Player ${playerProperties.name} with Tournament Builder ${tournamentBuilder.name}.")
            return Command.SINGLE_SUCCESS
        }
    }

}
