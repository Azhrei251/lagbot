package com.azhapps.lagbot.commands

import dev.kord.core.event.message.MessageCreateEvent

class StopCommand(messageCreateEvent: MessageCreateEvent): BaseCommand(messageCreateEvent) {

    override suspend fun execute() {
        /*val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.message.channel.createMessage("Nothing to stop!")
        } else {
            event.message.channel.createMessage("Stopped")
            scheduler.stop()
        }*/
    }
}