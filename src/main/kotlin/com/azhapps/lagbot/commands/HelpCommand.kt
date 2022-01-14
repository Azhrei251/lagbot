package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

class HelpCommand(messageEvent: MessageCreateEvent) : BaseCommand(messageEvent) {

    override fun execute() {
        var messageText = "```"
        Commands.Info.values().forEach {
            messageText += "${it.helpText}\n"
        }
        messageText += "```"
        event.channel.sendMessage(messageText)
    }
}