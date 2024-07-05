package com.cobblemontournament.common.commands.nodes.builder

import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.PLAYER
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.commands.util.CommandUtil.logNoArgument
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.player.TournamentPlayer
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object ActiveBuilderPlayersNode
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [BUILDER_NAME] -> [PLAYER] -> _
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [BUILDER]           ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [PLAYER]            ->
     *      _
     */
    @JvmStatic
    fun player(
        literal: LiteralArgumentBuilder<CommandSourceStack>,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner( literal = literal, argument = null)
    }

    /**
     * [TOURNAMENT] -> [BUILDER] -> [BUILDER_NAME] -> [PLAYER] -> _
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [BUILDER]           ->
     *      argument    [BUILDER_NAME] , StringType ->
     *      literal     [PLAYER]            ->
     *      _
     */
    @JvmStatic
    fun player(
        argument: RequiredArgumentBuilder<CommandSourceStack, *>,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return inner( literal = null, argument = argument)
    }

    @JvmStatic
    private fun inner(
        literal: LiteralArgumentBuilder<CommandSourceStack>? = null,
        argument: RequiredArgumentBuilder<CommandSourceStack,*>? = null
    ): LiteralArgumentBuilder<CommandSourceStack>
    {
        val builder = literal?: argument
        return ActiveBuilderNode.activeBuilder(
            Commands.literal( PLAYER)
                .executes {
                    // TODO clean these up
                    logNoArgument(
                        name    = PLAYER,
                        target  = "${TournamentBuilder::class.java.simpleName}, ${TournamentPlayer::class.java.simpleName}")
                }
                .then( builder)
        )
    }

}