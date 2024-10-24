package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext

fun Commands.resume(context: CommandContext) {
    val scheduler = context.scheduler
    if (scheduler == null) {
        context.onResponse("Nothing to resume!")
    } else {
        context.onResponse("Resumed")
        scheduler.resume()
    }
}