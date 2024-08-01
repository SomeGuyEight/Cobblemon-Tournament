package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.sg8.api.command.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.util.getTournamentBuilder
import com.cobblemontournament.common.tournament.TournamentType
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.sg8.api.command.getNodeInputRange
import com.sg8.api.command.node.ExecutionNode
import com.sg8.util.displayCommandFail
import com.sg8.util.displayCommandSuccess
import com.sg8.util.getConstantOrNull
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]-[UPDATE]
 *
 * calls [updateBuilderProperties]
 */
object UpdateBuilderCommand {

    val executionNode = ExecutionNode { updateBuilderProperties(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(ActiveBuilderNameNode
            .nest(Commands
                .literal(UPDATE)
                .then(Commands
                    .literal(NAME)
                    .then(Commands
                        .argument("$NEW$BUILDER_NAME", StringArgumentType.string())
                        .executes(this.executionNode.handler)
                    )
                )
//                .then(Commands
//                    .literal(TOURNAMENT_TYPE)
//                    .then(Commands
//                        .argument("$NEW$TOURNAMENT_TYPE", StringArgumentType.string())
//                        .suggests(TournamentTypeSuggestionProvider())
//                        .executes(this.executionNode.action)
//                    )
//                )
//                .then( Commands
//                    .literal(CHALLENGE_FORMAT)
//                    .then(Commands
//                        .argument("$NEW$CHALLENGE_FORMAT", StringArgumentType.string() )
//                        .suggests(ChallengeFormatSuggestionProvider())
//                        .executes(this.executionNode.action)
//                    )
//                )
                .then(Commands
                    .literal(MAX_PARTICIPANTS)
                    .then(Commands
                        .argument("$NEW$MAX_PARTICIPANTS", IntegerArgumentType.integer())
                        .executes(this.executionNode.handler)
                    )
                )
//                .then(Commands
//                    .literal(TEAM_SIZE)
//                    .then(Commands
//                        .argument("$NEW$TEAM_SIZE", IntegerArgumentType.integer())
//                        .executes(this.executionNode.action)
//                    )
//                )
//                .then(Commands
//                    .literal(GROUP_SIZE)
//                    .then(Commands
//                        .argument("$NEW$GROUP_SIZE", IntegerArgumentType.integer())
//                        .executes(this.executionNode.action)
//                    )
//                )
                .then(Commands
                    .literal(LEVEL)
                    .then(Commands
                        .argument("$NEW$LEVEL", IntegerArgumentType.integer())
                        .executes(this.executionNode.handler)
                    )
                )
//                .then(Commands
//                    .literal(LEVEL_RANGE)
//                    .then(Commands
//                        .argument("$NEW$MIN_LEVEL", IntegerArgumentType.integer())
//                        .then(Commands
//                            .argument("$NEW$MAX_LEVEL", IntegerArgumentType.integer())
//                            .executes(this.executionNode.action)
//                        )
//                    )
//                )
                .then(Commands
                    .literal(SHOW_PREVIEW)
                    .then(Commands
                        .argument("$NEW$SHOW_PREVIEW", BoolArgumentType.bool())
                        .executes(this.executionNode.handler)
                    )
                )
            )
        )
    }

    private fun updateBuilderProperties(ctx: CommandContext): Int {
        val player = ctx.source.player

        val tournamentBuilder = ctx
            .getTournamentBuilder(TournamentStoreManager.INACTIVE_STORE_ID)
            ?: run {
                player.displayCommandFail(reason = "Tournament Builder was null")
                return 0
            }

        ctx.getNodeInputRange(nodeName = "$NEW$BUILDER_NAME")?.let { tournamentBuilder.name = it }

        ctx.getNodeInputRange(nodeName = "$NEW$TOURNAMENT_TYPE")
            ?.getConstantOrNull<TournamentType>()
            ?.let { tournamentBuilder.tournamentType = it }

        ctx.getNodeInputRange(nodeName = "$NEW$CHALLENGE_FORMAT")
            ?.getConstantOrNull<ChallengeFormat>()
            ?.let { tournamentBuilder.challengeFormat = it }

        ctx.getNodeInputRange(nodeName = "$NEW$MAX_PARTICIPANTS")
            ?.let { tournamentBuilder.maxParticipants = Integer.parseInt(it) }

        ctx.getNodeInputRange(nodeName = "$NEW$TEAM_SIZE")
            ?.let { tournamentBuilder.teamSize = Integer.parseInt(it) }

        ctx.getNodeInputRange(nodeName = "$NEW$GROUP_SIZE")
            ?.let { tournamentBuilder.groupSize = Integer.parseInt(it) }

        ctx.getNodeInputRange(nodeName = "$NEW$MIN_LEVEL")
            ?.let { tournamentBuilder.minLevel = Integer.parseInt(it) }

        ctx.getNodeInputRange(nodeName = "$NEW$MAX_LEVEL")
            ?.let { tournamentBuilder.maxLevel = Integer.parseInt(it) }

        ctx.getNodeInputRange(nodeName = "$NEW$LEVEL")
            ?.let { Integer.parseInt(it) }
            ?.let { level ->
                tournamentBuilder.minLevel = level
                tournamentBuilder.maxLevel = level
            }

        ctx.getNodeInputRange(nodeName = "$NEW$SHOW_PREVIEW")
            ?.toBooleanStrictOrNull()
            ?.let { tournamentBuilder.showPreview = it }

        player.displayCommandSuccess(
            text = "UPDATED Tournament Builder \"${tournamentBuilder.name}\"",
        )

        return Command.SINGLE_SUCCESS
    }
}
