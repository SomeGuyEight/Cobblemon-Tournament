package com.cobblemontournament.common.util

import com.cobblemontournament.common.api.TournamentStoreManager
import com.cobblemontournament.common.api.storage.TournamentBuilderStore
import com.cobblemontournament.common.api.storage.TournamentStore
import com.cobblemontournament.common.commands.nodes.NodeEntry
import com.cobblemontournament.common.commands.nodes.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.nodes.NodeKeys.TOURNAMENT_NAME
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.context.ParsedCommandNode
import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.position.StorePosition
import com.someguy.storage.store.Store
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object CommandUtil
{
    fun tryGetNodeInput(
        nodes: List<ParsedCommandNode<*>>,
        input: String,
        key: String
    ): String? {
        for ( parsedNode in nodes ) {
            if ( parsedNode.node.name == key ) {
                val range = parsedNode.range
                return input.subSequence( range.start, range.end ).toString()
            }
        }
        return null
    }

    fun getNodeEntries( ctx: CommandContext<CommandSourceStack> ): List<NodeEntry> {
        return getNodeEntries( ctx.nodes, ctx.input )
    }

    private fun getNodeEntries(
        nodes: List<ParsedCommandNode<*>>,
        input: String
    ): List<NodeEntry> {
        val list = mutableListOf<NodeEntry>()
        for ( parsedNode in nodes ) {
            val range = parsedNode.range
            val value = input.subSequence( range.start, range.end )
            list.add( NodeEntry( parsedNode.node.name, value.toString() ) )
        }
        return list
    }

    fun getNodesAndTournament(
        ctx: CommandContext<CommandSourceStack>,
        storeID: UUID?
    ): Pair<List<NodeEntry>, Tournament?> {
        val nodeEntries = getNodeEntries( ctx )
        val tournament = tryGetInstance(
            storeClass = TournamentStore::class.java,
            storeID = storeID,
            entry = nodeEntries.firstOrNull { it.key == TOURNAMENT_NAME }
        )
        return Pair( nodeEntries, tournament )
    }

    fun getNodesAndTournamentBuilder(
        ctx: CommandContext<CommandSourceStack>,
        storeID: UUID?
    ): Pair<List<NodeEntry>, TournamentBuilder?> {
        val nodeEntries = getNodeEntries( ctx )
        val builder = tryGetInstance(
            storeClass = TournamentBuilderStore::class.java,
            storeID = storeID,
            entry = nodeEntries.firstOrNull { it.key == BUILDER_NAME }
        )
        return Pair( nodeEntries, builder )
    }


    private fun <P : StorePosition, C : ClassStored, St : Store<P, C>> tryGetInstance(
        storeClass: Class<out St>,
        storeID: UUID?,
        entry: NodeEntry?
    ): C? {
        return if (entry != null) {
            tryGetInstance(
                storeClass = storeClass,
                storeID = storeID,
                instanceName = entry.value
            )?: tryGetInstance(
                storeClass = storeClass,
                storeID = TournamentStoreManager.ACTIVE_STORE_ID,
                instanceName = entry.value
            )?: tryGetInstance(
                storeClass = storeClass,
                storeID = TournamentStoreManager.INACTIVE_STORE_ID,
                instanceName = entry.value
            )
        } else null
    }

    private fun <P : StorePosition, C : ClassStored, St : Store<P, C>> tryGetInstance(
        storeClass: Class<out St>,
        storeID: UUID?,
        instanceName: String
    ): C? {
        return if (storeID != null) {
            TournamentStoreManager.getInstanceByName(
                storeClass = storeClass,
                name = instanceName,
                storeID = storeID
            ).first
        } else null
    }

    fun displayNoArgument(
        player: ServerPlayer?,
        nodeKey: String
    ): Int {
        if (player == null) {
            Util.report( "\"$nodeKey\" command had no arguments." )
        } else {
            val text0 = ChatUtil.formatText(
                text = "\"$nodeKey\"  had no arguments.",
                color = ChatUtil.yellow,
                bold = true )
            player.displayClientMessage( text0 ,false )
        }
        return Command.SINGLE_SUCCESS
    }

    fun createChallengeMatchInteractable(
        text: String,
        tournament: Tournament,
        opponent: ServerPlayer,
        color: String = ChatUtil.white,
        bracketed: Boolean = false,
        bracketColor: String = ChatUtil.white,
    ): MutableComponent {
//      // this is the format for the next version of CobblemonChallenge when Handicap & level range are supported
//        var commandText = "/challenge ${opponent.name.string} "
//        commandText    += "minLevel ${tournament.minLevel} maxLevel ${tournament.maxLevel} "
//        commandText    += "handicapP1 0 handicapP2 0" // _TODO implement handicap in player properties

        var commandText = "/challenge ${opponent.name.string}"
        commandText += " level ${tournament.maxLevel}"
        commandText += if (tournament.showPreview) "" else " nopreview"

        val interactable = ChatUtil.getInteractableCommand(
            text = text,
            command = commandText,
            color = color,
            bold = true
        )

        if (bracketed) {
            val component = ChatUtil.formatText(
                text = "[",
                color = bracketColor
            )
            component.append( interactable )
            return component.append( ChatUtil.formatText(
                text = "]",
                color = bracketColor
            ) )
        } else {
            return interactable
        }
    }

    fun failedCommand( reason: String ): MutableComponent {
        return ChatUtil.formatText(
            text = "Command Failed: $reason.",
            color = ChatUtil.yellow
        )
    }

    fun successfulCommand( text: String ): MutableComponent {
        return ChatUtil.formatText(
            text = "Command Success: $text.",
            color = ChatUtil.green
        )
    }
}
