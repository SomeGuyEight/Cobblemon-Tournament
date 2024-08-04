package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.util.getTournamentBuilderOrDisplayFail
import com.mojang.brigadier.*
import com.mojang.brigadier.arguments.StringArgumentType
import com.sg8.api.command.*
import com.sg8.api.command.node.ExecutionNode
import com.sg8.util.*
import net.minecraft.commands.*

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]-[GENERATE_TOURNAMENT]-([NEW]+[TOURNAMENT_NAME])
 */
object GenerateTournamentCommand {

    val executionNode = ExecutionNode { generateTournament(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(ActiveBuilderNameNode
            .nest(Commands
                .literal(GENERATE_TOURNAMENT)
                // TODO other optional parameters
                .then(Commands
                    .argument("$NEW$TOURNAMENT_NAME", StringArgumentType.string())
                    .executes(this.executionNode.handler)
                )
            )
        )
    }

    private fun generateTournament(ctx: CommandContext): Int {
        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(storeID = null)
            ?: return 0

        val name = ctx.getNodeInputRangeOrDisplayFail(nodeName = "$NEW$TOURNAMENT_NAME")
            ?: return 0

        tournamentBuilder.toTournamentAndSave(name) ?: run {
            ctx.source.player.displayCommandFail(reason = "Tournament Data was null")
            return 0
        }

        ctx.source.player.displayCommandSuccess(text = "Generated Tournament \"$name\"")

        return Command.SINGLE_SUCCESS
    }
}
