package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.NodeKeys.ACTIVATE
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.HISTORY
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.nodes.builder.BuilderHistoryNameNode
import com.cobblemontournament.common.util.CommandUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util

/**
 * [TOURNAMENT] - [BUILDER] - [HISTORY]
 *
 * [BUILDER_NAME] - [ACTIVATE] -> [activate]
 *
 *      literal     [TOURNAMENT]    ->
 *      literal     [BUILDER]       ->
 *      literal     [HISTORY]       ->
 *      argument    [BUILDER_NAME] , StringType ->
 *      literal     [ACTIVATE]      ->
 *      function    [activate]
 */
object ActivateBuilderCommand : ExecutableCommand
{
    override val executionNode get() = ExecutionNode { activate( ctx = it ) }

    @JvmStatic
    fun register( dispatcher: CommandDispatcher <CommandSourceStack> )
    {
        dispatcher.register(
            BuilderHistoryNameNode.nest(
                Commands.literal( ACTIVATE )
                    .executes( this.executionNode.node )
            ) )
    }

    @JvmStatic
    private fun activate(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        val tournamentBuilder = CommandUtil.getNodesAndTournamentBuilder(
            ctx = ctx,
            storeID = TournamentStoreManager.inactiveStoreKey
        ).second

        var success = 0
        val text: MutableComponent
        if ( tournamentBuilder == null ) {
            text = CommandUtil.failedCommand( "Tournament Builder was null" )
        } else {
            TournamentStoreManager.transferInstance(
                storeClass  = TournamentBuilderStore::class.java,
                storeID     = TournamentStoreManager.inactiveStoreKey,
                newStoreID  = TournamentStoreManager.activeStoreKey,
                instance    = tournamentBuilder )
            text = CommandUtil.successfulCommand( "ACTIVATED Tournament Builder \"${tournamentBuilder.name}\"" )
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
