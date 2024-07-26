package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[BUILDER]-[BUILDER_NAME]-[PLAYER]-[UPDATE]-[PLAYER_NAME]
 *
 * calls [updatePlayer]
 */
object UpdatePlayerCommand {

    val executionNode by lazy { ExecutionNode { updatePlayer(ctx = it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher
            .register(ActiveBuilderPlayerNode
                .nest(Commands
                    .literal(UPDATE)
                    .then(Commands
                        .argument(PLAYER_NAME, StringArgumentType.string())
                        .suggests(PlayerNameSuggestionProvider())
//                        .then(Commands
//                            .literal(ACTOR_TYPE)
//                            .then(Commands
//                                .argument("$NEW$ACTOR_TYPE", StringArgumentType.string())
//                                .suggests(ActorTypeSuggestionProvider())
//                                .executes(this.executionNode.handler)
//                            )
//                        )
                        .then(Commands
                            .literal(SEED)
                            .then(Commands
                                .argument("$NEW$PLAYER_SEED", IntegerArgumentType.integer(-1))
                                .executes(this.executionNode.action)
                            )
                        )
                    )
                )
            )
    }

    private fun updatePlayer(ctx: CommandContext): Int {
        val player = ctx.source.player
        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
        ) ?: return 0

        val playerProperties = ctx
            .getNodeInputRange(PLAYER_NAME)
            ?.let { tournamentBuilder.getPlayer(it) }
            ?: let { _ ->
                player.displayCommandFail(reason = "Player properties were null")
                return 0
            }

        val seedUpdated: Boolean = ctx
            .getNodeInputRange(nodeName = "$NEW$PLAYER_SEED")
            ?.let { Integer.parseInt(it) }
            ?.let { seed ->
                playerProperties.seed = seed
                playerProperties.originalSeed = seed
            }
            ?.let { true }
            ?: false

        val actorTypeUpdated: Boolean = ctx
            .getNodeInputRange(nodeName = "$NEW$ACTOR_TYPE")
            ?.let { TournamentUtil.getActorTypeOrNull(it) }
            ?.let { playerProperties.actorType = it }
            ?.let { true }
            ?: false

        val builderName = tournamentBuilder.name
        val playerName = playerProperties.name

        if (!seedUpdated && !actorTypeUpdated) {
            player.displayCommandFail(
                reason = "Failed inside of tournament builder \"$builderName\""
            )
            return 0
        }

        val updatedPlayer = CobblemonTournament.getServerPlayer(playerProperties.playerID)

        if (updatedPlayer != null && updatedPlayer != player) {
            updatedPlayer.displayInChat(
                text = "Your properties were updated in tournament builder \"$builderName\"!",
            )
        }

        player.displayCommandSuccess(
            text = "Updated $playerName properties in tournament builder \"$builderName\"",
        )

        return Command.SINGLE_SUCCESS
    }
}
