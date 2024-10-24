package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext
import org.javacord.api.event.message.MessageCreateEvent

fun Commands.loopStop(context: CommandContext) {
    val scheduler = context.scheduler
    if (scheduler == null) {
        context.onResponse("Nothing in queue!")
    } else {
        scheduler.stopLoop()
        context.onResponse("Loop cleared")
    }
}