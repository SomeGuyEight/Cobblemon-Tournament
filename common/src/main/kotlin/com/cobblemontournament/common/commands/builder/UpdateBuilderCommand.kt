package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTIVE
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.CHALLENGE_FORMAT
import com.cobblemontournament.common.commands.nodes.NodeKeys.GROUP_SIZE
import com.cobblemontournament.common.commands.nodes.NodeKeys.LEVEL
import com.cobblemontournament.common.commands.nodes.NodeKeys.MAX_LEVEL
import com.cobblemontournament.common.commands.nodes.NodeKeys.MAX_PARTICIPANTS
import com.cobblemontournament.common.commands.nodes.NodeKeys.MIN_LEVEL
import com.cobblemontournament.common.commands.nodes.NodeKeys.NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.NEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.SHOW_PREVIEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.TEAM_SIZE
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_TYPE
import com.cobblemontournament.common.commands.nodes.NodeKeys.UPDATE
import com.cobblemontournament.common.commands.nodes.builder.ActiveBuilderNameNode
import com.cobblemontournament.common.util.TournamentUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util

/**
 * [TOURNAMENT] - [BUILDER] - [ACTIVE] - [BUILDER_NAME]
 *
 * [UPDATE] - * arguments -> [updateBuilderProperties]
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
object UpdateBuilderCommand : ExecutableCommand
{
    override val executionNode get() = ExecutionNode { updateBuilderProperties( ctx = it ) }

    @JvmStatic
    fun register( dispatcher: CommandDispatcher <CommandSourceStack> )
    {
        dispatcher.register(
            ActiveBuilderNameNode.nest(
                Commands.literal( UPDATE )
                    .then( Commands.literal( NAME )
                        .then( Commands.argument( "$NEW$BUILDER_NAME", StringArgumentType.string() )
                            .executes {
                                ctx -> updateBuilderProperties( ctx = ctx )
                            } ) )
//                    .then(Commands.literal(TOURNAMENT_TYPE)
//                        .then( Commands.argument( "$NEW$TOURNAMENT_TYPE", StringArgumentType.string() )
//                            .suggests( TournamentTypeSuggestionProvider() )
//                            .executes {
//                                ctx -> updateBuilderProperties( ctx = ctx )
//                            } ) )
//                    .then( Commands.literal( CHALLENGE_FORMAT )
//                        .then( Commands.argument( "$NEW$CHALLENGE_FORMAT", StringArgumentType.string() )
//                            .suggests( ChallengeFormatSuggestionProvider() )
//                            .executes {
//                                ctx -> updateBuilderProperties( ctx = ctx )
//                            } ) )
                    .then( Commands.literal( MAX_PARTICIPANTS )
                        .then( Commands.argument( "$NEW$MAX_PARTICIPANTS", IntegerArgumentType.integer() )
                            .executes {
                                ctx -> updateBuilderProperties( ctx = ctx )
                            } ) )
//                    .then( Commands.literal( TEAM_SIZE )
//                        .then( Commands.argument( "$NEW$TEAM_SIZE", IntegerArgumentType.integer() )
//                            .executes {
//                                ctx -> updateBuilderProperties( ctx = ctx )
//                            } ) )
//                    .then( Commands.literal( GROUP_SIZE )
//                        .then( Commands.argument( "$NEW$GROUP_SIZE", IntegerArgumentType.integer() )
//                            .executes {
//                                ctx -> updateBuilderProperties( ctx = ctx )
//                            } ) )
                    .then( Commands.literal( LEVEL )
                        .then( Commands.argument( "$NEW$LEVEL", IntegerArgumentType.integer() )
                            .executes {
                                ctx -> updateBuilderProperties( ctx = ctx )
                            }
                        ) )
//                    .then( Commands.literal( LEVEL )
//                        .then( Commands.argument( "$NEW$MIN_LEVEL", IntegerArgumentType.integer() )
//                            .then( Commands.argument( "$NEW$MAX_LEVEL", IntegerArgumentType.integer() )
//                                .executes {
//                                    ctx -> updateBuilderProperties( ctx = ctx )
//                                }
//                            ) ) )
                    .then( Commands.literal( SHOW_PREVIEW )
                        .then( Commands.argument( "$NEW$SHOW_PREVIEW", BoolArgumentType.bool() )
                            .executes {
                                ctx -> updateBuilderProperties( ctx = ctx )
                            } ) )
            ) )
    }

    @JvmStatic
    private fun updateBuilderProperties(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        val ( nodeEntries, tournamentBuilder ) = CommandUtil
            .getNodesAndTournamentBuilder(
                ctx = ctx,
                storeID = TournamentStoreManager.activeStoreKey )

        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                "$NEW$BUILDER_NAME"     -> tournamentBuilder?.name = entry.value
                "$NEW$TOURNAMENT_TYPE"  -> {
                    tournamentBuilder?.tournamentType = TournamentUtil.getTournamentTypeOrNull( entry.value )
                        ?: continue
                }
                "$NEW$CHALLENGE_FORMAT" -> {
                    tournamentBuilder?.challengeFormat = TournamentUtil.getChallengeFormatOrNull( entry.value )
                        ?: continue
                }
                "$NEW$MAX_PARTICIPANTS" -> tournamentBuilder?.maxParticipants = Integer.parseInt( entry.value )
                "$NEW$TEAM_SIZE"   -> tournamentBuilder?.teamSize  = Integer.parseInt( entry.value )
                "$NEW$GROUP_SIZE"  -> tournamentBuilder?.groupSize = Integer.parseInt( entry.value )
                "$NEW$MIN_LEVEL"   -> tournamentBuilder?.minLevel  = Integer.parseInt( entry.value )
                // "$NEW$LEVEL" is temporary until level range is released for CobblemonChallenge
                "$NEW$MAX_LEVEL", "$NEW$LEVEL" -> tournamentBuilder?.maxLevel  = Integer.parseInt( entry.value )
                "$NEW$SHOW_PREVIEW" -> {
                    val showPreview = entry.value.toBooleanStrictOrNull()
                    if ( showPreview != null ) {
                        tournamentBuilder?.showPreview = showPreview
                    } // else // TODO maybe log if reaches here ?
                }
            }
        }

        var success = 0
        val text: MutableComponent
        if ( tournamentBuilder == null ) {
            text = CommandUtil.failedCommand(
                reason = "Tournament Builder was null" )
        } else {
            text = CommandUtil.successfulCommand(
                action = "UPDATED Tournament Builder \"${tournamentBuilder.name}\"" )
            success = Command.SINGLE_SUCCESS
        }

        val player = ctx.source.player
        if ( player != null ) {
            player.displayClientMessage( text ,false )
        } else {
            Util.report( text.string )
        }
        return success
    }
}
