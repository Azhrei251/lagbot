package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext

@Suppress("UnusedReceiverParameter")
fun Commands.help(commandContext: CommandContext) {
    var messageText = "```"
    Commands.Info.entries.forEach {
        messageText += "${it.helpText}\n"
    }
    messageText += "```"
    commandContext.onResponse(messageText)
}