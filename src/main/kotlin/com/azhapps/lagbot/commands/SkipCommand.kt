package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext

fun Commands.skip(context: CommandContext) {
    context.onResponse("Skipping!")
    context.scheduler?.apply {
        skip()
        resume()
    }
}