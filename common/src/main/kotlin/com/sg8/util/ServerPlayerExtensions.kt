package com.sg8.util

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import org.slf4j.helpers.Util

fun ServerPlayer.displayInChat(text: String, color: String = WHITE_FORMAT, bold: Boolean = false) {
    this.displayClientMessage(getComponent(text, color, bold = bold), false)
}

fun ServerPlayer.displayInChat(component: Component) {
    this.displayClientMessage(component, false)
}

fun ServerPlayer?.displayNoArgument(nodeKey: String): Int {
    this?.displayInChat(text = "\"$nodeKey\"  had no arguments.", color = YELLOW_FORMAT)
        ?: Util.report("\"$nodeKey\" command had no arguments.")
    return 0
}

fun ServerPlayer?.displayCommandFail(reason: String) {
    this?.displayInChat(text = "Command Failed: $reason.", color = YELLOW_FORMAT)
        ?: Util.report("Command Failed: $reason.")
}

fun ServerPlayer?.displayCommandSuccess(text: String) {
    this?.displayInChat(text = "Command Success: $text.", color = GREEN_FORMAT)
        ?: Util.report("Command Success: $text.")
}
