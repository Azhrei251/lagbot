package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

fun Commands.pause(event: MessageCreateEvent) {
    val scheduler = audioManager.getScheduler(event.server.get())
    if (scheduler == null) {
        event.channel.sendMessage("No song currently playing!")
    } else {
        event.channel.sendMessage("Paused!")
        scheduler.pause()
    }
}