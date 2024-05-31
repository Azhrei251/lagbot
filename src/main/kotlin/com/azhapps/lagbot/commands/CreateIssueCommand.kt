package com.azhapps.lagbot.commands

import com.azhapps.lagbot.github.GithubRepository
import dev.kord.core.entity.effectiveName
import dev.kord.core.event.message.MessageCreateEvent

class CreateIssueCommand(messageEvent: MessageCreateEvent) : BaseCommand(messageEvent) {
    override suspend fun execute() {
        val decomposed = event.message.content.split(' ')

        GithubRepository.createIssue(
            title = decomposed.slice(IntRange(1, decomposed.size - 1)).joinToString(" "),
            body = "Created by lagbot. Requesting user: ${event.message.author?.username} | ${event.message.author?.effectiveName}"
        ) {
            event.message.channel.createMessage(it)
        }
    }
}