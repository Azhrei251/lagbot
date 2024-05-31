package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import dev.kord.core.event.message.MessageCreateEvent

class LoopCommand(event: MessageCreateEvent): BaseCommand(event) {

    override suspend fun execute() {
        val scheduler = null//AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.message.channel.createMessage("Nothing in queue!")
        } else {
            val looped = 0//scheduler.loop()
            if (looped == 0) {
                event.message.channel.createMessage("Nothing to loop!")
            } else {
                event.message.channel.createMessage("Setup loop for $looped songs")
            }
        }
    }
}