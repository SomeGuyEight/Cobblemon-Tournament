package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.GENERATE_TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.NEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.PRINT_DEBUG
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.api.tournament.TournamentData
import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderNameNode
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util

/**
 * [TOURNAMENT] - [BUILDER] -  [ACTIVE] - [BUILDER_NAME] - [GENERATE_TOURNAMENT]
 *
 * [NEW]+[TOURNAMENT_NAME] - [PRINT_DEBUG]* -> [generateTournament]
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
object GenerateTournamentCommand : ExecutableCommand {

    override val executionNode get() = ExecutionNode { generateTournament(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(ActiveBuilderNameNode
            .nest(Commands
                .literal(GENERATE_TOURNAMENT)
                // TODO other optional parameters
                .then(Commands
                    .argument("$NEW$TOURNAMENT_NAME", StringArgumentType.string())
                    .executes(this.executionNode.node)
                )
            )
        )
    }

    @JvmStatic
    fun generateTournament(ctx: CommandContext<CommandSourceStack>): Int {
        val (nodeEntries, tournamentBuilder) = CommandUtil
            .getNodesAndTournamentBuilder(
                ctx = ctx,
                storeID = null,
                )

        val tournamentData: TournamentData? = run {
            val entry = nodeEntries.firstOrNull { it.key == "$NEW$TOURNAMENT_NAME" }
            if (entry != null) {
                return@run tournamentBuilder?.toTournament(entry.value)
            } else {
                return@run null
            }
        }

        var success = 0
        val text: MutableComponent = when {
            tournamentBuilder == null -> CommandUtil.failedCommand(reason = "Tournament Builder was null")
            tournamentData == null -> CommandUtil.failedCommand(reason = "Tournament Data was null")
            else -> {
                success = Command.SINGLE_SUCCESS
                CommandUtil.successfulCommand(text = "GENERATED Tournament \"${tournamentData.tournament.name}\"")
            }
        }

        ctx.source.player?.displayClientMessage(text ,false)
            ?: Util.report(text.string)

        return success
    }
}
