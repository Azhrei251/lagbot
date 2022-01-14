package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import org.javacord.api.event.message.MessageCreateEvent

class PlayNowCommand(messageCreateEvent: MessageCreateEvent): BasePlayCommand(messageCreateEvent) {

    override val playTime: AudioUtil.PlayTime
        get() = AudioUtil.PlayTime.IMMEDIATE
}