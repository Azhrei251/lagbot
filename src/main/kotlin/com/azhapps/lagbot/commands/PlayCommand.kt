package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.PlayTime
import dev.kord.core.event.message.MessageCreateEvent

class PlayCommand(messageCreateEvent: MessageCreateEvent): BasePlayCommand(messageCreateEvent) {

    override val playTime: PlayTime
        get() = PlayTime.QUEUED
}