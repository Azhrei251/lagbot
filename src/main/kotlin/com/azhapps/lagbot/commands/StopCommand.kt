package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

fun Commands.stop(event: MessageCreateEvent) {
    val scheduler = audioManager.getScheduler(event.server.get())
    if (scheduler == null) {
        event.channel.sendMessage("Nothing to stop!")
    } else {
        event.channel.sendMessage("Stopped")
        scheduler.stop()
    }
}