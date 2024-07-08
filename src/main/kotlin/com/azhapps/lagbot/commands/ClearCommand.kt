package com.azhapps.lagbot.commands

import dev.kord.core.event.message.MessageCreateEvent

class ClearCommand(messageCreateEvent: MessageCreateEvent): BaseCommand(messageCreateEvent) {

    override suspend fun execute() {
        /*val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.message.channel.createMessage("No songs in queue!")
        } else {
            event.message.channel.createMessage("Cleared queue")
            scheduler.clear()
        }*/
    }
}