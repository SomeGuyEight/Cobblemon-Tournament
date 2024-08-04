package com.cobblemontournament.common.commands.builder

import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.util.getTournamentBuilderOrDisplayFail
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.*
import com.sg8.api.command.*
import com.sg8.api.command.node.ExecutionNode
import com.sg8.util.displayCommandFail
import net.minecraft.commands.*
import net.minecraft.server.level.ServerPlayer

/**
 * [TOURNAMENT]-[BUILDER]-[ACTIVE]-[BUILDER_NAME]-[INFO]
 */
object PrintBuilderInfoCommand {

    val executionNode = ExecutionNode { printInfo(ctx = it) }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val overviewStack = Commands
            .literal(OVERVIEW)
            .executes(this.executionNode.handler)

        val builderPropertiesStack = Commands
            .literal(BUILDER_PROPERTIES)
            .executes(this.executionNode.handler)

        val playerPropertiesStack = Commands
            .literal(PLAYER_PROPERTIES)
            .executes(this.executionNode.handler)

        dispatcher.register(ActiveBuilderInfoNode.nest(overviewStack))
        dispatcher.register(ActiveBuilderInfoNode.nest(builderPropertiesStack))
        dispatcher.register(ActiveBuilderInfoNode.nest(playerPropertiesStack))

        dispatcher.register(BuilderHistoryInfoNode.nest(overviewStack))
        dispatcher.register(BuilderHistoryInfoNode.nest(builderPropertiesStack))
        dispatcher.register(BuilderHistoryInfoNode.nest(playerPropertiesStack))
    }

    private fun printInfo(ctx: CommandContext): Int {
        val tournamentBuilder = ctx.getTournamentBuilderOrDisplayFail(storeID = null) ?: return 0

        when {
            ctx.getNodeInputRange(BUILDER_PROPERTIES) != null -> {
                printBuilderProperties(ctx.source.player, tournamentBuilder)
            }
            ctx.getNodeInputRange(PLAYER_PROPERTIES) != null -> {
                printPlayerProperties(ctx.source.player, tournamentBuilder)
            }
            ctx.getNodeInputRange(OVERVIEW) != null -> {
                printOverview(ctx.source.player, tournamentBuilder)
            }
            else -> {
                ctx.source.player.displayCommandFail(reason = "No valid input found in command")
                return 0
            }
        }
        return Command.SINGLE_SUCCESS
    }

    private fun printBuilderProperties(player: ServerPlayer?, builder: TournamentBuilder) {
        player?. run { builder.displayPropertiesInChatSlim(player = player) }
            ?: builder.printProperties()
    }

    private fun printPlayerProperties(player: ServerPlayer?, builder: TournamentBuilder) {
        player?. run { builder.displayPlayersInChat(player, padStart = 2, displaySeed = true) }
            ?: builder.printPlayerInfo()
    }

    private fun printOverview(player: ServerPlayer?, builder: TournamentBuilder) {
        player?.run { builder.displayPropertiesInChat(player = player) }
            ?: run {
                builder.printProperties()
                builder.printPlayerInfo()
            }
    }

}
