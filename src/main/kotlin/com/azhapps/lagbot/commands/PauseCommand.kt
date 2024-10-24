package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext
import org.javacord.api.event.message.MessageCreateEvent

fun Commands.pause(context: CommandContext) {
    val scheduler = context.scheduler
    if (scheduler == null) {
        context.onResponse("No song currently playing!")
    } else {
        context.onResponse("Paused!")
        scheduler.pause()
    }
}