package com.cobblemontournament.common.commands.builder

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.api.PlayerManager
import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderPlayersNode
import com.cobblemontournament.common.commands.suggestions.PlayerNameSuggestionProvider
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTOR_TYPE
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.NEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.PLAYER_SEED
import com.cobblemontournament.common.commands.nodes.NodeKeys.SEED
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.UPDATE
import com.cobblemontournament.common.util.ChatUtil
import com.cobblemontournament.common.util.TournamentUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util
import java.util.UUID

/**
 * [TOURNAMENT] - [BUILDER] - [BUILDER_NAME] - [PLAYER]
 * [UPDATE] - [PLAYER_NAME] - * arguments -> [updatePlayer]
 *
 *      literal     [TOURNAMENT]        ->
 *      literal     [BUILDER]           ->
 *      argument    [BUILDER_NAME] , StringType ->
 *      literal     [PLAYER]            ->
 *      literal     [UPDATE]            ->
 *      argument    [PLAYER_NAME] , StringType ->
 *      * arguments
 *      method      [updatePlayer]
 *
 *      * - optional
 */
object UpdatePlayerCommand : ExecutableCommand {

    override val executionNode get() = ExecutionNode { updatePlayer(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(ActiveBuilderPlayersNode
            .nest(Commands
                .literal(UPDATE)
                .then(Commands
                    .argument(PLAYER_NAME, StringArgumentType.string())
                    .suggests { ctx, builder ->
                        PlayerNameSuggestionProvider().getSuggestions(ctx = ctx, builder = builder)
                    }
//                    .then(Commands
//                        .literal(ACTOR_TYPE)
//                        .then(Commands
//                            .argument("$NEW$ACTOR_TYPE", StringArgumentType.string())
//                            .suggests(ActorTypeSuggestionProvider())
//                            .executes(this.executionNode.node)
//                        )
//                    )
                    .then(Commands
                        .literal(SEED)
                        .then(Commands
                            .argument("$NEW$PLAYER_SEED", IntegerArgumentType.integer(-1))
                            .executes(this.executionNode.node)
                        )
                    )
                )
            )
        )
    }

    private fun updatePlayer(ctx: CommandContext<CommandSourceStack>): Int {
        val (nodeEntries, tournamentBuilder) = CommandUtil
            .getNodesAndTournamentBuilder(
                ctx = ctx,
                storeID = TournamentStoreManager.ACTIVE_STORE_ID,
                )

        var playerID: UUID? = null
        var seed: Int? = null
        var actorType: ActorType? = null
        for (entry in nodeEntries) {
            when (entry.key) {
                PLAYER_NAME -> playerID = tournamentBuilder?.getPlayer(entry.value)?.playerID
                "$NEW$PLAYER_SEED" -> seed = Integer.parseInt( entry.value )
                "$NEW$ACTOR_TYPE" -> {
                    TournamentUtil.getActorTypeOrNull(entry.value)
                        ?.let { actorType = it }
                }
            }
        }

        val updatedPlayer = if (playerID != null) {
            PlayerManager.getServerPlayer(playerID)
        } else {
            null
        }

        var success = 0
        val text: MutableComponent = when {
            tournamentBuilder == null -> CommandUtil.failedCommand(reason = "Tournament Builder was null")
            updatedPlayer == null -> CommandUtil.failedCommand(reason = "Server Player was null")
            seed == null && actorType == null -> {
                CommandUtil.failedCommand(reason = "All properties to update were null")
            }
            else -> {
                if (tournamentBuilder.updatePlayer(updatedPlayer.uuid, actorType, seed)) {
                    success = Command.SINGLE_SUCCESS
                    CommandUtil.successfulCommand(
                        text = "UPDATED ${updatedPlayer.name.string} properties in Tournament Builder \"${tournamentBuilder.name}\"",
                    )
                } else {
                    CommandUtil.failedCommand(
                        reason = "Failed inside of Tournament Builder \"${tournamentBuilder.name}\"",
                        )
                }
            }
        }

        ctx.source.player?.let { player ->
            if (updatedPlayer != null && updatedPlayer != player && tournamentBuilder != null) {
                ChatUtil.displayInPlayerChat(
                    player = updatedPlayer,
                    text = "Your properties have been UPDATED in Tournament Builder \"${tournamentBuilder.name}\"!",
                    color  = ChatUtil.white,
                    )
            }
            player.displayClientMessage(text ,false)
        } ?: Util.report(text.string)

        return success
    }
}
