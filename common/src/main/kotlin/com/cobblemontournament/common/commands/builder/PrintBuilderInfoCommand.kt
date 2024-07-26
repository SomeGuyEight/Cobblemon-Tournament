package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.commands.CommandContext
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]-[INFO]
 */
object PrintBuilderInfoCommand {

    val executionNode by lazy { ExecutionNode { printInfo(ctx = it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val overviewStack = Commands
            .literal(OVERVIEW)
            .executes(this.executionNode.action)

        val builderPropertiesStack = Commands
            .literal(BUILDER_PROPERTIES)
            .executes(this.executionNode.action)

        val playerPropertiesStack = Commands
            .literal(PLAYER_PROPERTIES)
            .executes(this.executionNode.action)

        dispatcher.register(ActiveBuilderInfoNode.nest(overviewStack))
        dispatcher.register(ActiveBuilderInfoNode.nest(builderPropertiesStack))
        dispatcher.register(ActiveBuilderInfoNode.nest(playerPropertiesStack))

        dispatcher.register(BuilderHistoryInfoNode.nest(overviewStack))
        dispatcher.register(BuilderHistoryInfoNode.nest(builderPropertiesStack))
        dispatcher.register(BuilderHistoryInfoNode.nest(playerPropertiesStack))
    }

    private fun printInfo(ctx: CommandContext): Int {
        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(storeID = null) ?: return 0

        return when {
            ctx.getNodeInputRange(BUILDER_PROPERTIES) != null -> {
                handleBuilderProperties(ctx.source.player, tournamentBuilder)
            }
            ctx.getNodeInputRange(PLAYER_PROPERTIES) != null -> {
                handlePlayerProperties(ctx.source.player, tournamentBuilder)
            }
            ctx.getNodeInputRange(OVERVIEW) != null -> {
                handleOverview(ctx.source.player, tournamentBuilder)
            }
            else -> {
                ctx.source.player.displayCommandFail(reason = "No valid input found in command")
                return 0
            }
        }
    }

    private fun handleBuilderProperties(
        player: ServerPlayer?,
        tournamentBuilder: TournamentBuilder,
    ): Int {
        if (player != null) {
            tournamentBuilder.displayPropertiesInChatSlim(player = player)
        } else {
            tournamentBuilder.printProperties()
        }
        return Command.SINGLE_SUCCESS
    }

    private fun handlePlayerProperties(
        player: ServerPlayer?,
        tournamentBuilder: TournamentBuilder,
    ): Int {
        if (player != null) {
            tournamentBuilder.displayPlayersInChat(player, padStart = 2, displaySeed = true)
        } else {
            tournamentBuilder.printPlayerInfo()
        }
        return Command.SINGLE_SUCCESS
    }

    private fun handleOverview(
        player: ServerPlayer?,
        tournamentBuilder: TournamentBuilder,
    ): Int {
        if (player != null) {
            tournamentBuilder.displayPropertiesInChat(player = player)
        } else {
            tournamentBuilder.printProperties()
            tournamentBuilder.printPlayerInfo()
        }
        return Command.SINGLE_SUCCESS
    }

}
