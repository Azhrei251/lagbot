package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

fun Commands.skip(event: MessageCreateEvent) {
    event.channel.sendMessage("Skipping!")
    val scheduler = audioManager.getScheduler(event.server.get())
    scheduler?.apply {
        skip()
        resume()
    }
}