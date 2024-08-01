package com.sg8.util

import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

fun MutableComponent.setStyle(
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
): MutableComponent {
    val style = Style.EMPTY
    if (bold) style.withBold(true)
    if (italic) style.withItalic(true)
    if (underlined) style.withUnderlined(true)
    return this.setStyle(style)
}

fun MutableComponent.appendWith(
    text: String,
    color: String = WHITE_FORMAT,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
) {
    this.append(
        getComponent(
            text = text,
            color = color,
            bold = bold,
            italic = italic,
            underlined = underlined,
        ),
    )
}

fun MutableComponent.appendWithBracketed(
    text: String,
    textColor: String = WHITE_FORMAT,
    bracketColor: String = WHITE_FORMAT,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
) {
    this.append(
        getBracketedComponent(
            text = text,
            textColor = textColor,
            bracketColor = bracketColor,
            bold = bold,
            italic = italic,
            underlined = underlined,
            padding = padding,
        )
    )
}

fun MutableComponent.appendWithQuoted(
    text: String,
    textColor: String = WHITE_FORMAT,
    quoteColor: String = WHITE_FORMAT,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
) {
    this.append(
        getQuotedComponent(
            text = text,
            textColor = textColor,
            quoteColor = quoteColor,
            bold = bold,
            italic = italic,
            underlined = underlined,
            padding = padding,
        )
    )
}

fun MutableComponent.appendWithCommand(
    command: String,
    text: String = "Interact",
    color: String = WHITE_FORMAT,
    bold: Boolean = false,
) {
    this.append(
        getInteractableComponent(command = command, text = text, color = color, bold = bold)
    )
}

fun MutableComponent.appendWithBracketedCommand(
    command: String,
    text: String = "Interact",
    textColor: String = WHITE_FORMAT,
    bracketColor: String = WHITE_FORMAT,
    bold: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
) {
    this.appendWith(text = "[".padStart(padding.first), color = bracketColor, bold = bold)
    this.append(
        getInteractableComponent(command = command, text = text, color = textColor, bold = bold)
    )
    this.appendWith(text = "]".padEnd(padding.second), color = bracketColor, bold = bold)
}
