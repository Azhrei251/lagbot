package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext

private const val FIRST = "first"
private const val LAST = "last"

fun Commands.remove(context: CommandContext) {
    val scheduler = context.scheduler
    if (scheduler == null) {
        context.onResponse("Nothing in queue!")
    } else {
        val indexToRemove = when (val requestedRemove = context.arguments.lowercase()) {
            FIRST -> 1

            LAST -> scheduler.queueSize()

            else -> try {
                requestedRemove.toInt()
            } catch (e: NumberFormatException) {
                context.onResponse("Invalid command")
                -1
            }
        }

        if (indexToRemove != -1) {
            val removed = scheduler.remove(indexToRemove - 1)
            if (removed) {
                context.onResponse("Removed song at position $indexToRemove")
            } else {
                context.onResponse("No song in queue at position $indexToRemove")
            }
        }
    }
}