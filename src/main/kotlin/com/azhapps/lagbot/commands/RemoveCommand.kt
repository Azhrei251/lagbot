package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import dev.kord.core.event.message.MessageCreateEvent

private const val FIRST = "first"
private const val LAST = "last"

class RemoveCommand(messageCreateEvent: MessageCreateEvent) : BaseCommand(messageCreateEvent) {

    override suspend fun execute() {
      /*  val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.message.channel.createMessage("Nothing in queue!")
        } else {
            val indexToRemove = when (val requestedRemove = event.messageContent.substringAfter(' ').lowercase()) {
                FIRST -> 1

                LAST -> scheduler.queueSize()

                else -> try {
                    requestedRemove.toInt()
                } catch (e: NumberFormatException) {
                    event.message.channel.createMessage("Invalid command")
                    -1
                }
            }

            if (indexToRemove != -1) {
                val removed = scheduler.remove(indexToRemove - 1)
                if (removed) {
                    event.message.channel.createMessage("Removed song at position $indexToRemove")
                } else {
                    event.message.channel.createMessage("No song in queue at position $indexToRemove")
                }
            }
        }*/
    }
}