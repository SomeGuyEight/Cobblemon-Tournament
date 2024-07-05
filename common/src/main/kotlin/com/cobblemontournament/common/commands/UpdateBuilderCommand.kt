package com.cobblemontournament.common.commands

import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderNode
import com.cobblemontournament.common.commands.suggestions.ChallengeFormatSuggestionProvider
import com.cobblemontournament.common.commands.suggestions.TournamentTypeSuggestionProvider
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.commands.util.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.CHALLENGE_FORMAT
import com.cobblemontournament.common.commands.util.NodeKeys.GROUP_SIZE
import com.cobblemontournament.common.commands.util.NodeKeys.LEVEL
import com.cobblemontournament.common.commands.util.NodeKeys.MAX_LEVEL
import com.cobblemontournament.common.commands.util.NodeKeys.MAX_PARTICIPANTS
import com.cobblemontournament.common.commands.util.NodeKeys.MIN_LEVEL
import com.cobblemontournament.common.commands.util.NodeKeys.NEW
import com.cobblemontournament.common.commands.util.NodeKeys.SHOW_PREVIEW
import com.cobblemontournament.common.commands.util.NodeKeys.TEAM_SIZE
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT_TYPE
import com.cobblemontournament.common.commands.util.NodeKeys.UPDATE
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.TournamentUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import org.slf4j.helpers.Util

object UpdateBuilderCommand
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [ACTIVE] -> [BUILDER_NAME]
     *
     * -> [UPDATE] -> [updateBuilderProperties]
     *
     *      literal     [TOURNAMENT]    ->
     *      literal     [BUILDER]       ->
     *      literal     [ACTIVE]        ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [UPDATE]        ->
     *      * arguments
     *      function    [updateBuilderProperties]
     *
     *      * - optional
     */
    @JvmStatic
    @Suppress("DuplicatedCode")
    fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registry: CommandBuildContext,
        selection: CommandSelection
    )
    {
        dispatcher.register(
            ActiveBuilderNode.activeBuilder(
                Commands.literal( UPDATE)
                    .then(Commands.literal(BUILDER_NAME)
                        .then( Commands.argument("$NEW$BUILDER_NAME",StringArgumentType.string())
                            .executes { ctx -> updateBuilderProperties( ctx = ctx) }
                        ))
                    .then(Commands.literal(TOURNAMENT_NAME)
                        .then( Commands.argument("$NEW$TOURNAMENT_NAME",StringArgumentType.string())
                            .executes { ctx -> updateBuilderProperties( ctx = ctx) }
                        ))
                    .then(Commands.literal(TOURNAMENT_TYPE)
                        .then( Commands.argument("$NEW$TOURNAMENT_TYPE",StringArgumentType.string())
                            .suggests(TournamentTypeSuggestionProvider())
                            .executes { ctx -> updateBuilderProperties( ctx = ctx) }
                        ))
                    .then(Commands.literal(CHALLENGE_FORMAT)
                        .then( Commands.argument("$NEW$CHALLENGE_FORMAT",StringArgumentType.string())
                            .suggests(ChallengeFormatSuggestionProvider())
                            .executes { ctx -> updateBuilderProperties( ctx = ctx) }
                        ))
                    .then(Commands.literal(MAX_PARTICIPANTS)
                        .then(Commands.argument("$NEW$MAX_PARTICIPANTS", IntegerArgumentType.integer())
                            .executes { ctx -> updateBuilderProperties( ctx = ctx) }
                        ))
                    .then(Commands.literal(TEAM_SIZE)
                        .then(Commands.argument("$NEW$TEAM_SIZE", IntegerArgumentType.integer())
                            .executes { ctx -> updateBuilderProperties( ctx = ctx) }
                        ))
                    .then(Commands.literal(GROUP_SIZE)
                        .then(Commands.argument("$NEW$GROUP_SIZE", IntegerArgumentType.integer())
                            .executes { ctx -> updateBuilderProperties( ctx = ctx) }
                        ))
                    .then(Commands.literal(LEVEL)
                        .then(Commands.argument("$NEW$MIN_LEVEL", IntegerArgumentType.integer())
                            .then(Commands.argument("$NEW$MAX_LEVEL", IntegerArgumentType.integer())
                                .executes { ctx -> updateBuilderProperties( ctx = ctx) }
                            )))
                    .then(Commands.literal(SHOW_PREVIEW)
                        .then(Commands.argument("$NEW$SHOW_PREVIEW", BoolArgumentType.bool())
                            .executes { ctx -> updateBuilderProperties( ctx = ctx) }
                        ))
        ))
    }

    @JvmStatic
    @Suppress("DuplicatedCode")
    private fun updateBuilderProperties(
        ctx: CommandContext<CommandSourceStack>
    ): Int
    {
        var tournamentBuilder: TournamentBuilder? = null
        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input)
        for (entry in nodeEntries) {
            when (entry.key) {
                BUILDER_NAME -> {
                    // skipping to stop override when updating builder name, b/c BUILDER_NAME is also a literal argument
                    if (tournamentBuilder != null) continue
                    val (builder, _) = TournamentManager.getTournamentBuilderByName(entry.value)
                    tournamentBuilder = builder
                }
                "$NEW$BUILDER_NAME"     -> tournamentBuilder?.name = entry.value
                "$NEW$TOURNAMENT_NAME"  -> tournamentBuilder?.tournamentName = entry.value
                "$NEW$TOURNAMENT_TYPE"  -> {
                    tournamentBuilder?.tournamentType = TournamentUtil.getTournamentTypeOrNull( entry.value)?: continue
                }
                "$NEW$CHALLENGE_FORMAT" -> {
                    tournamentBuilder?.challengeFormat = TournamentUtil.getChallengeFormatOrNull( entry.value)?: continue
                }
                "$NEW$MAX_PARTICIPANTS" -> tournamentBuilder?.maxParticipants = Integer.parseInt( entry.value)
                "$NEW$TEAM_SIZE"   -> tournamentBuilder?.teamSize  = Integer.parseInt( entry.value)
                "$NEW$GROUP_SIZE"  -> tournamentBuilder?.groupSize = Integer.parseInt( entry.value)
                "$NEW$MIN_LEVEL"   -> tournamentBuilder?.minLevel  = Integer.parseInt( entry.value)
                "$NEW$MAX_LEVEL"   -> tournamentBuilder?.maxLevel  = Integer.parseInt( entry.value)
                "$NEW$SHOW_PREVIEW" -> {
                    val showPreview = entry.value.toBooleanStrictOrNull()
                    if (showPreview != null) {
                        tournamentBuilder?.showPreview = showPreview
                    } // else // TODO maybe log if reaches here ?
                }
            }
        }

        if (tournamentBuilder == null) {
            Util.report("Failed to UPDATE Tournament Builder b/c Tournament Builder was null")
            return 0
        } else {
            Util.report("Successfully UPDATED Tournament Builder ${tournamentBuilder.name}.")
            return Command.SINGLE_SUCCESS
        }
    }

}
