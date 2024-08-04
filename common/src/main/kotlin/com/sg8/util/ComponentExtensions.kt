package com.sg8.util

import net.minecraft.ChatFormatting
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
    color: ChatFormatting = ChatFormatting.WHITE,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
) {
    this.append(
        ComponentUtil.getComponent(
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
    textColor: ChatFormatting = ChatFormatting.WHITE,
    bracketColor: ChatFormatting = ChatFormatting.WHITE,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
) {
    this.append(
        ComponentUtil.getBracketedComponent(
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
    textColor: ChatFormatting = ChatFormatting.WHITE,
    quoteColor: ChatFormatting = ChatFormatting.WHITE,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
) {
    this.append(
        ComponentUtil.getQuotedComponent(
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
    color: ChatFormatting = ChatFormatting.WHITE,
    bold: Boolean = false,
) {
    this.append(
        ComponentUtil.getInteractableComponent(command = command, text = text, color = color, bold = bold)
    )
}

fun MutableComponent.appendWithBracketedCommand(
    command: String,
    text: String = "Interact",
    textColor: ChatFormatting = ChatFormatting.WHITE,
    bracketColor: ChatFormatting = ChatFormatting.WHITE,
    bold: Boolean = false,
    padding: Pair<Int, Int> = 0 to 0,
) {
    this.appendWith(text = "[".padStart(padding.first), color = bracketColor, bold = bold)
    this.append(
        ComponentUtil.getInteractableComponent(command = command, text = text, color = textColor, bold = bold)
    )
    this.appendWith(text = "]".padEnd(padding.second), color = bracketColor, bold = bold)
}
