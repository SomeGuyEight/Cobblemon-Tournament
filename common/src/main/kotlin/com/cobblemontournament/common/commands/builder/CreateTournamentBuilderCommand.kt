package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.commands.ExecutableCommand
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.builder.CreateBuilderNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.CREATE
import com.cobblemontournament.common.commands.nodes.NodeKeys.NEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util

/**
 * [TOURNAMENT] - [BUILDER] - [CREATE]
 *
 * [NEW]+[BUILDER_NAME] -> [createNewBuilder]
 *
 *      literal     [TOURNAMENT]        ->
 *      literal     [BUILDER]           ->
 *      literal     [CREATE]            ->
 *      argument    [NEW]+[BUILDER_NAME] , StringType ->
 *      function    [createNewBuilder]
 */
object CreateTournamentBuilderCommand : ExecutableCommand
{
    override val executionNode get() = ExecutionNode { createNewBuilder( ctx = it ) }

    @JvmStatic
    fun register( dispatcher: CommandDispatcher <CommandSourceStack> )
    {
        dispatcher.register(
            CreateBuilderNode.nest(
                Commands.argument( "$NEW$BUILDER_NAME", StringArgumentType.string() )
                    .executes( this.executionNode.node )
                // TODO add more optional properties to assign
            ) )
    }

    @JvmStatic
    private fun createNewBuilder(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        var name = ""
        var exists              : Boolean?           = null
        var tournamentBuilder   : TournamentBuilder? = null

        val nodeEntries = CommandUtil.getNodeEntries( ctx )
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                // TODO implement function to append an index to builders with the same name
                //      - ?? implement inside store manager ??
                //      - ex. parameter "allowDuplicateNames": Boolean
                //      - !! maintain reference through registration & apply new name inside of store manager
                // keep in switch b/c will add setting properties simultaneously in the future
                "$NEW$BUILDER_NAME" -> {
                    name = entry.value
                    tournamentBuilder = TournamentStoreManager.getInstanceByName(
                        name        = entry.value,
                        storeClass  = TournamentBuilderStore::class.java,
                        storeID     = TournamentStoreManager.activeStoreKey
                    ).first ?: TournamentStoreManager.getInstanceByName(
                        name        = entry.value,
                        storeClass  = TournamentBuilderStore::class.java,
                        storeID     = TournamentStoreManager.inactiveStoreKey
                    ).first

                    val finalName = if ( tournamentBuilder == null ) {
                        entry.value
                    } else {
                        val value = TournamentStoreManager.getNameWithIndex( currentInstance = tournamentBuilder )
                        if ( value == null ) {
                            exists = true
                            continue
                        } else value
                    }

                    tournamentBuilder = TournamentBuilder().initialize()
                    tournamentBuilder.name = finalName
                    val success = TournamentStoreManager.addInstance(
                        storeClass  = TournamentBuilderStore::class.java,
                        storeID     = TournamentStoreManager.activeStoreKey,
                        instance    = tournamentBuilder
                    ).first

                    if ( !success ) {
                        tournamentBuilder = null
                    }
                }
            }
        }

        var success = 0
        val text: MutableComponent
        if ( exists == true) {
            text = CommandUtil.failedCommand( "A builder named \"$name\" already exists." )
        } else if ( tournamentBuilder == null ) {
            text = CommandUtil.failedCommand( "Tournament Builder was null" )
        } else {
            text = CommandUtil.successfulCommand( "CREATED Tournament Builder \"$name\"" )
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
