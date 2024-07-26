package com.cobblemontournament.common.commands.testing.player

import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.commands.builder.UnregisterPlayerCommand.unregisterPlayer
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]-[PLAYER]-([UNREGISTER]+[FAKE])
 */
object UnregisterFakePlayerCommand {

    private val execution: ExecutionNode by lazy { ExecutionNode { unregisterFakePlayer(it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher
            .register(ActiveBuilderPlayerNode
                .nest(Commands
                    .literal(("$UNREGISTER-$FAKE"))
                    .executes { ctx -> unregisterPlayer(ctx = ctx) }
                    .then(Commands
                        .argument(PLAYER_NAME, StringArgumentType.string())
                        .suggests(PlayerNameSuggestionProvider())
                        .executes(this.execution.action)
                    )
                )
            )
    }

    private fun unregisterFakePlayer(ctx: CommandContext): Int {
        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(
            storeID = TournamentStoreManager.ACTIVE_STORE_ID
        ) ?: return 0

        val playerName = ctx.getNodeInputRangeOrDisplayFail(
            nodeName = PLAYER_NAME
        ) ?: return 0

        if (!tournamentBuilder.containsPlayer(playerName)) {
            ctx.source.player.displayCommandFail(
                reason = "Tournament builder did not contain name."
            )
            return 0
        } else if (!tournamentBuilder.removePlayer(playerName)) {
            ctx.source.player.displayCommandFail(
                reason = "Tournament builder failed to remove player."
            )
            return 0
        }

        ctx.source.player.displayCommandSuccess(
            text = "Unregistered fake player from: \"${tournamentBuilder.name}\"."
        )

        return Command.SINGLE_SUCCESS
    }

}
