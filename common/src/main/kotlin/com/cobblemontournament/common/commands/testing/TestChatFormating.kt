package com.cobblemontournament.common.commands.testing

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.commands.nodes.*
import com.cobblemontournament.common.commands.nodes.ExecutionNode
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.match.properties.MatchProperties
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.round.properties.RoundProperties
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.*
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

/**
 * [TOURNAMENT]-"test"
 */
object TestChatFormating {

    private val execution: ExecutionNode by lazy { ExecutionNode { displayChat(it) } }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher
            .register(TournamentRootNode
                .nest(Commands
                    .literal("test")
                    .then(Commands
                        .literal("general")
                        .executes(execution.action)
                    )
                    .then(Commands
                        .literal(BUILDER)
                        .then(Commands
                            .literal("all_${BUILDER}")
                            .executes(this.execution.action)
                        )
                        .then(Commands
                            .literal("${CREATE}_${BUILDER}")
                            .executes(this.execution.action)
                        )
                        .then(Commands
                            .literal("${UPDATE}_${BUILDER}")
                            .executes(this.execution.action)
                        )
                        .then(Commands
                            .literal("${UPDATE}_${BUILDER}")
                            .executes(this.execution.action)
                        )
                    )
                    .then(Commands
                        .literal(PLAYER)
                        .then(Commands
                            .literal("all_${PLAYER}")
                            .executes(this.execution.action)
                        )
                        .then(Commands
                            .literal("${REGISTER}_${PLAYER}")
                            .executes(this.execution.action)
                        )
                        .then(Commands
                            .literal("${UPDATE}_${PLAYER}")
                            .executes(this.execution.action)
                        )
                        .then(Commands
                            .literal("${UNREGISTER}_${PLAYER}")
                            .executes(this.execution.action)
                        )
                    .then(Commands
                        .literal(MATCH)
                        .then(Commands
                            .literal("${MATCH}_manager")
                            .executes(this.execution.action)))
                        .then(Commands
                            .literal("all_${MATCH}")
                            .executes(this.execution.action)
                        )
                    )
                    .then(Commands
                        .literal(ROUND)
                        .then(Commands
                            .literal("all_${ROUND}")
                            .executes(this.execution.action)
                        )
                    )
                    .then(Commands
                        .literal(TOURNAMENT)
                        .then(Commands
                            .literal("all_${TOURNAMENT}")
                            .executes(this.execution.action)
                        )
                    )
                )
            )
    }

    private fun displayChat(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.source.player?: run {
            Util.report("Could not display chat b/c player was null")
            return 0
        }

        // all properties
        val builderID = UUID.randomUUID()
        val tournamentID = UUID.randomUUID()

        val playerOneProps = PlayerProperties(
            name = "Player_One",
            actorType = ActorType.PLAYER,
            playerID = UUID.randomUUID(),
            tournamentID = builderID,
        )

        val playerTwoProps = PlayerProperties(
            name = "Player_Two",
            actorType = ActorType.PLAYER,
            playerID = UUID.randomUUID(),
            tournamentID = builderID,
        )

        val builder = TournamentBuilder(builderID)
        builder.addPlayer(
            playerID = playerOneProps.playerID,
            playerName = playerOneProps.name,
            actorType = ActorType.PLAYER,
            seed = 1,
        )
        builder.addPlayer(
            playerID = playerTwoProps.playerID,
            playerName = playerTwoProps.name,
            actorType = ActorType.PLAYER,
            seed = 2,
        )

        playerOneProps.tournamentID = tournamentID
        playerTwoProps.tournamentID = tournamentID

        val playerMap = mutableMapOf<UUID,Int>()
        playerMap[playerOneProps.playerID] = 1
        playerMap[playerTwoProps.playerID] = 2

        val matchProps = MatchProperties()
        matchProps.playerMap.putAll(playerMap)
        matchProps.victorID = playerOneProps.playerID

        val roundProps = RoundProperties(
            roundID = UUID.randomUUID(),
            tournamentID = tournamentID,
            roundIndex = 0,
        )
        roundProps.roundType = RoundType.PRIMARY
        roundProps.indexedMatchMap[0] = matchProps.matchID

        val playerOne = TournamentPlayer(playerOneProps)
        val playerTwo = TournamentPlayer(playerTwoProps)
        val match = TournamentMatch(matchProps)
        val round = TournamentRound(roundProps)

        val tournamentProps = TournamentProperties(tournamentID = tournamentID)

        tournamentProps.rounds[round.uuid] = round
        tournamentProps.matches[match.uuid] = match
        tournamentProps.players[playerOne.uuid] = playerOne
        tournamentProps.players[playerTwo.uuid] = playerTwo
        val tournament = Tournament(tournamentProps)


        ctx.getNodeInputRange(nodeName = "general")?. let { displayGeneral(player) }

        ctx.getNodeInputRange(nodeName = "all_${BUILDER}")?. let {
            displayBuilderProperties(player, builder)
            displayCreateTournamentBuilderCommand(player)
            displayUpdateBuilderCommand(player)
        }

        ctx.getNodeInputRange(nodeName = "${CREATE}_${BUILDER}")?. let {
            displayCreateTournamentBuilderCommand(player)
        }

        ctx.getNodeInputRange(nodeName = "${UPDATE}_${BUILDER}")?. let {
            displayUpdateBuilderCommand(player)
        }

        ctx.getNodeInputRange(nodeName = "all_${PLAYER}")?. let {
            displayPlayerProperties(player, playerOneProps)
            displayRegisterPlayerCommand(player)
            displayUpdatePlayerCommand(player)
            displayUnregisterPlayerCommand(player)
        }

        ctx.getNodeInputRange(nodeName = "${REGISTER}_${PLAYER}")?. let {
            displayRegisterPlayerCommand(player)
        }

        ctx.getNodeInputRange(nodeName = "${UPDATE}_${PLAYER}")?. let {
            displayUpdatePlayerCommand(player)
        }

        ctx.getNodeInputRange(nodeName = "${UNREGISTER}_${PLAYER}")?. let {
            displayUnregisterPlayerCommand(player)
        }

        ctx.getNodeInputRange(nodeName = "all_${MATCH}" )?. let {
            displayMatchProperties(player, matchProps)
            displayMatchManager(player, playerOne, playerTwo, tournament, match)
        }

        ctx.getNodeInputRange(nodeName = "${MATCH}_manager")?. let {
            displayMatchManager(player, playerOne, playerTwo, tournament, match)
        }

        ctx.getNodeInputRange(nodeName = "all_${ROUND}")?. let {
            displayRoundProperties(player, roundProps)
        }

        ctx.getNodeInputRange(nodeName = "all_${TOURNAMENT}")?. let {
            displayTournamentProperties(player, tournamentProps)
            displayGenerateTournamentCommand(player)
        }

        return Command.SINGLE_SUCCESS
    }

    private fun displayGeneral(player: ServerPlayer) {
        TODO("Need to update format")
//        player.displayInChat(text = "[General]")
//        player.displayCommandFail(reason = "Server Player was null")
//        player.displayCommandFail(reason = "Tournament Builder was null" )
//        player.displayCommandFail(reason = "Tournament Data was null" )
//        player.displayCommandFail(reason = "Tournament was null")
    }

    private fun displayBuilderProperties(player: ServerPlayer, builder: TournamentBuilder) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Tournament Builder]")
