package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext

fun Commands.loop(context: CommandContext) {
    val scheduler = context.scheduler
    if (scheduler == null) {
        context.onResponse("Nothing in queue!")
    } else {
        val looped = scheduler.loop()
        if (looped == 0) {
            context.onResponse("Nothing to loop!")
        } else {
            context.onResponse("Setup loop for $looped songs")
        }
    }
}