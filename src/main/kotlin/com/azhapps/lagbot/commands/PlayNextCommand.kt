package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioUtil
import kotlinx.coroutines.CoroutineScope
import org.javacord.api.event.message.MessageCreateEvent

class PlayNextCommand(
    messageCreateEvent: MessageCreateEvent,
    scope: CoroutineScope,
): BasePlayCommand(messageCreateEvent, scope) {

    override val playTime: AudioUtil.PlayTime
        get() = AudioUtil.PlayTime.NEXT
}