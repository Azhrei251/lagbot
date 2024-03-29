package com.azhapps.lagbot.commands

import com.azhapps.lagbot.github.GithubRepository
import org.javacord.api.event.message.MessageCreateEvent

class CreateIssueCommand(messageEvent: MessageCreateEvent) : BaseCommand(messageEvent) {
    override fun execute() {
        val decomposed = event.messageContent.split(' ')

        GithubRepository.createIssue(
            title = decomposed.slice(IntRange(1, decomposed.size - 1)).joinToString(" "),
            body = "Created by lagbot. Requesting user: ${event.messageAuthor.discriminatedName} | ${event.messageAuthor.displayName}"
        ) {
            event.channel.sendMessage(it)
        }
    }
}