package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import org.javacord.api.event.message.MessageCreateEvent

class LoopStopCommand(event: MessageCreateEvent): BaseCommand(event) {

    override fun execute() {
        val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.channel.sendMessage("Nothing in queue!")
        } else {
            scheduler.stopLoop()
            event.channel.sendMessage("Loop cleared")
        }
    }
}