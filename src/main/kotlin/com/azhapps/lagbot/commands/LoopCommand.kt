package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

fun Commands.loop(event: MessageCreateEvent) {
    val scheduler = audioManager.getScheduler(event.server.get())
    if (scheduler == null) {
        event.channel.sendMessage("Nothing in queue!")
    } else {
        val looped = scheduler.loop()
        if (looped == 0) {
            event.channel.sendMessage("Nothing to loop!")
        } else {
            event.channel.sendMessage("Setup loop for $looped songs")
        }
    }
}