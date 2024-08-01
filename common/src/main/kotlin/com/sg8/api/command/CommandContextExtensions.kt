package com.sg8.api.command

import com.sg8.api.command.node.PLAYER_ENTITY
import com.sg8.util.displayCommandFail
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util


fun CommandContext.containsNodeName(nodeName: String): Boolean {
    return this.nodes.any { it.node.name == nodeName }
}

fun CommandContext.getNodeInputRange(nodeName: String): String? {
    return this.nodes
        .firstOrNull { it.node.name == nodeName }
        ?.let { this.input.subSequence(it.range.start, it.range.end).toString() }
}

fun CommandContext.getNodeInputRangeOrDisplayFail(nodeName: String): String? {
    return this.getNodeInputRange(nodeName) ?: run {
        val reason = "No valid \"$nodeName\" node in command."
        this.source.player.displayCommandFail(reason)
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
    return this.getPlayerEntityArgument() ?: run {
        this.source.player.displayCommandFail(reason = "Player entity argument was invalid")
        return null
    }
}

fun CommandContext.getServerPlayerOrDisplayFail(): ServerPlayer? {
    return this.source.player ?: run {
        Util.report("Server Player was null")
        return null
    }
}
