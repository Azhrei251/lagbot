package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioManager
import org.javacord.api.event.message.MessageCreateEvent

class QueueCommand(
    private val audioManager: AudioManager,
    messageCreateEvent: MessageCreateEvent
) : BaseCommand(messageCreateEvent) {

    override fun execute() {
        val scheduler = audioManager.getScheduler(event.server.get())
        if (scheduler == null) {
            event.channel.sendMessage("No songs in queue!")
        } else {
            scheduler.printQueue()
        }
    }
}