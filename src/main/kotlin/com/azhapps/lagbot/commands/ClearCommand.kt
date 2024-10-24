package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext

fun Commands.clear(context: CommandContext) {
    val scheduler = context.scheduler
    if (scheduler == null) {
        context.onResponse("No songs in queue!")
    } else {
        context.onResponse("Cleared queue")
        scheduler.clear()
    }
}