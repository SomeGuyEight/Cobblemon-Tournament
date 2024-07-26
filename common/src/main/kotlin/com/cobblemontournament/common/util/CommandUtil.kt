package com.cobblemontournament.common.util

import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.someguy.storage.*
import com.someguy.storage.util.StoreID
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

fun ServerPlayer?.displayNoArgument(nodeKey: String): Int {
    this?.displayInChat(text = "\"$nodeKey\"  had no arguments.", color = ChatUtil.YELLOW_FORMAT)
        ?: Util.report("\"$nodeKey\" command had no arguments.")
    return 0
}

fun ServerPlayer?.displayCommandFail(reason: String) {
    this?.displayInChat(text = "Command Failed: $reason.", color = ChatUtil.YELLOW_FORMAT)
        ?: Util.report("Command Failed: $reason.")
}

fun ServerPlayer?.displayCommandSuccess(text: String) {
    this?.displayInChat(text = "Command Success: $text.", color = ChatUtil.GREEN_FORMAT)
        ?: Util.report("Command Success: $text.")
}

fun CommandContext.containsNodeName(nodeName: String): Boolean {
    return this.nodes.any { it.node.name == nodeName }
}

fun CommandContext.getNodeInputRange(nodeName: String): String? {
    return this.nodes.firstOrNull { it.node.name == nodeName }
        ?.let { this.input.subSequence(it.range.start, it.range.end).toString() }
}

fun CommandContext.getNodeInputRangeOrDisplayFail(nodeName: String): String? {
    return this.getNodeInputRange(nodeName)
        ?: let { ctx ->
            val reason = "No valid \"$nodeName\" node in command."
            ctx.source.player.displayCommandFail(reason)
            return null
        }
}

fun CommandContext.getPlayerEntityArgument(): ServerPlayer? {
    if (this.containsNodeName(PLAYER_ENTITY)) {
        return EntityArgument.getPlayer(this, PLAYER_ENTITY)
    }
    return null
}

fun CommandContext.getPlayerEntityArgumentOrDisplayFail(): ServerPlayer? {
    return this.getPlayerEntityArgument()
        ?: let { ctx ->
            ctx.source.player.displayCommandFail(reason = "Player entity argument was invalid")
            return null
        }
}

fun CommandContext.getServerPlayerOrDisplayFail(): ServerPlayer? {
    return this.source.player ?: let { _ ->
        Util.report( "Server Player was null")
        return null
    }
}

fun CommandContext.getTournament(storeID: StoreID? = null): Tournament? {
    return this.getNodeInputRange(TOURNAMENT_NAME)
        ?.let { name ->
            CommandUtil.tryGetInstance(
                storeClass = TournamentStore::class.java,
                name = name,
                storeID = storeID,
            )
        }
}

fun CommandContext.getTournamentOrDisplayFail(storeID: StoreID? = null): Tournament? {
    return this.getTournament(storeID = storeID)
        ?: let { ctx ->
            val reason = "Tournament was null."
            ctx.source.player.displayCommandFail(reason)
            return null
        }
}

fun CommandContext.getTournamentBuilder(storeID: StoreID? = null): TournamentBuilder? {
    return this.getNodeInputRange(BUILDER_NAME)
        ?.let { name ->
            CommandUtil.tryGetInstance(
                storeClass = TournamentBuilderStore::class.java,
                name = name,
                storeID = storeID,
            )
        }
}

fun CommandContext.getTournamentBuilderOrDisplayFail(
    storeID: StoreID? = null,
): TournamentBuilder? {
    return this.getTournamentBuilder(storeID = storeID)
        ?: let { ctx ->
            val reason = "Tournament builder was null."
            ctx.source.player.displayCommandFail(reason)
            return null
        }
}

object CommandUtil {

    fun <P : StorePosition, C : ClassStored, St : Store<P, C>> tryGetInstance(
        storeClass: Class<out St>,
        name: String,
        storeID: StoreID? = null,
    ): C? {
        val getInstance: (UUID) -> C? = { uuid ->
            TournamentStoreManager.getInstanceByName(storeClass, name, storeID = uuid)
        }
        return storeID
            ?.let { getInstance(it) }
            ?: getInstance(TournamentStoreManager.ACTIVE_STORE_ID)
            ?: getInstance(TournamentStoreManager.INACTIVE_STORE_ID)
    }

    fun createChallengeMatchInteractable(
        text: String,
        tournament: Tournament,
        opponent: ServerPlayer,
        color: String = ChatUtil.WHITE_FORMAT,
        bracketed: Boolean = false,
        bracketColor: String = ChatUtil.WHITE_FORMAT,
    ): MutableComponent {
// for the next version of CobblemonChallenge when Handicap & level range are supported
//        var commandText = "/challenge ${opponent.name.string} "
//        commandText += "minLevel ${tournament.minLevel} maxLevel ${tournament.maxLevel} "
//        commandText += "handicapP1 0 handicapP2 0" // _TODO implement handicap in player properties

        var commandText = ("/challenge ${opponent.name.string}")
        commandText += (" level ${tournament.maxLevel}")
        commandText += if (tournament.showPreview) "" else " nopreview"

        val interactable = getInteractableCommand(
            command = commandText,
            text = text,
            color = color,
            bold = true,
        )

        if (bracketed) {
            val component = getComponent(text = "[", color = bracketColor)
            component.append(interactable)
            component.appendWith(text = "]", color = bracketColor)
            return component
        } else {
            return interactable
        }
    }

}
