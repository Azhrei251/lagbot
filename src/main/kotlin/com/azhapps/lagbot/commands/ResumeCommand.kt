package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

fun Commands.resume(event: MessageCreateEvent) {
    val scheduler = audioManager.getScheduler(event.server.get())
    if (scheduler == null) {
        event.channel.sendMessage("Nothing to resume!")
    } else {
        event.channel.sendMessage("Resumed")
        scheduler.resume()
    }
}