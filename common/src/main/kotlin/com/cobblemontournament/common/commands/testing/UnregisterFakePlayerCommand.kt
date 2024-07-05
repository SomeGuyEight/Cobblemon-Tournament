package com.cobblemontournament.common.commands.testing

import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.commands.UnregisterPlayerCommand.unregisterPlayer
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderPlayersNode
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.EXECUTE
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_ENTITY
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.util.NodeKeys.UNREGISTER
import com.cobblemontournament.common.commands.util.NodeKeys.FAKE
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import org.slf4j.helpers.Util

object UnregisterFakePlayerCommand
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [BUILDER_NAME] -> [PLAYER] -> [PLAYER_ENTITY]
     *
     * -> [UNREGISTER]-[FAKE] -> [unregisterFakePlayer]
     *
     *      literal     [TOURNAMENT]            ->
     *      literal     [BUILDER]               ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [PLAYER]                ->
     *      argument    [PLAYER_ENTITY] , EntityType ->
     *      literal     [UNREGISTER]-[FAKE]     ->
     *      method      [unregisterFakePlayer]
     */
    @JvmStatic
    fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registry: CommandBuildContext,
        selection: CommandSelection)
    {
        dispatcher.register(
            ActiveBuilderPlayersNode.player(
                Commands.literal("$UNREGISTER-$FAKE")
                    .then(Commands.literal(EXECUTE)
                        .executes { ctx -> unregisterPlayer( ctx) }
                    )))
    }

    @JvmStatic
    fun unregisterFakePlayer(
        ctx : CommandContext<CommandSourceStack>
    ): Int
    {
        var tournamentBuilder   : TournamentBuilder? = null
        var removed             : Boolean?           = null

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input)
        for (entry in nodeEntries) {
            when (entry.key) {
                BUILDER_NAME -> {
                    val ( builder, _ ) = TournamentManager.getTournamentBuilderByName( entry.value)
                    tournamentBuilder = builder
                }
                PLAYER_ENTITY -> removed = tournamentBuilder?.removePlayerByName(entry.value)
            }
        }

        if (tournamentBuilder == null) {
            Util.report("Failed to UNREGISTER Fake Player b/c Tournament Builder was null")
            return 0
        } else if (removed == null || removed == false) {
            Util.report("Failed to UNREGISTER Fake Player with ${tournamentBuilder.name} b/c IDK...")
            return 0
        } else {
            Util.report("Successfully UNREGISTERED Fake Player from Tournament Builder \"${tournamentBuilder.name}\".")
            return Command.SINGLE_SUCCESS
        }
    }

}
