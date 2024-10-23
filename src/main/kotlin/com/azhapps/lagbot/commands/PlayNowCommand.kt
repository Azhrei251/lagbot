package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import kotlinx.coroutines.CoroutineScope
import org.javacord.api.event.message.MessageCreateEvent

class PlayNowCommand(
    messageCreateEvent: MessageCreateEvent,
    scope: CoroutineScope,
): BasePlayCommand(messageCreateEvent, scope) {

    override val playTime: AudioUtil.PlayTime
        get() = AudioUtil.PlayTime.IMMEDIATE
}