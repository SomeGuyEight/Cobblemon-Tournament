package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.cobblemonchallenge.ChallengeFormat
import com.cobblemontournament.common.api.storage.TournamentStoreManager
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.util.getTournamentBuilder
import com.cobblemontournament.common.tournament.TournamentType
import com.mojang.brigadier.*
import com.mojang.brigadier.arguments.*
import com.sg8.api.command.*
import com.sg8.api.command.node.ExecutionNode
import com.sg8.api.command.node.RecursiveNodeGenerator
import com.sg8.api.command.node.nested.*
import com.sg8.util.*
import net.minecraft.commands.*

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]-[UPDATE]
 *
 * calls [updateBuilderProperties]
 */
object UpdateBuilderCommand {

    val executionNode = ExecutionNode { updateBuilderProperties(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {

        val rootNode = LiteralNestedNode(UPDATE, ActiveBuilderNameNode)
        val nodeLists = mutableListOf<List<NestedNode>>()

        nodeLists.add(listOf(
            LiteralNestedNode(NAME, rootNode),
            RequiredNestedNode(
                nodeKey = "$NEW$BUILDER_NAME",
                parentNode = rootNode,
                argumentType = StringArgumentType.string(),
            )
        ))

        nodeLists.add(listOf(
            LiteralNestedNode(SHOW_PREVIEW, rootNode),
            RequiredNestedNode(
                nodeKey = "$NEW$SHOW_PREVIEW",
                parentNode = rootNode,
                argumentType = BoolArgumentType.bool(),
            )
        ))

        nodeLists.add(listOf(
            LiteralNestedNode(MAX_PARTICIPANTS, rootNode),
            RequiredNestedNode(
                nodeKey = "$NEW$MAX_PARTICIPANTS",
                parentNode = rootNode,
                argumentType = IntegerArgumentType.integer(),
            )
        ))

        nodeLists.add(listOf(
            LiteralNestedNode(LEVEL, rootNode),
            RequiredNestedNode(
                nodeKey = "$NEW$LEVEL",
                parentNode = rootNode,
                argumentType = IntegerArgumentType.integer(),
            )
        ))

//        nodeLists.add(listOf(
//            LiteralNestedNode(TOURNAMENT_TYPE, rootNode),
//            RequiredNestedNode(
//                nodeKey = "$NEW$TOURNAMENT_TYPE",
//                parentNode = rootNode,
//                argumentType = StringArgumentType.string(),
//                suggestionProvider = TournamentTypeSuggestionProvider
//            )
//        ))

//        nodeLists.add(listOf(
//            LiteralNestedNode(CHALLENGE_FORMAT, rootNode),
//            RequiredNestedNode(
//                nodeKey = "$NEW$CHALLENGE_FORMAT",
//                parentNode = rootNode,
//                argumentType = StringArgumentType.string(),
//                suggestionProvider = ChallengeFormatSuggestionProvider
//            )
//        ))

        nodeLists.add(listOf(
            LiteralNestedNode(TEAM_SIZE, rootNode),
            RequiredNestedNode(
                nodeKey = "$NEW$TEAM_SIZE",
                parentNode = rootNode,
                argumentType = IntegerArgumentType.integer(),
            )
        ))

        nodeLists.add(listOf(
            LiteralNestedNode(GROUP_SIZE, rootNode),
            RequiredNestedNode(
                nodeKey = "$NEW$GROUP_SIZE",
                parentNode = rootNode,
                argumentType = IntegerArgumentType.integer(),
            )
        ))

        nodeLists.add(listOf(
            LiteralNestedNode(LEVEL_RANGE, rootNode),
            RequiredNestedNode(
                nodeKey = "$NEW$MIN_LEVEL",
                parentNode = rootNode,
                argumentType = IntegerArgumentType.integer(),
            ),
            RequiredNestedNode(
                nodeKey = "$NEW$MAX_LEVEL",
                parentNode = rootNode,
                argumentType = IntegerArgumentType.integer(),
            )
        ))

        RecursiveNodeGenerator.registerAllPermutations(
            dispatcher = dispatcher,
            rootNode = rootNode,
            executionNode = executionNode,
            nodeLists = nodeLists,
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

// example of what a full command looks like
// they can be put in any order
// /tournament builder active testBuilder update group-size 8 level 8 level-range 8 8 max-participants 8 name NewBuilderName show-preview false team-size 8
