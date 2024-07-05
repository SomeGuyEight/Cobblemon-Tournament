package com.cobblemontournament.common.commands.testing

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderPlayersNode
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_ENTITY
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_SEED
import com.cobblemontournament.common.commands.util.NodeKeys.REGISTER
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.util.NodeKeys.FAKE
import com.cobblemontournament.common.player.properties.MutablePlayerProperties
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import org.slf4j.helpers.Util
import java.util.*

object RegisterFakePlayerCommand
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [BUILDER_NAME] -> [PLAYER] -> [PLAYER_ENTITY]
     *
     * -> [REGISTER]-[FAKE] -> [PLAYER_SEED]-> [registerFakePlayer]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [BUILDER]           ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [PLAYER]            ->
     *      argument    [PLAYER_ENTITY] , EntityType ->
     *      literal     [REGISTER]-[FAKE]   ->
     *      * argument  [PLAYER_SEED] , IntType ->
     *      method      [registerFakePlayer]
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
                Commands.literal("$REGISTER-$FAKE")
                    .executes { ctx: CommandContext<CommandSourceStack> ->
                        registerFakePlayer( ctx = ctx)
                    }
                    .then(Commands.literal(PLAYER_SEED)
                        .then(Commands.argument(PLAYER_SEED, IntegerArgumentType.integer(-1))
                            .executes { ctx -> registerFakePlayer( ctx = ctx) }
                        ))
                    ))
    }

    @JvmStatic
    fun registerFakePlayer(
        ctx : CommandContext<CommandSourceStack>
    ): Int
    {
        var tournamentBuilder: TournamentBuilder? = null
        var playerProperties: MutablePlayerProperties? = null

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input)
        for (entry in nodeEntries) {
            when (entry.key) {
                BUILDER_NAME -> {
                    val ( builder, _ ) = TournamentManager.getTournamentBuilderByName( entry.value)
                    tournamentBuilder = builder
                }
                PLAYER_ENTITY ->  {
                    // TODO better method or is fine since it is just for testing ?
                    //  - temp for testing -> just take as string for now
                    if (tournamentBuilder == null) continue
                    val id = UUID.randomUUID()
                    playerProperties = MutablePlayerProperties(
                        name            = entry.value,
                        actorType       = ActorType.NPC,
                        playerID        = id,
                        tournamentID    = tournamentBuilder.uuid
                    )
                }
                PLAYER_SEED -> playerProperties?.seed = Integer.parseInt( entry.value)
            }
        }

        if (tournamentBuilder == null) {
            Util.report("Failed to REGISTER Fake Player b/c Tournament Builder was null")
            return 0
        } else if (playerProperties == null) {
            Util.report("Failed to REGISTER Fake Player with ${tournamentBuilder.name} b/c Player Properties were null")
            return 0
        } else {
            Util.report("Successfully REGISTERED Fake Player \"${playerProperties.name}\" with Tournament Builder \"${tournamentBuilder.name}\".")
            return Command.SINGLE_SUCCESS
        }
    }

}
