package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

@Suppress("UnusedReceiverParameter")
fun Commands.help(event: MessageCreateEvent) {
    var messageText = "```"
    Commands.Info.entries.forEach {
        messageText += "${it.helpText}\n"
    }
    messageText += "```"
    event.channel.sendMessage(messageText)
}