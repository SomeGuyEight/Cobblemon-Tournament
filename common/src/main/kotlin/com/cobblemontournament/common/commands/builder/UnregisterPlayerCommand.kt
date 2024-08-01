package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.sg8.api.command.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.commands.util.getTournamentBuilderOrDisplayFail
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.sg8.api.command.getNodeInputRange
import com.sg8.api.command.node.ExecutionNode
import com.sg8.api.command.node.PLAYER
import com.sg8.api.command.node.PLAYER_NAME
import com.sg8.util.displayCommandFail
import com.sg8.util.displayCommandSuccess
import com.sg8.util.displayInChat
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[BUILDER]-[BUILDER_NAME]-[PLAYER]-[UNREGISTER]
 */
object UnregisterPlayerCommand {

    val executionNode = ExecutionNode { unregisterPlayer(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(ActiveBuilderPlayerNode
            .nest(Commands
                .literal(UNREGISTER)
                .then(Commands
                    .argument(PLAYER_NAME, StringArgumentType.string())
                    .suggests(PlayerNameSuggestionProvider())
                    .executes(this.executionNode.handler)
                )
            )
        )
    }

    fun unregisterPlayer(ctx: CommandContext): Int {
        val player = ctx.source.player

        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(
            storeID = TournamentStoreManager.INACTIVE_STORE_ID
        ) ?: return 0

        val properties = ctx
            .getNodeInputRange(PLAYER_NAME)
            ?.let { tournamentBuilder.getPlayer(it) }
            ?: run {
                player.displayCommandFail(reason = "No valid player found.")
                return 0
            }

        if (!tournamentBuilder.removePlayer(properties.uuid)) {
            player.displayCommandFail(
                reason = "Failed inside of builder when unregistering ${properties.name}."
            )
            return 0
        }

        CobblemonTournament.getServerPlayer(properties.uuid)
            ?.displayInChat(
                text = "You were successfully unregistered from " +
                        "Tournament Builder \"${tournamentBuilder.name}\"!",
            )

        player.displayCommandSuccess(
            text = "Unregistered ${properties.name} from \"${tournamentBuilder.name}\""
        )

        return Command.SINGLE_SUCCESS
    }

}
