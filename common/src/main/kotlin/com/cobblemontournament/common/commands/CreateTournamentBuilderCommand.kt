package com.cobblemontournament.common.commands

import com.cobblemontournament.common.TournamentManager
import com.cobblemontournament.common.commands.nodes.builder.CreateBuilderNode
import com.cobblemontournament.common.commands.util.CommandUtil
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER
import com.cobblemontournament.common.commands.util.NodeKeys.BUILDER_NAME
import com.cobblemontournament.common.commands.util.NodeKeys.CREATE
import com.cobblemontournament.common.commands.util.NodeKeys.EXECUTE
import com.cobblemontournament.common.commands.util.NodeKeys.NEW
import com.cobblemontournament.common.commands.util.NodeKeys.TOURNAMENT
import com.cobblemontournament.common.tournamentbuilder.TournamentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.CommandSelection
import org.slf4j.helpers.Util

object CreateTournamentBuilderCommand
{
    /**
     * [TOURNAMENT] -> [BUILDER] -> [CREATE] -> [NEW]+[BUILDER_NAME] -> [createNewBuilder]
     *
     *      literal     [TOURNAMENT]        ->
     *      literal     [BUILDER]           ->
     *      literal     [CREATE]            ->
     *      argument    [NEW]+[BUILDER_NAME] , StringType ->
     *      literal     [EXECUTE]           ->
     *      function    [createNewBuilder]
     */
    @JvmStatic
    fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registry: CommandBuildContext,
        selection: CommandSelection)
    {
        dispatcher.register(
            CreateBuilderNode.createBuilder(
                Commands.literal(EXECUTE)
                    .executes { ctx: CommandContext<CommandSourceStack> ->
                        createNewBuilder( ctx = ctx)
                    })
        )
        // TODO add more optional properties to assign
    }

    @JvmStatic
    private fun createNewBuilder(
        ctx: CommandContext<CommandSourceStack>
    ): Int
    {
        var name = "something is wrong if you see this ¯\\_(ツ)_/¯ "
        var reason              : String = name
        var exists              : Boolean? = null
        var tournamentBuilder   : TournamentBuilder?    = null

        val nodeEntries = CommandUtil.getNodeEntries( ctx.nodes, ctx.input)
        for (entry in nodeEntries) {
            when (entry.key) {
                "$NEW$BUILDER_NAME" -> {
                    name = entry.value
                    val (builder, reasonInner) = TournamentManager.getTournamentBuilderByName(entry.value)
                    reason = reasonInner
                    if (builder != null) {
                        exists = true
                    } else {
                        tournamentBuilder = TournamentBuilder()
                        tournamentBuilder.name = entry.value
                        TournamentManager.addTournamentBuilder( builder = tournamentBuilder, saveStore = true)
                    }
                }
            }
        }

        if (exists == true) {
            Util.report("Failed to CREATE Tournament Builder b/c \"$name\" already exists. Reason from Manager: \"$reason\"")
            return 0
        } else if (tournamentBuilder == null) {
            Util.report("Failed to CREATE Tournament Builder b/c Tournament Builder was null")
            return 0
        } else {
            // changes already saved above
            Util.report("Successfully CREATED Tournament Builder \"${tournamentBuilder.name}\"")
            return Command.SINGLE_SUCCESS
        }
    }

}
