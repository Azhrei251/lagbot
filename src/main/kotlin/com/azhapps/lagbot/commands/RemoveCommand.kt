package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

private const val FIRST = "first"
private const val LAST = "last"

fun Commands.remove(event: MessageCreateEvent) {
    val scheduler = audioManager.getScheduler(event.server.get())
    if (scheduler == null) {
        event.channel.sendMessage("Nothing in queue!")
    } else {
        val indexToRemove = when (val requestedRemove = event.messageContent.substringAfter(' ').lowercase()) {
            FIRST -> 1

            LAST -> scheduler.queueSize()

            else -> try {
                requestedRemove.toInt()
            } catch (e: NumberFormatException) {
                event.channel.sendMessage("Invalid command")
                -1
            }
        }

        if (indexToRemove != -1) {
            val removed = scheduler.remove(indexToRemove - 1)
            if (removed) {
                event.channel.sendMessage("Removed song at position $indexToRemove")
            } else {
                event.channel.sendMessage("No song in queue at position $indexToRemove")
            }
        }
    }
}