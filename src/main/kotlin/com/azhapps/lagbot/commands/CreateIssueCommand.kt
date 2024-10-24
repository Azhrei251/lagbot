package com.azhapps.lagbot.commands

import com.azhapps.lagbot.commands.model.CommandContext
import org.javacord.api.event.message.MessageCreateEvent

fun Commands.createIssue(context: CommandContext, userInfo: String) {
    val decomposed = context.arguments.split(' ')

    githubRepository.createIssue(
        title = decomposed.slice(IntRange(1, decomposed.size - 1)).joinToString(" "),
        body = "Created by lagbot. Requesting user: $userInfo"
    ) {
        context.onResponse(it)
    }
}