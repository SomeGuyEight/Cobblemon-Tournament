package com.cobblemontournament.common.commands.testing

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemontournament.common.commands.nodes.NodeKeys
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.api.MatchManager
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.match.properties.MatchProperties
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.player.properties.PlayerProperties
import com.cobblemontournament.common.round.RoundType
import com.cobblemontournament.common.round.TournamentRound
import com.cobblemontournament.common.round.properties.RoundProperties
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.tournament.properties.TournamentProperties
import com.cobblemontournament.common.tournament.properties.TournamentPropertiesHelper
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.cobblemontournament.common.util.ChatUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object TestChatFormating
{
    @JvmStatic
    fun register(
        dispatcher  : CommandDispatcher <CommandSourceStack>,
        registry    : CommandBuildContext,
        selection   : Commands.CommandSelection
    )
    {
        dispatcher.register(
            literal( "tournament" )
                .then( literal( "test" )
                    .then( literal( "general" )
                        .executes { ctx -> displayChat( ctx ) } )
                    .then( literal( NodeKeys.BUILDER )
                        .then( literal( "all_${NodeKeys.BUILDER}" )
                            .executes { ctx -> displayChat( ctx ) } )
                        .then( literal( "${NodeKeys.CREATE}_${NodeKeys.BUILDER}" )
                            .executes { ctx -> displayChat( ctx ) } )
                        .then( literal( "${NodeKeys.UPDATE}_${NodeKeys.BUILDER}" )
                            .executes { ctx -> displayChat( ctx ) } )
                        .then( literal( "${NodeKeys.UPDATE}_${NodeKeys.BUILDER}" )
                            .executes { ctx -> displayChat( ctx ) } ) )
                    .then( literal( NodeKeys.PLAYER )
                        .then( literal( "all_${NodeKeys.PLAYER}" )
                            .executes { ctx -> displayChat( ctx ) } )
                        .then( literal( "${NodeKeys.REGISTER}_${NodeKeys.PLAYER}" )
                            .executes { ctx -> displayChat( ctx ) } )
                        .then( literal( "${NodeKeys.UPDATE}_${NodeKeys.PLAYER}" )
                            .executes { ctx -> displayChat( ctx ) } )
                        .then( literal( "${NodeKeys.UNREGISTER}_${NodeKeys.PLAYER}" )
                            .executes { ctx -> displayChat( ctx ) } ) )
                    .then( literal( NodeKeys.MATCH )
                        .then( literal( "${NodeKeys.MATCH}_manager" )
                            .executes { ctx -> displayChat( ctx ) } )
                        .then( literal( "all_${NodeKeys.MATCH}" )
                            .executes { ctx -> displayChat( ctx ) } ) )
                    .then( literal( NodeKeys.ROUND )
                        .then( literal( "all_${NodeKeys.ROUND}" )
                            .executes { ctx -> displayChat( ctx ) } ) )
                    .then( literal( NodeKeys.TOURNAMENT )
                        .then( literal( "all_${NodeKeys.TOURNAMENT}" )
                            .executes { ctx -> displayChat( ctx ) } ) )
//                    .then( literal( "my_active" )
//                        .then( literal( "all_my_active" )
//                            .executes { ctx -> displayChat( ctx ) } )
//                        .then( literal( "my_active_info" )
//                            .executes { ctx -> displayChat( ctx ) } )
//                        .then( literal( "my_active_current_match" )
//                            .executes { ctx -> displayChat( ctx ) } ) )
                ) )

    }

    private fun displayChat(
        ctx: CommandContext<CommandSourceStack>
    ): Int {

        val player = ctx.source.player
        if ( player == null ) {
            Util.report( "Could not display chat b/c player was null" )
            return 0
        }

        // all properties
        val builderID = UUID.randomUUID()
        val tournamentID = UUID.randomUUID()

        val playerOneProps = PlayerProperties(
            name            = "Player_One",
            actorType       = ActorType.PLAYER,
            playerID        = UUID.randomUUID(),
            tournamentID    = builderID )

        val playerTwoProps = PlayerProperties(
            name            = "Player_Two",
            actorType       = ActorType.PLAYER,
            playerID        = UUID.randomUUID(),
            tournamentID    = builderID )

        val builder = TournamentBuilder( builderID )
        builder.addPlayer(
            playerID    = playerOneProps.playerID,
            playerName  = playerOneProps.name,
            actorType   = ActorType.PLAYER,
            seed        = 1 )
        builder.addPlayer(
            playerID    = playerTwoProps.playerID,
            playerName  = playerTwoProps.name,
            actorType   = ActorType.PLAYER,
            seed        = 2 )

        playerOneProps.tournamentID = tournamentID
        playerTwoProps.tournamentID = tournamentID

        val playerMap = mutableMapOf <UUID,Int>()
        playerMap[playerOneProps.playerID] = 1
        playerMap[playerTwoProps.playerID] = 2

        val matchProps = MatchProperties()
        matchProps.playerMap.putAll( playerMap )
        matchProps.victorID = playerOneProps.playerID

        val roundProps = RoundProperties(
            roundID         = UUID.randomUUID(),
            tournamentID    = tournamentID,
            roundIndex      = 0 )
        roundProps.roundType = RoundType.PRIMARY
        roundProps.indexedMatchMap[0] = matchProps.matchID

        val playerOne = TournamentPlayer( playerOneProps )
        val playerTwo = TournamentPlayer( playerTwoProps )
        val match = TournamentMatch( matchProps )
        val round = TournamentRound( roundProps )

        val tournamentProps = TournamentProperties(
            tournamentID = tournamentID )

        tournamentProps.rounds[round.uuid] = round
        tournamentProps.matches[match.uuid] = match
        tournamentProps.players[playerOne.uuid] = playerOne
        tournamentProps.players[playerTwo.uuid] = playerTwo
        val tournament = Tournament( tournamentProps )

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input )
        for ( entry in nodeEntries ) {
            when ( entry.key ) {
                "general" -> general( player )
                "all_${NodeKeys.BUILDER}" -> {
                    builderProperties( player, builder )
                    createTournamentBuilderCommand( player )
                    updateBuilderCommand( player )
                }
                "${NodeKeys.CREATE}_${NodeKeys.BUILDER}" -> createTournamentBuilderCommand( player )
                "${NodeKeys.UPDATE}_${NodeKeys.BUILDER}" -> updateBuilderCommand( player )
                "all_${NodeKeys.PLAYER}" -> {
                    playerProperties( player, playerOneProps )
                    registerPlayerCommand( player )
                    updatePlayerCommand( player )
                    unregisterPlayerCommand( player )
                }
                "${NodeKeys.REGISTER}_${NodeKeys.PLAYER}"   -> registerPlayerCommand( player )
                "${NodeKeys.UPDATE}_${NodeKeys.PLAYER}"     -> updatePlayerCommand( player )
                "${NodeKeys.UNREGISTER}_${NodeKeys.PLAYER}" -> unregisterPlayerCommand( player )
                "all_${NodeKeys.MATCH}" -> {
                    matchProperties( player, matchProps )
                    matchManager( player, playerOne, playerTwo, tournament, match )
                }
                "${NodeKeys.MATCH}_manager" -> matchManager( player, playerOne, playerTwo, tournament, match )
                "all_${NodeKeys.ROUND}" -> {
                    roundProperties( player, roundProps )
                }
                "all_${NodeKeys.TOURNAMENT}" -> {
                    tournamentProperties( player, tournamentProps )
                    generateTournamentCommand( player )
                }
                "all_my_active" -> {
                    myActiveInfoCommand( player )
                    myActiveCurrentMatchCommand( player )
                }
                "my_active_info" -> myActiveInfoCommand( player )
                "my_active_current_match" ->  myActiveCurrentMatchCommand( player )
            }
        }

        return Command.SINGLE_SUCCESS
    }

    fun general( player: ServerPlayer)
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[General]" )
        player.displayClientMessage(
            CommandUtil.failedCommand(
                reason = "Server Player was null" ),
            false )
        player.displayClientMessage(
            CommandUtil.failedCommand(
                reason = "Tournament Builder was null" ),
            false )
        player.displayClientMessage(
            CommandUtil.failedCommand(
                reason = "Tournament Data was null" ),
            false )
        player.displayClientMessage(
            CommandUtil.failedCommand(
                reason = "Tournament was null" ),
            false )
    }

    fun builderProperties(
        player: ServerPlayer,
        builder: TournamentBuilder )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Tournament Builder]" )
        builder.displayPropertiesInChat( player )
    }

    fun createTournamentBuilderCommand( player: ServerPlayer)
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Create Tournament Builder Command]" )
        player.displayClientMessage(
            CommandUtil.failedCommand(
                reason = "A builder named \"\${BUILDER_NAME}\" already exists" ),
            false )
        player.displayClientMessage(
            CommandUtil.successfulCommand(
                action = "CREATED Tournament Builder \"\${BUILDER_NAME}\"" ),
            false )
    }

    fun updateBuilderCommand( player: ServerPlayer )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Update Builder Command]" )
        player.displayClientMessage(
            CommandUtil.successfulCommand(
                action = "UPDATED Tournament Builder \"\${BUILDER_NAME}\"" ),
            false )
    }

    fun playerProperties(
        player: ServerPlayer,
        props: PlayerProperties )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Player Properties]" )
        props.displayInChat( player )
    }

    fun registerPlayerCommand( player: ServerPlayer )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Register Player Command]" )
        player.displayClientMessage(
            CommandUtil.failedCommand(
                reason = "Player already registered OR registration failed inside builder" ),
            false )
        player.displayClientMessage(
            CommandUtil.successfulCommand(
                action = "REGISTERED \${PLAYER_NAME} with \"\${BUILDER_NAME}\"" ),
            false )
        ChatUtil.displayInPlayerChat(
            player = player,
            text   = "You were successfully REGISTERED with Tournament Builder \"\${BUILDER_NAME}\"!" )
    }

    fun unregisterPlayerCommand( player: ServerPlayer )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[UnregisterPlayerCommand]" )
        player.displayClientMessage(
            CommandUtil.successfulCommand(
                action = "UNREGISTERED \${PLAYER_NAME} from \"\${BUILDER_NAME}\"" ),
            false )
        ChatUtil.displayInPlayerChat(
            player = player,
            text   = "You were successfully UNREGISTERED from Tournament Builder \"\${BUILDER_NAME}\"!",
            color  = ChatUtil.white )
    }

    fun updatePlayerCommand( player: ServerPlayer )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Update Player Command]" )
        player.displayClientMessage(
            CommandUtil.failedCommand(
                reason = "All properties to update were null" ),
            false )
        player.displayClientMessage(
            CommandUtil.successfulCommand(
                action = "UPDATED \${PLAYER_NAME} properties in \"\${BUILDER_NAME}\"" ),
            false )
        ChatUtil.displayInPlayerChat(
            player = player,
            text   = "Your properties have been UPDATED in Tournament Builder \"\${BUILDER_NAME}\"!",
            color  = ChatUtil.white )
    }

    fun matchProperties(
        player: ServerPlayer,
        props: MatchProperties )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Match Properties]" )
        props.displayInChat( player )
    }

    fun matchManager(
        player: ServerPlayer,
        playerOne: TournamentPlayer,
        playerTwo: TournamentPlayer,
        tournament: Tournament,
        match: TournamentMatch )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[MatchManager]" )
        val text = ChatUtil.formatText(  text = "\${PLAYER_NAME} ", ChatUtil.aqua )
        text.append( ChatUtil.formatText( text = "finished in " ) )
        text.append( ChatUtil.formatTextBracketed(
            text    = "\${FINAL_PLACEMENT}",
            color   = ChatUtil.green,
            bold    = true ) )
        text.append( ChatUtil.formatText( text = " place!" ) )
        player.displayClientMessage( text ,false )

        val action: (Component) -> Unit = { component ->
            MatchManager.displayMatchDetails(
                player          = player,
                playerOneName   = playerOne.name,
                playerTwoName   = playerTwo.name,
                tournamentName  = tournament.name,
                match           = match,
                insert          = component )
        }

        action( ChatUtil.formatTextBracketed( text = "Server Player was null", ChatUtil.yellow ) )
        action( ChatUtil.formatTextBracketed( text = "Match was null", ChatUtil.yellow ) )
        action( ChatUtil.formatTextBracketed( text = "Participant offline", ChatUtil.yellow ) )
        action( MatchManager.handleChallengeInteractable( player, tournament ) )
    }

    fun roundProperties(
        player: ServerPlayer,
        props: RoundProperties )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Round Properties]" )
        props.displayInChat( player )
    }

    fun tournamentProperties(
        player: ServerPlayer,
        props: TournamentProperties )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Tournament Properties]" )
        props.displayInChat( player )

        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Tournament] " )
        TournamentPropertiesHelper.displayInChatHelper(
            props,
            player )
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Tournament - Slim] " )
        TournamentPropertiesHelper.displaySlimInChatHelper(
            props,
            player )
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Tournament - Overview] " )
        TournamentPropertiesHelper.displayOverviewInChat(
            props,
            player )
    }

    fun generateTournamentCommand( player: ServerPlayer )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[Generate Tournament Command]" )
        player.displayClientMessage(
            CommandUtil.successfulCommand(
                action = "GENERATED Tournament \"\${TOURNAMENT_NAME}\"" ),
            false )
    }

    fun myActiveInfoCommand( player: ServerPlayer )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[MyActiveInfoCommand]" )
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "-> just overviews & properties directly right now" )
    }

    fun myActiveCurrentMatchCommand( player: ServerPlayer )
    {
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "[MyActiveCurrentMatchCommand]" )
        ChatUtil.displayInPlayerChat(
            player = player,
            text = "-> just overviews & properties directly right now" )
    }

}
