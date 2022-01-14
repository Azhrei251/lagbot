package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import org.javacord.api.event.message.MessageCreateEvent

class ResumeCommand(messageCreateEvent: MessageCreateEvent): BaseCommand(messageCreateEvent) {

    override fun execute() {
        val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.channel.sendMessage("Nothing to resume!")
        } else {
            event.channel.sendMessage("Resumed")
            scheduler.resume()
        }
    }
}