package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

fun Commands.loopStop(event: MessageCreateEvent) {
    val scheduler = audioManager.getScheduler(event.server.get())
    if (scheduler == null) {
        event.channel.sendMessage("Nothing in queue!")
    } else {
        scheduler.stopLoop()
        event.channel.sendMessage("Loop cleared")
    }
}