package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import dev.kord.core.event.message.MessageCreateEvent

class SkipCommand(messageCreateEvent: MessageCreateEvent): BaseCommand(messageCreateEvent) {

    override suspend fun execute() {
       /* event.message.channel.createMessage("Skipping!")
        AudioUtil.getScheduler(event.server.get())?.skip()*/
    }
}