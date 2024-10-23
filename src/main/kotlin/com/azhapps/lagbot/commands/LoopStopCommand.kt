package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioManager
import org.javacord.api.event.message.MessageCreateEvent

class LoopStopCommand(
    private val audioManager: AudioManager,
    event: MessageCreateEvent): BaseCommand(event
) {

    override fun execute() {
        val scheduler = audioManager.getScheduler(event.server.get())
        if (scheduler == null) {
            event.channel.sendMessage("Nothing in queue!")
        } else {
            scheduler.stopLoop()
            event.channel.sendMessage("Loop cleared")
        }
    }
}