//        builder.displayPropertiesInChat(player = player)
    }

    private fun displayCreateTournamentBuilderCommand(player: ServerPlayer) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Create Tournament Builder Command]")
//        player.displayCommandFail(reason = "A builder named \"\${BUILDER_NAME}\" already exists")
//        player.displayCommandSuccess(text = "CREATED Tournament Builder \"\${BUILDER_NAME}\"")
    }

    private fun displayUpdateBuilderCommand(player: ServerPlayer ) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Update Builder Command]")
//        player.displayCommandSuccess(text = "UPDATED Tournament Builder \"\${BUILDER_NAME}\"")
    }

    private fun displayPlayerProperties(player: ServerPlayer, props: PlayerProperties) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Player Properties]")
//        props.displayInChat(player = player)
    }

    private fun displayRegisterPlayerCommand(player: ServerPlayer) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Register Player Command]")
//        player.displayCommandFail(
//            reason = "Player already registered OR registration failed inside builder"
//        )
//        player.displayCommandSuccess(text = "REGISTERED \${PLAYER_NAME} with \"\${BUILDER_NAME}\"")
//        player.displayInChat(
//            text = "You were REGISTERED with Tournament Builder \"\${BUILDER_NAME}\"!",
//            color = ChatUtil.GREEN_FORMAT,
//        )
    }

    private fun displayUnregisterPlayerCommand(player: ServerPlayer ) {
        TODO("Need to update format")
//        player.displayInChat(text = "[UnregisterPlayerCommand]")
//        player.displayCommandSuccess(text = "UNREGISTERED \${PLAYER_NAME} from \"\${BUILDER_NAME}\"")
//        player.displayInChat(
//            text = "You were UNREGISTERED from Tournament Builder \"\${BUILDER_NAME}\"!",
//        )
    }

    private fun displayUpdatePlayerCommand(player: ServerPlayer ) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Update Player Command]")
