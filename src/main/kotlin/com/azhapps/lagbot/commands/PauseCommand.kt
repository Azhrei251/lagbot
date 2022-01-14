package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import org.javacord.api.event.message.MessageCreateEvent

class PauseCommand(messageCreateEvent: MessageCreateEvent): BaseCommand(messageCreateEvent) {

    override fun execute() {
        val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.channel.sendMessage("No song currently playing!")
        } else {
            event.channel.sendMessage("Paused!")
            scheduler.pause()
        }
    }
}