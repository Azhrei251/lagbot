package com.azhapps.lagbot.commands

import dev.kord.core.event.message.MessageCreateEvent

class PauseCommand(messageCreateEvent: MessageCreateEvent): BaseCommand(messageCreateEvent) {

    override suspend fun execute() {
       /* val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.message.channel.createMessage("No song currently playing!")
        } else {
            event.message.channel.createMessage("Paused!")
            scheduler.pause()
        }*/
    }
}