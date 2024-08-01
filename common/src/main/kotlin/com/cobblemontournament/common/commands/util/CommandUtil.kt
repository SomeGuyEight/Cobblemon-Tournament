package com.cobblemontournament.common.commands.util

import com.cobblemontournament.common.api.storage.*
import com.cobblemontournament.common.api.storage.store.TournamentBuilderStore
import com.cobblemontournament.common.api.storage.store.TournamentStore
import com.sg8.api.command.CommandContext
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.sg8.api.command.getNodeInputRange
import com.sg8.storage.*
import com.sg8.util.*
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

fun CommandContext.getTournament(storeID: UUID? = null): Tournament? {
    return this.getNodeInputRange(TOURNAMENT_NAME)?.let { name ->
        CommandUtil.tryGetInstance(
            storeClass = TournamentStore::class.java,
            name = name,
            storeID = storeID,
        )
    }
}

fun CommandContext.getTournamentOrDisplayFail(storeID: UUID? = null): Tournament? {
    return this.getTournament(storeID = storeID) ?: run {
        val reason = "Tournament was null."
        this.source.player.displayCommandFail(reason)
        return null
    }
}

fun CommandContext.getTournamentBuilder(storeID: UUID? = null): TournamentBuilder? {
    return this.getNodeInputRange(BUILDER_NAME)?.let { name ->
        CommandUtil.tryGetInstance(
            storeClass = TournamentBuilderStore::class.java,
            name = name,
            storeID = storeID,
        )
    }
}

fun CommandContext.getTournamentBuilderOrDisplayFail(
    storeID: UUID? = null,
): TournamentBuilder? {
    return this.getTournamentBuilder(storeID = storeID) ?: run {
        val reason = "Tournament builder was null."
        this.source.player.displayCommandFail(reason)
        return null
    }
}

object CommandUtil {

    fun <P : StorePosition, T : TypeStored, S : Store<P, T>> tryGetInstance(
        storeClass: Class<out S>,
        name: String,
        storeID: UUID? = null,
    ): T? {
        val getInstance: (UUID) -> T? = { uuid ->
            TournamentStoreManager.getInstanceByName(storeClass, name = name, storeID = uuid)
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
        color: String = WHITE_FORMAT,
        bracketed: Boolean = false,
        bracketColor: String = WHITE_FORMAT,
    ): MutableComponent {
// for the next version of CobblemonChallenge when Handicap & level range are supported
//        var commandText = "/challenge ${opponent.name.string} "
//        commandText += "minLevel ${tournament.minLevel} maxLevel ${tournament.maxLevel} "
//        commandText += "handicapP1 0 handicapP2 0" // _TODO implement handicap in player properties

        var commandText = ("/challenge ${opponent.name.string}")
        commandText += (" level ${tournament.maxLevel}")
        commandText += if (tournament.showPreview) "" else " nopreview"

        val interactable = getInteractableComponent(
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
