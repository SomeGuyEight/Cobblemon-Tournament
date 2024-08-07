package com.cobblemontournament.common.commands.builder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.CobblemonTournament
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.commands.util.getTournamentBuilderOrDisplayFail
import com.mojang.brigadier.*
import com.mojang.brigadier.arguments.*
import com.sg8.api.command.*
import com.sg8.api.command.node.*
import com.sg8.util.*
import net.minecraft.commands.*

/**
 * [TOURNAMENT]-[BUILDER]-[BUILDER_NAME]-[PLAYER]-[UPDATE]-[PLAYER_NAME]
 *
 * calls [updatePlayer]
 */
object UpdatePlayerCommand {

    val executionNode = ExecutionNode { updatePlayer(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(ActiveBuilderPlayerNode
            .nest(Commands
                .literal(UPDATE)
                .then(Commands
                    .argument(NodeKeys.PLAYER_NAME, StringArgumentType.string())
                    .suggests(PlayerNameSuggestionProvider())
//                    .then(Commands
//                        .literal(ACTOR_TYPE)
//                        .then(Commands
//                            .argument("$NEW$ACTOR_TYPE", StringArgumentType.string())
//                            .suggests(ActorTypeSuggestionProvider())
//                            .executes(this.executionNode.handler)
//                        )
//                    )
                    .then(Commands
                        .literal(SEED)
                        .then(Commands
                            .argument("$NEW$PLAYER_SEED", IntegerArgumentType.integer(-1))
                            .executes(this.executionNode.handler)
                        )
                    )
                )
            )
        )
    }

    private fun updatePlayer(ctx: CommandContext): Int {
        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(
            storeID = TournamentStoreManager.ACTIVE_STORE_ID,
        ) ?: return 0

        val playerProperties = ctx
            .getNodeInputRange(NodeKeys.PLAYER_NAME)
            ?.let { tournamentBuilder.getPlayer(it) }
            ?: run {
                ctx.source.player.displayCommandFail(reason = "Player properties were null")
                return 0
            }

        val seedUpdated: Boolean = ctx
            .getNodeInputRange(nodeName = "$NEW$PLAYER_SEED")
            ?.let { Integer.parseInt(it) }
            ?.let { seed ->
                playerProperties.seed = seed
                playerProperties.originalSeed = seed
                true
            }
            ?: false

        val actorTypeUpdated: Boolean = ctx
            .getNodeInputRange(nodeName = "$NEW$ACTOR_TYPE")
            ?.getConstantOrNull<ActorType>()
            ?.let { type ->
                playerProperties.actorType = type
                true
            }
            ?: false

        if (!seedUpdated && !actorTypeUpdated) {
            ctx.source.player.displayCommandFail(
                reason = "Failed inside of tournament builder \"${tournamentBuilder.name}\""
            )
            return 0
        }

        CobblemonTournament.getServerPlayer(playerProperties.uuid)
            ?.let { updatedPlayer ->
                if (updatedPlayer != ctx.source.player) {
                    updatedPlayer.displayInChat(
                        text = "Your properties were updated " +
                                "in tournament builder \"${tournamentBuilder.name}\"!",
                    )
                }
            }

        ctx.source.player.displayCommandSuccess(
            text = "Updated ${playerProperties.name} properties " +
                    "in tournament builder \"${tournamentBuilder.name}\"",
        )

        return Command.SINGLE_SUCCESS
    }
}
