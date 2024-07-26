package com.cobblemontournament.common.commands.testing.player

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import java.util.UUID

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]-[PLAYER]-([REGISTER]+[FAKE])
 */
object RegisterFakePlayerCommand {

    private val execution: ExecutionNode by lazy { ExecutionNode { registerFakePlayer(it) } }

    fun register( dispatcher: CommandDispatcher<CommandSourceStack> ) {
        dispatcher
            .register(ActiveBuilderPlayerNode
                .nest(Commands
                    .literal("$REGISTER-$FAKE")
                    .then(Commands
                        .argument(PLAYER_NAME, StringArgumentType.string())
                        .executes(this.execution.action).then(Commands
                            .literal(PLAYER_SEED)
                            .then(Commands
                                .argument(PLAYER_SEED, IntegerArgumentType.integer((-1)))
                                .executes(this.execution.action)
                            )
                        )
                    )
                )
            )
    }

    private fun registerFakePlayer(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.source.player

        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
        ) ?: return 0

        val playerName = ctx.getNodeInputRangeOrDisplayFail(nodeName = PLAYER_NAME) ?: return 0

        if (tournamentBuilder.containsPlayer(playerName)) {
            player?.displayCommandFail(
                reason = "Tournament builder already contains player $playerName"
            )
            return 0
        }

        val seed = ctx.getNodeInputRange(nodeName = SEED)?.let { Integer.parseInt(it) }

        val added = tournamentBuilder.addPlayer(
            playerID = UUID.randomUUID(),
            playerName = playerName,
            actorType = ActorType.NPC,
            seed = seed,
        )

        if (!added) {
            player?.displayCommandFail(
                reason = "Tournament builder failed to add player \"$playerName\"."
            )
            return 0
        }

        player.displayCommandSuccess(
            text = "Registered fake player with " +
                    "tournament builder: \"${tournamentBuilder.name}\".",
        )

        return Command.SINGLE_SUCCESS
    }

}
