package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioManager
import org.javacord.api.event.message.MessageCreateEvent

class SkipCommand(
    private val audioManager: AudioManager,
    messageCreateEvent: MessageCreateEvent
) : BaseCommand(messageCreateEvent) {

    override fun execute() {
        event.channel.sendMessage("Skipping!")
        audioManager.getScheduler(event.server.get())?.skip()
    }
}