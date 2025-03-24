package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext

fun Commands.queue(context: CommandContext) {
    if (context.scheduler == null) {
        context.onResponse("No songs in queue!")
    } else {
        context.onResponse(context.scheduler.getFormattedQueue())
    }
}