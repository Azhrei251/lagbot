package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import dev.kord.core.event.message.MessageCreateEvent

class QueueCommand(messageCreateEvent: MessageCreateEvent) : BaseCommand(messageCreateEvent) {

    override suspend fun execute() {
       /* val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.message.channel.createMessage("No songs in queue!")
        } else {
            scheduler.printQueue()
        }*/
    }
}