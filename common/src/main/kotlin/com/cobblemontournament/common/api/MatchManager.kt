package com.cobblemontournament.common.api

import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemontournament.common.util.CommandUtil
import com.cobblemontournament.common.match.MatchStatus
import com.cobblemontournament.common.match.TournamentMatch
import com.cobblemontournament.common.tournament.Tournament
import com.cobblemontournament.common.util.ChatUtil
import com.mojang.brigadier.Command
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util
import java.util.UUID

object MatchManager
{
    private val watchedMatches = mutableSetOf<TournamentMatch>()

    fun handlePlayerLogoutEvent( player: ServerPlayer ) {
        watchedMatches.removeIf { it.containsPlayer( player.uuid ) }
    }

    fun displayAllPlayerMatches(
        player: ServerPlayer )
    {
        val tournaments = getTournamentsWithPlayer( player.uuid )
        for ( tournament in tournaments ) {
            handleMatchChallengeRequest( tournament, player )
        }
    }

    private fun getTournamentsWithPlayer(
        playerID: UUID
    ): Set <Tournament> {
        val tournaments = mutableSetOf <Tournament>()
        val tournamentStore = TournamentStoreManager.getTournamentStore() ?: return tournaments // TODO log?
        for ( tournament in tournamentStore ) {
            if ( tournament.containsPlayerID( playerID ) ) {
                tournaments.add( tournament)
            }
        }
        return tournaments
    }

    private fun getMatchesWithPlayer(
        playerID: UUID
    ): Set <TournamentMatch> {
        val matches = mutableSetOf <TournamentMatch>()
        val tournamentStore = TournamentStoreManager.getTournamentStore() ?: return matches // TODO log?
        for ( tournament in tournamentStore ) {
            val match = tournament.getCurrentMatch( playerID ) ?: continue
            matches.add( match )
        }
        return matches
    }

    fun handleBattleVictoryEvent( event: BattleVictoryEvent )
    {
        if ( event.wasWildCapture ) return

        val victorID = event.winners.first().uuid
        val matches = getWatchedMatchesWithPlayer( victorID )
        if ( matches.isEmpty() ) {
            return
        }

        val validMatches = mutableSetOf <TournamentMatch>()
        for ( match in matches ) {
            if ( event.losers.any { match.containsPlayer( it.uuid ) } ) {
                validMatches.add( match )
            }
        }

        // TODO handle doubles & other match types when implemented
        //  - should just be one for now
        if ( validMatches.isEmpty() ) {
            val player = PlayerManager.getServerPlayer(event.winners.first().uuid)
                ?: return Util.report( "Failed to get victor ServerPlayer for current match." )
            return ChatUtil.displayInPlayerChat(
                player = player,
                text = "Failed to get victor ServerPlayer for current match." )
        }

        validMatches.first().updateVictorID( victorID )
    }

    private fun getWatchedMatchesWithPlayer(
        uuid: UUID?
    ): List <TournamentMatch> {
        return if ( uuid != null ) {
            watchedMatches.toSet().dropWhile { !it.containsPlayer( uuid ) }
        } else mutableListOf()
    }

