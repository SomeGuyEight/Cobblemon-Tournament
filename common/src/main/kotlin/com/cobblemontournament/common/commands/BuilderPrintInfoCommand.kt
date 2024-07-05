package com.cobblemontournament.common.commands

import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderInfoNode
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.commands.util.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_PROPERTIES
import com.cobblemontournament.common.commands.util.NodeKeys.INFO
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER_PROPERTIES
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import org.slf4j.helpers.Util

object BuilderPrintInfoCommand
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [ACTIVE] -> [BUILDER_NAME] -> [INFO] -> [PLAYER_PROPERTIES] or [BUILDER_PROPERTIES] -> [printInfo]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      literal     [ACTIVE]        ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [INFO]          ->
     *      literal     [PLAYER_PROPERTIES] or [BUILDER_PROPERTIES] ->
     *      function    [printInfo]
     */
    @JvmStatic
    fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registry: CommandBuildContext,
        selection: CommandSelection
    )
    {
        dispatcher.register(
            ActiveBuilderInfoNode.getInfo(
                Commands.literal(PLAYER_PROPERTIES)
                    .executes { ctx -> printInfo( ctx) }))

        dispatcher.register(
            ActiveBuilderInfoNode.getInfo(
                Commands.literal(BUILDER_PROPERTIES)
                            .executes { ctx -> printInfo( ctx) }))
    }

    @JvmStatic
    private fun printInfo(
        ctx: CommandContext<CommandSourceStack>
    ): Int
    {
        var tournamentBuilder : TournamentBuilder?  = null

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input)
        for (entry in nodeEntries) {
            when (entry.key) {
                BUILDER_NAME -> {
                    val (builder, _) = TournamentManager.getTournamentBuilderByName(entry.value)
                    tournamentBuilder = builder
                }
                PLAYER_PROPERTIES -> tournamentBuilder?.printPlayerProperties()
                BUILDER_PROPERTIES -> tournamentBuilder?.printProperties()
            }
        }

        if (tournamentBuilder == null) {
            Util.report("Failed to print PLAYER INFO b/c Tournament Builder was null")
            return 0
        } else {
            Util.report("Successfully printed PLAYER INFO for Tournament Builder \"${tournamentBuilder.name}\"")
            return Command.SINGLE_SUCCESS
        }
    }
}