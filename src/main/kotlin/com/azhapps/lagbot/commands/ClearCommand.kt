package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

fun Commands.clear(event: MessageCreateEvent) {
    val scheduler = audioManager.getScheduler(event.server.get())
    if (scheduler == null) {
        event.channel.sendMessage("No songs in queue!")
    } else {
        event.channel.sendMessage("Cleared queue")
        scheduler.clear()
    }
}