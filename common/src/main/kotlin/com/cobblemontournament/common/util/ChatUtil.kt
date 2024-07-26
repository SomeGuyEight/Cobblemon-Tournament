package com.cobblemontournament.common.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer

fun getComponent(
    text: String = "",
    color: String = ChatUtil.WHITE_FORMAT,
    padding: Pair<Int, Int> = 0 to 0,
    bold: Boolean = false,
    italic: Boolean = false
): MutableComponent {
    val component = if (padding.first > 0) {
        Component.literal(color + text.padStart(padding.first))
    } else {
        Component.literal(color + text)
    }
    return component.setStyle(bold, italic)
}

fun getBracketedComponent(
    text: String,
    textColor: String = ChatUtil.WHITE_FORMAT,
    bracketColor: String = ChatUtil.WHITE_FORMAT,
    padding: Pair<Int, Int> = 0 to 0,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
): MutableComponent {
    return getBracketedComponent(
        component = getComponent(text = text, color = textColor, bold = bold),
        bracketColor = bracketColor,
        padding = padding,
        bold = bold,
        italic = italic,
        underlined = underlined,
    )
}

fun getBracketedComponent(
    component: Component,
    bracketColor: String = ChatUtil.WHITE_FORMAT,
    padding: Pair<Int, Int> = 0 to 0,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
): MutableComponent {
    val outerComponent = getComponent(
        text = "[".padStart(padding.first),
        color = bracketColor,
        bold = bold,
    )
    outerComponent.append(component)
    outerComponent.appendWith(
        text = "]".padEnd(padding.second),
        color = bracketColor,
        bold = bold,
    )
    return outerComponent.setStyle(bold, italic, underlined)
}

fun getQuotedComponent(
    text: String,
    textColor: String = ChatUtil.WHITE_FORMAT,
    quotationColor: String = ChatUtil.WHITE_FORMAT,
    padding: Pair<Int, Int> = 0 to 0,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
): MutableComponent {
    val component = getComponent(
        text = "\"".padStart(padding.first),
        color = quotationColor,
        bold = bold,
    )
    component.appendWith(text = text, color = textColor, bold = bold)
    component.appendWith(
        text = "\"".padEnd(padding.second),
        color = quotationColor,
        bold = bold,
    )
    return component.setStyle(bold, italic, underlined)
}

fun getInteractableCommand(
    command: String,
    text: String = "(Interact)",
    color: String = ChatUtil.WHITE_FORMAT,
    bracketed: Boolean = false,
    bracketColor: String = ChatUtil.WHITE_FORMAT,
    bold: Boolean = false,
): MutableComponent {
    // TODO test & confirm 'String.format(value)' not needed
    val component = Component
        //.literal((color + (String.format(text))))
        .literal(color + text)
        .setStyle(Style.EMPTY.withBold(bold)
            //.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, (String.format(command)))
            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
        )

    return if (bracketed) {
        getBracketedComponent(component, bracketColor = bracketColor, bold = bold)
    } else {
        component
    }
}

fun MutableComponent.setStyle(
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
): MutableComponent {
    val style = Style.EMPTY
    bold ifTrue { style.withBold(true) }
    italic ifTrue { style.withItalic(true) }
    underlined ifTrue { style.withUnderlined(true) }
    return this.setStyle(style)
}

fun ServerPlayer.displayInChat(
    text: String,
    color: String = ChatUtil.WHITE_FORMAT,
    bold: Boolean = false,
) {
    this.displayClientMessage(getComponent(text, color, bold = bold), false)
}

fun MutableComponent.appendWith(
    text: String,
    color: String = ChatUtil.WHITE_FORMAT,
    bold: Boolean = false,
) {
    this.append(getComponent(text = text, color = color, bold = bold))
}

fun MutableComponent.appendWithBracketed(
    text: String,
    textColor: String = ChatUtil.WHITE_FORMAT,
    bracketColor: String = ChatUtil.WHITE_FORMAT,
    padding: Pair<Int, Int> = 0 to 0,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
) {
    this.append(
        getBracketedComponent(
            text = text,
            textColor = textColor,
            bracketColor = bracketColor,
            padding = padding,
            bold = bold,
            italic = italic,
            underlined = underlined,
        )
    )
}

fun MutableComponent.appendWithQuoted(
    text: String,
    textColor: String = ChatUtil.WHITE_FORMAT,
    quotationColor: String = ChatUtil.WHITE_FORMAT,
    padding: Pair<Int, Int> = 0 to 0,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
) {
    this.append(
        getQuotedComponent(
            text = text,
            textColor = textColor,
            quotationColor = quotationColor,
            padding = padding,
            bold = bold,
            italic = italic,
            underlined = underlined,
        )
    )
}

/*  TODO maybe
    fun MutableComponent.appendWithParentheses() {

    }
 */

fun MutableComponent.appendWithCommand(
    text: String,
    command: String,
    color: String = ChatUtil.WHITE_FORMAT,
    bold: Boolean = false,
) {
    this.append(getInteractableCommand(command, text, color, bold))
}

object ChatUtil {
    val AQUA_FORMAT by lazy { ChatFormatting.AQUA.toString() }
    val GREEN_FORMAT by lazy { ChatFormatting.GREEN.toString() }
    val PURPLE_FORMAT by lazy { ChatFormatting.LIGHT_PURPLE.toString() }
    val RED_FORMAT by lazy { ChatFormatting.RED.toString() }
    val YELLOW_FORMAT by lazy { ChatFormatting.YELLOW.toString() }
    val WHITE_FORMAT by lazy { ChatFormatting.WHITE.toString() }
}
