package com.azhapps.lagbot.commands

import com.azhapps.lagbot.Main
import com.azhapps.lagbot.audio.AudioUtil
import org.javacord.api.event.message.MessageCreateEvent

abstract class BasePlayCommand(messageEvent: MessageCreateEvent) : BaseCommand(messageEvent) {

    abstract val immediate: Boolean

    override fun execute() {
        val botInVoice = Main.isConnectedToVoice(event)
        val requesterInVoice = event.messageAuthor.connectedVoiceChannel.isPresent
        if (requesterInVoice) {
            val songRequest = event.messageContent.substringAfter(' ').substringBefore("?playlist")
            if (botInVoice) {
                AudioUtil.playSong(event.server.get(), songRequest, event.channel, immediate)

            } else {
                event.messageAuthor.connectedVoiceChannel.get().connect().thenAccept {
                    AudioUtil.connect(event.server.get(), it)
                    AudioUtil.playSong(event.server.get(), songRequest, event.channel, immediate)
                }.whenComplete { _, t ->
                    t.printStackTrace()
                    event.channel.sendMessage("Something went wrong: ${t.message}")
                }
            }

        } else {
            event.channel.sendMessage("Join a voice channel first!")
        }
    }
}