package com.azhapps.lagbot.commands

import dev.kord.core.event.message.MessageCreateEvent

abstract class BaseCommand(val event: MessageCreateEvent) {

    abstract suspend fun execute()
}