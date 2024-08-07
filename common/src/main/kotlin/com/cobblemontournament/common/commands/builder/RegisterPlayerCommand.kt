package com.cobblemontournament.common.commands.builder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.util.getTournamentBuilder
import com.mojang.brigadier.*
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.sg8.api.command.*
import com.sg8.api.command.node.*
import com.sg8.util.*
import net.minecraft.commands.*
import net.minecraft.commands.arguments.EntityArgument

/**
 * [TOURNAMENT]-[BUILDER]-[BUILDER_NAME]-[PLAYER]-[REGISTER]
 */
object RegisterPlayerCommand {

    val executionNode = ExecutionNode { registerPlayer(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(ActiveBuilderPlayerNode
            .nest(Commands
                .literal(REGISTER)
                .then(Commands
                    .argument(NodeKeys.PLAYER_ENTITY, EntityArgument.player())
                    .executes(this.executionNode.handler)
                    .then(Commands
                        .literal(SEED)
                        .then(Commands
                            .argument(PLAYER_SEED, IntegerArgumentType.integer(-1))
                            .executes(this.executionNode.handler)
                        )
                    )
                )
            )
        )
    }

    private fun registerPlayer(ctx: CommandContext): Int {
        val player = ctx.source.player

        val tournamentBuilder = ctx
            .getTournamentBuilder(TournamentStoreManager.INACTIVE_STORE_ID)
            ?: run {
                player.displayCommandFail(reason = "Tournament builder was null")
                return 0
            }

        val seed = ctx.getNodeInputRange(PLAYER_SEED)?.let { Integer.parseInt(it) }

        val newPlayer = ctx.getPlayerEntityArgumentOrDisplayFail() ?: return 0

        val builderName = tournamentBuilder.name
        val newPlayerName = newPlayer.name.string

        tournamentBuilder.getPlayer(newPlayerName)?.run {
            player.displayCommandFail(
                reason = "'$newPlayerName' already registered with $builderName"
            )
            return 0
        }

        val success = tournamentBuilder.addPlayer(
            playerID = newPlayer.uuid,
            playerName = newPlayerName,
            actorType = ActorType.PLAYER,
            seed = seed,
        )

        if (!success) {
            player.displayCommandFail(reason = "Failed inside of builder when registering player.")
            return 0
        }

        if (newPlayer != player) {
            newPlayer.displayInChat(
                text = "You were successfully registered with Tournament Builder \"$builderName\"!",
            )
        }

        player.displayCommandSuccess(
            text = "Registered $newPlayerName with Tournament Builder \"$builderName\""
        )

        return Command.SINGLE_SUCCESS
    }

}
