package com.sg8.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

val AQUA_FORMAT by lazy { ChatFormatting.AQUA.toString() }
val GREEN_FORMAT by lazy { ChatFormatting.GREEN.toString() }
val PURPLE_FORMAT by lazy { ChatFormatting.LIGHT_PURPLE.toString() }
val RED_FORMAT by lazy { ChatFormatting.RED.toString() }
val YELLOW_FORMAT by lazy { ChatFormatting.YELLOW.toString() }
val WHITE_FORMAT by lazy { ChatFormatting.WHITE.toString() }

fun getComponent(
    text: String = "text",
    color: String = WHITE_FORMAT,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
): MutableComponent {
    val component = if (padding.first > 0) {
        Component.literal(color + text.padStart(padding.first))
    } else {
        Component.literal(color + text)
    }
    return component.setStyle(bold, italic, underlined)
}

fun getBracketedComponent(
    text: String,
    textColor: String = WHITE_FORMAT,
    bracketColor: String = WHITE_FORMAT,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
): MutableComponent {
    return getBracketedComponent(
        component = getComponent(
            text = text,
            color = textColor,
            bold = bold,
            italic = italic,
            underlined = underlined,
        ),
        bracketColor = bracketColor,
        padding = padding,
        bold = bold,
        italic = italic,
        underlined = underlined,
    )
}

fun getBracketedComponent(
    component: Component,
    bracketColor: String = WHITE_FORMAT,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
): MutableComponent {
    val outerComponent = getComponent(
        text = "[".padStart(padding.first),
        color = bracketColor,
        bold = bold,
    )
    outerComponent.append(component)
    outerComponent.appendWith(text = "]".padEnd(padding.second), color = bracketColor, bold = bold)
    return outerComponent.setStyle(bold, italic, underlined)
}

fun getQuotedComponent(
    text: String,
    textColor: String = WHITE_FORMAT,
    quoteColor: String = WHITE_FORMAT,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
): MutableComponent {
    return getQuotedComponent(
        component = getComponent(
            text = text,
            color = textColor,
            bold = bold,
            italic = italic,
            underlined = underlined,
        ),
        quoteColor = quoteColor,
        bold = bold,
        italic = italic,
        underlined = underlined,
        padding = padding,
    )
}

fun getQuotedComponent(
    component: Component,
    quoteColor: String = WHITE_FORMAT,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
): MutableComponent {
    val quotedComponent = getComponent(
        text = "\"".padStart(padding.first),
        color = quoteColor,
        bold = bold,
    )
    quotedComponent.append(component)
    quotedComponent.appendWith(text = "\"".padEnd(padding.second), color = quoteColor, bold = bold)
    return quotedComponent.setStyle(bold, italic, underlined)
}

fun getInteractableComponent(
    command: String,
    text: String = "Interact",
    color: String = WHITE_FORMAT,
    bracketed: Boolean = false,
    bracketColor: String = WHITE_FORMAT,
    bold: Boolean = false,
): MutableComponent {
    // TODO test & confirm 'String.format(value)' not needed
    val component = Component
        //.literal((color + (String.format(text))))
        .literal(color + text)
        .setStyle(
            Style.EMPTY.withBold(bold)
            //.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, (String.format(command)))
            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
        )

    return if (bracketed) {
        getBracketedComponent(component, bracketColor = bracketColor, bold = bold)
    } else {
        component
    }
}

