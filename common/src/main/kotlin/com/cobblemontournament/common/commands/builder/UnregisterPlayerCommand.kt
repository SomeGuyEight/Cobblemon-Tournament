package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[BUILDER]-[BUILDER_NAME]-[PLAYER]-[UNREGISTER]
 */
object UnregisterPlayerCommand {

    val executionNode by lazy { ExecutionNode { unregisterPlayer(ctx = it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher
            .register(ActiveBuilderPlayerNode
                .nest(Commands
                    .literal(UNREGISTER)
                    .then(Commands
                        .argument(PLAYER_NAME, StringArgumentType.string())
                        .suggests(PlayerNameSuggestionProvider())
                        .executes(this.executionNode.action)
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
            ?: let { _ ->
                player.displayCommandFail(reason = "No valid player found.")
                return 0
            }

        val playerName = properties.name
        val builderName = tournamentBuilder.name

        if (!tournamentBuilder.removePlayer(properties.playerID)) {
            player.displayCommandFail(
                reason = "Failed inside of builder when unregistering $playerName."
            )
            return 0
        }

        CobblemonTournament
            .getServerPlayer(properties.playerID)
            ?.displayInChat(
                text = "You were successfully unregistered from " +
                        "Tournament Builder \"$builderName\"!",
            )

        player.displayCommandSuccess(text = "Unregistered $playerName from \"$builderName\"")

        return Command.SINGLE_SUCCESS
    }

}
