package com.azhapps.lagbot.commands

import com.azhapps.lagbot.github.GithubRepository
import org.javacord.api.event.message.MessageCreateEvent

class CreateIssueCommand(messageEvent: MessageCreateEvent) : BaseCommand(messageEvent) {
    override fun execute() {
        val decomposed = event.messageContent.split(' ')

        GithubRepository.createIssue(decomposed[1], "Created by lagbot") {
            event.channel.sendMessage(it)
        }
    }
}