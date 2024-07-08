package com.azhapps.lagbot.commands

import dev.kord.core.event.message.MessageCreateEvent

class LoopStopCommand(event: MessageCreateEvent): BaseCommand(event) {

    override suspend fun execute() {
        /*val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.message.channel.createMessage("Nothing in queue!")
        } else {
            scheduler.stopLoop()
            event.message.channel.createMessage("Loop cleared")
        }*/
    }
}