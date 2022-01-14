package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

class PlayCommand(messageCreateEvent: MessageCreateEvent): BasePlayCommand(messageCreateEvent) {

    override val immediate: Boolean
        get() = true
}