    @JvmStatic
    fun handleMatchChallengeRequest(
        tournament      : Tournament,
        challenger      : ServerPlayer,
        nullableMatch   : TournamentMatch? = null)
    {
        val player = TournamentStoreManager.getPlayer( tournament.tournamentID, challenger.uuid )

        if ( player != null && player.currentMatchID == null ) {
            val text = ChatUtil.formatText(  text = "${player.name} ", ChatUtil.aqua )
            text.append( ChatUtil.formatText( text = "finished in " ) )
            text.append( ChatUtil.formatTextBracketed(
                text    = player.finalPlacement.toString(),
                color   = ChatUtil.green,
                bold    = true ) )
            text.append( ChatUtil.formatText( text = " place!" ) )
            challenger.displayClientMessage( text , false )
            return
        }

        var match = nullableMatch
        var playerTwoName = "[opponent]"
        var insert: MutableComponent? = null
        if ( player != null ) {
            match = TournamentStoreManager.getMatch( tournament.tournamentID, player.currentMatchID!! )
            if ( match != null ) {
                // TODO handle NPCs
                val playerMap = match.playerEntrySet()
                val challengerTeam = playerMap.firstNotNullOfOrNull {
                    if ( it.key == challenger.uuid ) {
                        it.value
                    } else null
                }
                val opponentID = playerMap.firstNotNullOfOrNull {
                    if ( it.key != challenger.uuid && it.value != challengerTeam ) {
                        it.key
                    } else null
                }

                val serverPlayers = PlayerManager.getServerPlayers( playerMap.keys.toSet() )
                val opponent = if ( opponentID != null ) {
                    serverPlayers.firstOrNull { it.uuid == opponentID }
                } else null

                if ( opponent != null ) {
                    playerTwoName = opponent.name.string
                } else if ( opponentID != null ) {
                    playerTwoName = ChatUtil.shortUUID( opponentID )
                }

                if ( serverPlayers.size < playerMap.size ) {
                    insert = ChatUtil.formatTextBracketed(
                        text    = "Participant offline",
                        color   = ChatUtil.yellow )
                } else if ( opponent != null ) {
                    insert = handleChallengeInteractable( opponent, tournament )
                    watchedMatches.add( match )
                }
            } else {
                insert = ChatUtil.formatTextBracketed(
                    text    = "Match was null",
                    color   = ChatUtil.yellow )
            }
        } else {
            insert = ChatUtil.formatTextBracketed(
                text    = "Server Player was null",
                color   = ChatUtil.yellow )
        }

        displayMatchDetails(
            player          = challenger,
            playerOneName   = challenger.name.string,
            playerTwoName   = playerTwoName,
            tournamentName  = tournament.name,
            match           = match,
            insert          = insert )
    }

    @JvmStatic
    fun displayMatchDetails(
        player          : ServerPlayer,
        playerOneName   : String,
        playerTwoName   : String,
        tournamentName  : String,
        match           : TournamentMatch?,
        insert          : Component? = null
    ): Int
    {
        val title = ChatUtil.formatText( text = "\"")
        title.append( ChatUtil.formatText( text = tournamentName, ChatUtil.green ) )
        title.append( ChatUtil.formatText( text = "\" ${match?.name} " ) )
        title.append( ChatUtil.formatText( text = playerOneName, ChatUtil.aqua ) )
        title.append( ChatUtil.formatText( text = " vs ", ChatUtil.purple ) )
        title.append( ChatUtil.formatText( text = playerTwoName, ChatUtil.aqua ) )
        val status = ChatUtil.formatText( text = "  Match Status ", bold = true )
        val statusColor = if ( match != null ) {
            getStatusColor( match.matchStatus )
        } else ChatUtil.white
        status.append( ChatUtil.formatTextBracketed(
            text    = "${match?.matchStatus}",
            color   = statusColor,
            bold    = true ) )
        if ( insert != null ) {
            status.append( ChatUtil.formatText( text = " " ) )
            status.append( insert )
        }
        player.displayClientMessage( title, false )
        player.displayClientMessage( status, false )
        return Command.SINGLE_SUCCESS
    }

    @JvmStatic
    fun handleChallengeInteractable(
        opponent    : ServerPlayer,
        tournament  : Tournament
    ): MutableComponent
    {
        return CommandUtil.createChallengeMatchInteractable(
            text        = "Click to Challenge!",
            tournament  = tournament,
            opponent    = opponent,
            color       = ChatUtil.green,
            bracketed   = true )
    }

    @JvmStatic
    fun getStatusColor(
        matchStatus: MatchStatus
    ): String {
        return when ( matchStatus ) {
            MatchStatus.ERROR -> ChatUtil.red
            MatchStatus.UNKNOWN,
            MatchStatus.EMPTY,
            MatchStatus.PENDING,
            MatchStatus.NOT_READY -> ChatUtil.yellow
            MatchStatus.READY,
            MatchStatus.IN_PROGRESS,
            MatchStatus.COMPLETE,
            MatchStatus.FINALIZED -> ChatUtil.green
        }
    }

}
