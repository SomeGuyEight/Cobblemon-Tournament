package com.cobblemontournament.common.commands

import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderNode
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.commands.util.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.GENERATE_TOURNAMENT
import com.cobblemontournament.common.commands.util.NodeKeys.NEW
import com.cobblemontournament.common.commands.util.NodeKeys.PRINT_DEBUG
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.tournament.TournamentData
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import org.slf4j.helpers.Util

object GenerateTournamentCommand
{
    /**
     * [TOURNAMENT] -> [BUILDER] ->  [ACTIVE] -> [BUILDER_NAME] -> [GENERATE_TOURNAMENT]
     *
     * -> [NEW]+[TOURNAMENT_NAME] -> * [PRINT_DEBUG] -> [generateTournament]
     *
     *      literal     [TOURNAMENT]            ->
     *      literal     [BUILDER]               ->
     *      argument    [ACTIVE] , StringType   ->
     *      literal     [BUILDER_NAME]          ->
     *      literal     [GENERATE_TOURNAMENT]   ->
     *      argument    [NEW]+[TOURNAMENT_NAME] , StringType ->
     *      * argument  [PRINT_DEBUG] , BooleanType ->
     *      function    [generateTournament]
     *
     *      * == optional
     */
    @JvmStatic
    fun register(
        dispatcher  : CommandDispatcher<CommandSourceStack>,
        registry    : CommandBuildContext,
        selection   : CommandSelection
    )
    {
        dispatcher.register(
            ActiveBuilderNode.activeBuilder(
                Commands.literal( GENERATE_TOURNAMENT)
                    .then(Commands.argument("$NEW$TOURNAMENT_NAME", StringArgumentType.string())
                        .executes {
                            ctx -> generateTournament( ctx)
                        }
                        .then(Commands.argument(PRINT_DEBUG, BoolArgumentType.bool())
                            .executes { ctx -> generateTournament( ctx) }
                        )
                        //.then() // TODO other optional
                    ))
        )
    }

    @JvmStatic
    fun generateTournament(
        ctx : CommandContext<CommandSourceStack>,
    ): Int
    {
        var tournamentBuilder   : TournamentBuilder?    = null
        var tournamentData      : TournamentData?       = null
        var print               : Boolean?              = null

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input)
        for (entry in nodeEntries) {
            when (entry.key) {
                BUILDER_NAME -> {
                    val (builder, _) = TournamentManager.getTournamentBuilderByName(entry.value)
                    tournamentBuilder = builder
                }
                "$NEW$TOURNAMENT_NAME"  -> tournamentData = tournamentBuilder?.toTournament( entry.value, save = true)
                PRINT_DEBUG             -> print = entry.value.toBooleanStrictOrNull()
            }
        }

        if (tournamentBuilder == null) {
            Util.report("Failed to GENERATE Tournament b/c Tournament Builder was null")
            return 0
        } else if (tournamentData == null) {
            Util.report("Failed to GENERATE Tournament b/c Tournament Data was null")
            return 0
        } else {
            if (print == true) {
                tournamentData.printTournamentDataDebug()
            }
            Util.report("Successfully GENERATED Tournament \"${tournamentData.tournament.name}\" from \"${tournamentBuilder.name}\"")
            return Command.SINGLE_SUCCESS
        }
    }

}