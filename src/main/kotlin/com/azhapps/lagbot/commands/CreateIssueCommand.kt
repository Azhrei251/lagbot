package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

fun Commands.createIssue(
    event: MessageCreateEvent,
) {
    val decomposed = event.messageContent.split(' ')

    githubRepository.createIssue(
        title = decomposed.slice(IntRange(1, decomposed.size - 1)).joinToString(" "),
        body = "Created by lagbot. Requesting user: ${event.messageAuthor.discriminatedName} | ${event.messageAuthor.displayName}"
    ) {
        event.channel.sendMessage(it)
    }
}