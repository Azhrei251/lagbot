package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext
import org.javacord.api.event.message.MessageCreateEvent

fun Commands.createIssue(context: CommandContext, userInfo: String) {

    githubRepository.createIssue(
        title = context.arguments,
        body = "Created by lagbot. Requesting user: $userInfo"
    ) {
        context.onResponse(it)
    }
}