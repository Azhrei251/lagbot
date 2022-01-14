package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

abstract class BaseCommand(val event: MessageCreateEvent) {

    abstract fun execute()
}