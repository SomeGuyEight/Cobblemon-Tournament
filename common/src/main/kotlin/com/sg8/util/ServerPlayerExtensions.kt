package com.sg8.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

fun ServerPlayer.displayInChat(
    text: String,
    color: ChatFormatting = ChatFormatting.WHITE,
    bold: Boolean = false,
) {
    this.displayClientMessage(
        ComponentUtil.getComponent(text = text, color = color, bold = bold),
        false
    )
}

fun ServerPlayer.displayInChat(component: Component) {
    this.displayClientMessage(component, false)
}

fun ServerPlayer?.displayNoArgument(nodeKey: String): Int {
    this?.displayInChat(text = "\"$nodeKey\"  had no arguments.", color = ChatFormatting.YELLOW)
        ?: Util.report("\"$nodeKey\" command had no arguments.")
    return 0
}

fun ServerPlayer?.displayCommandFail(reason: String) {
    this?.displayInChat(text = "Command Failed: $reason.", color = ChatFormatting.YELLOW)
        ?: Util.report("Command Failed: $reason.")
}

fun ServerPlayer?.displayCommandSuccess(text: String) {
    this?.displayInChat(text = "Command Success: $text.", color = ChatFormatting.GREEN)
        ?: Util.report("Command Success: $text.")
}
