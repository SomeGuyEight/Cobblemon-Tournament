package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.commands.nodes.builder.CreateBuilderNode
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.CREATE
import com.cobblemontournament.common.commands.nodes.NodeKeys.EXECUTE
import com.cobblemontournament.common.commands.nodes.NodeKeys.NEW
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import net.minecraft.network.chat.MutableComponent
import org.slf4j.helpers.Util

object CreateTournamentBuilderCommand
{
    /**
     * [TOURNAMENT] - [BUILDER] - [CREATE] - [NEW]+[BUILDER_NAME]
     *
     * -> [createNewBuilder]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [BUILDER]           ->
     *      literal     [CREATE]            ->
     *      argument    [NEW]+[BUILDER_NAME] , StringType ->
     *      literal     [EXECUTE]           ->
     *      function    [createNewBuilder]
     */
    @JvmStatic
    fun register(
        dispatcher  : CommandDispatcher <CommandSourceStack>,
        registry    : CommandBuildContext,
        selection   : CommandSelection )
    {
        dispatcher.register(
            CreateBuilderNode.createBuilder(
                Commands.literal( EXECUTE )
                    .executes { ctx: CommandContext <CommandSourceStack> ->
                        createNewBuilder( ctx = ctx )
                    } )
        )
        // TODO add more optional properties to assign
    }

    @JvmStatic
    private fun createNewBuilder(
        ctx: CommandContext <CommandSourceStack>
    ): Int
    {
        var name = ""
        var exists              : Boolean?           = null
        var tournamentBuilder   : TournamentBuilder? = null

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input )
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                "$NEW$BUILDER_NAME" -> {
                    name = entry.value
                    val ( builder, _ ) = TournamentStoreManager.getTournamentBuilderByName( entry.value )
                    if ( builder != null ) {
                        exists = true
                    } else {
                        tournamentBuilder = TournamentBuilder()
                        tournamentBuilder.name = entry.value
                        TournamentStoreManager.addTournamentBuilder( builder = tournamentBuilder )
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
