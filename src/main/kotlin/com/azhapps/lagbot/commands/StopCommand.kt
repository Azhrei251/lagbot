package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext

fun Commands.stop(context: CommandContext) {
    val scheduler = context.scheduler
    if (scheduler == null) {
        context.onResponse("Nothing to stop!")
    } else {
        context.onResponse("Stopped")
        scheduler.stop()
    }
}