//        player.displayCommandFail(reason = "All properties to update were null")
//        player.displayCommandSuccess(
//            text = "UPDATED \${PLAYER_NAME} properties in \"\${BUILDER_NAME}\""
//        )
//        player.displayInChat(
//            text = "Your properties have been UPDATED in Tournament Builder \"\${BUILDER_NAME}\"!",
//            color = ChatUtil.WHITE_FORMAT,
//        )
    }

    private fun displayMatchProperties(player: ServerPlayer, props: MatchProperties) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Match Properties]")
//        props.displayInChat(player = player)
    }

    private fun displayMatchManager(
        player: ServerPlayer,
        playerOne: TournamentPlayer,
        playerTwo: TournamentPlayer,
        tournament: Tournament,
        match: TournamentMatch,
    ) {
        TODO("Need to update format")
//        player.displayInChat(text = "[MatchManager]")
//        val text = getComponent( text = "\${PLAYER_NAME} ", color = ChatUtil.AQUA_FORMAT)
//        text.appendWith(text = "finished in ")
//        text.appendWithBracketed(
//            text = "\${FINAL_PLACEMENT}",
//            textColor = ChatUtil.GREEN_FORMAT,
//            bold = true,
//        )
//        text.appendWith(text = " place!")
//        player.displayClientMessage(text ,false)
//
//        val action: (Component) -> Unit = { component ->
//            WatchedMatchManager.displayMatchDetails(
//                player = player,
//                playerOneName = playerOne.name,
//                playerTwoName = playerTwo.name,
//                tournamentName = tournament.name,
//                match = match,
//                insert = component,
//            )
//        }
//
//        action(
//            getBracketedComponent(
//                text = "Server Player was null",
//                textColor = ChatUtil.YELLOW_FORMAT,
//            )
//        )
//        action(
//            getBracketedComponent(text = "Match was null", textColor = ChatUtil.YELLOW_FORMAT)
//        )
//        action(
//            getBracketedComponent(text = "Participant offline", textColor = ChatUtil.YELLOW_FORMAT)
//        )
//        action(WatchedMatchManager.handleChallengeInteractable(player, tournament))
    }

    private fun displayRoundProperties(player: ServerPlayer, props: RoundProperties) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Round Properties]")
//        props.displayInChat(player = player)
    }

    private fun displayTournamentProperties(player: ServerPlayer, props: TournamentProperties) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Tournament Properties]")
//        props.displayInChat(player = player)
//        player.displayInChat(text = "[Tournament] ")
//
//        TournamentPropertiesHelper.displayInChatHelper(properties = props, player = player)
//
//        player.displayInChat(text = "[Tournament - Slim] ")
//        TournamentPropertiesHelper.displaySlimInChatHelper(properties = props, player = player)
//
//        player.displayInChat(text = "[Tournament - Overview] ")
//        TournamentPropertiesHelper.displayOverviewInChat(properties = props, player = player)
    }

    private fun displayGenerateTournamentCommand(player: ServerPlayer) {
        TODO("Need to update format")
//        player.displayInChat(text = "[Generate Tournament Command]")
//        player.displayCommandSuccess(text = "GENERATED Tournament \"\${TOURNAMENT_NAME}\"")
    }

}
