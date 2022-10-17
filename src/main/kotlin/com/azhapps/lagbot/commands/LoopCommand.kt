package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import org.javacord.api.event.message.MessageCreateEvent

class LoopCommand(event: MessageCreateEvent): BaseCommand(event) {

    override fun execute() {
        val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.channel.sendMessage("Nothing in queue!")
        } else {
            val looped = scheduler.loop()
            if (looped == 0) {
                event.channel.sendMessage("Nothing to loop!")
            } else {
                event.channel.sendMessage("Setup loop for $looped songs")
            }
        }
    }
}