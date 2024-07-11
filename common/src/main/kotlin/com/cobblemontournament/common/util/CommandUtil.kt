package com.cobblemontournament.common.util

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.commands.nodes.NodeEntry
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.context.ParsedCommandNode
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

object CommandUtil
{
    @JvmStatic
    fun getNodeEntries(
        parsedNodes : List <ParsedCommandNode <*>>,
        input       : String
    ): List <NodeEntry>
    {
        val list = mutableListOf <NodeEntry>()
        for ( parsedNode in parsedNodes ) {
            val range = parsedNode.range
            val value = input.subSequence( range.start, range.end )
            list.add( NodeEntry( parsedNode.node.name, value.toString() ) )
        }
        return list
    }

    @JvmStatic
    fun getNodesAndTournament(
        ctx: CommandContext <CommandSourceStack>
    ): Pair < List <NodeEntry>, Tournament? >
    {
        val nodeEntries = getNodeEntries( ctx.nodes, ctx.input )
        val entry = nodeEntries.firstOrNull { it.key == TOURNAMENT_NAME }
        val tournament = if ( entry != null ) {
            TournamentStoreManager.getTournamentByName( entry.value ).first
        } else null
        return Pair( nodeEntries, tournament )
    }

    @JvmStatic
    fun getNodesAndTournamentBuilder(
        ctx: CommandContext <CommandSourceStack>
    ): Pair < List <NodeEntry>, TournamentBuilder? >
    {
        val nodeEntries = getNodeEntries( ctx.nodes, ctx.input )
        val entry = nodeEntries.firstOrNull { it.key == BUILDER_NAME }
        val builder = if ( entry != null ) {
            TournamentStoreManager.getTournamentBuilderByName( entry.value ).first
        } else null
        return Pair( nodeEntries, builder )
    }

    @JvmStatic
    fun displayNoArgument(
        player  : ServerPlayer?,
        nodeKey : String
    ): Int
    {
        if ( player == null ) {
            Util.report("\"$nodeKey\" command had no arguments.")
        } else {
            val text0 = ChatUtil.formatText( text = "\"$nodeKey\"  had no arguments.", ChatUtil.yellow, bold = true )
            player.displayClientMessage( text0 ,false )
        }
        return Command.SINGLE_SUCCESS
    }

    @JvmStatic
    fun createChallengeMatchInteractable(
        text        : String,
        tournament  : Tournament,
        opponent    : ServerPlayer,
        color       : String    = ChatUtil.white,
        bracketed   : Boolean   = false,
        bracketColor: String    = ChatUtil.white,
    ): MutableComponent
    {
        var commandText = "/challenge ${opponent.name.string} "
        commandText    += "minLevel ${tournament.minLevel} maxLevel ${tournament.maxLevel} "
        commandText    += "handicapP1 0 handicapP2 0" // TODO implement handicap in player properties
        commandText    +=  if ( tournament.showPreview ) "" else " nopreview"

        val interactable = ChatUtil.getInteractableCommand(
            text    = text,
            command = commandText,
            color   = color,
            bold    = true )

        if (bracketed) {
            val component = ChatUtil.formatText( text = "[", color = bracketColor)
            component.append( interactable )
            return component.append( ChatUtil.formatText( text = "]", color = bracketColor) )
        } else {
            return interactable
        }
    }

    @JvmStatic
    fun failedCommand(
        reason: String
    ): MutableComponent {
        return ChatUtil.formatText( text = "Command Failed: $reason.", ChatUtil.yellow )
    }

    @JvmStatic
    fun successfulCommand(
        action: String
    ): MutableComponent {
        return ChatUtil.formatText( text = "Command Success: $action.", ChatUtil.green )
    }

}
