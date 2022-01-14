package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import org.javacord.api.event.message.MessageCreateEvent

class SkipCommand(messageCreateEvent: MessageCreateEvent): BaseCommand(messageCreateEvent) {

    override fun execute() {
        event.channel.sendMessage("Skipping!")
        AudioUtil.getScheduler(event.server.get())?.skip()
    }
}