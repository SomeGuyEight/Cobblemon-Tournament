package com.cobblemontournament.common.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

object ChatUtil
{
    val aqua    = ChatFormatting.AQUA .toString()
    val black   = ChatFormatting.BLACK.toString()
    val blue    = ChatFormatting.BLUE.toString()
    val gold    = ChatFormatting.GOLD.toString()
    val gray    = ChatFormatting.GRAY.toString()
    val green   = ChatFormatting.GREEN.toString()
    val purple  = ChatFormatting.LIGHT_PURPLE.toString()
    val red     = ChatFormatting.RED.toString()
    val yellow  = ChatFormatting.YELLOW.toString()
    val white   = ChatFormatting.WHITE.toString()

    val bold            = ChatFormatting.BOLD.toString()
    val italic          = ChatFormatting.ITALIC.toString()
    val obfuscated      = ChatFormatting.OBFUSCATED.toString()
    val strikethrough   = ChatFormatting.STRIKETHROUGH.toString()
    val underlined      = ChatFormatting.UNDERLINE.toString()
    val reset           = ChatFormatting.RESET.toString()

    @JvmStatic
    fun shortUUID( uuid: UUID? ) = uuid?.toString()?.substring( 0, 8 ) ?: "null-uuid"

    @JvmStatic
    fun formatText(
        text            : String,
        color           : String  = white,
        bold            : Boolean = false,
        italic          : Boolean = false,
        underlined      : Boolean = false,
        strikethrough   : Boolean = false,
        obfuscated      : Boolean = false
    ): MutableComponent {
        val component = Component.literal( color + String.format( text ) )
        return setStyle( component, bold, italic, underlined, strikethrough, obfuscated )
    }

    @JvmStatic
    fun formatTextBracketed(
        text            : String,
        color           : String  = white, // text specifically
        bracketColor    : String  = white,
        bold            : Boolean = false,
        italic          : Boolean = false,
        underlined      : Boolean = false,
        strikethrough   : Boolean = false,
        obfuscated      : Boolean = false
    ): MutableComponent {
        val component = formatText( text = "[", color = bracketColor, bold = bold )
        component.append( formatText( text = text, color = color, bold = bold ) )
        component.append( formatText( text = "]", color = bracketColor, bold = bold ) )
        return setStyle( component, bold, italic, underlined, strikethrough, obfuscated )
    }

    @JvmStatic
    fun setStyle(
        component       : MutableComponent,
        bold            : Boolean = false,
        italic          : Boolean = false,
        underlined      : Boolean = false,
        strikethrough   : Boolean = false,
        obfuscated      : Boolean = false
    ): MutableComponent
    {
        val style = Style.EMPTY
        if ( bold )             { style.withBold( true ) }
        if ( italic )           { style.withItalic( true ) }
        if ( underlined )       { style.withUnderlined( true ) }
        if ( strikethrough )    { style.withStrikethrough( true ) }
        if ( obfuscated )       { style.withObfuscated( true ) }
        return component.setStyle( style )
    }

    @JvmStatic
    fun getInteractableCommand(
        text    : String,
        command : String,
        color   : String,
        bold    : Boolean = false
    ): MutableComponent
    {
        return Component.literal( color + String.format( text ) )
            .setStyle( Style.EMPTY.withBold( bold )
                .withClickEvent( ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    String.format( command )
                ) ) )
    }

    @JvmStatic
    fun displayInPlayerChat(
        player  : ServerPlayer,
        text    : String,
        color   : String = white,
        bold    : Boolean = false
    ) {
        player.displayClientMessage( formatText( text, color, bold ), false)
    }

}
