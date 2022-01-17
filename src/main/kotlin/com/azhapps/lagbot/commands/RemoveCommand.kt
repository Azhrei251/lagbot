package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import org.javacord.api.event.message.MessageCreateEvent
import java.lang.NumberFormatException

class RemoveCommand(messageCreateEvent: MessageCreateEvent): BaseCommand(messageCreateEvent) {

    override fun execute() {
        val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.channel.sendMessage("Nothing in queue!")
        } else {
            val indexToRemove = try {
                 event.messageContent.substringAfter(' ').toInt()
            } catch (e: NumberFormatException) {
                event.channel.sendMessage("Invalid command")
                -1
            }
            if (indexToRemove != -1 ) {
                val removed =  scheduler.remove(indexToRemove - 1)
                if (removed) {
                    event.channel.sendMessage("Removed song at position $indexToRemove")
                } else {
                    event.channel.sendMessage("No song in queue at position $indexToRemove")
                }
            }
        }
    }
}