package com.azhapps.lagbot.commands

import dev.kord.core.event.message.MessageCreateEvent

class HelpCommand(messageEvent: MessageCreateEvent) : BaseCommand(messageEvent) {

    override suspend fun execute() {
        var messageText = "```"
        Commands.Info.values().forEach {
            messageText += "${it.helpText}\n"
        }
        messageText += "```"
        event.message.channel.createMessage(messageText)
    }
}