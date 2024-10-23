package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioManager
import com.azhapps.lagbot.audio.PlayTime
import kotlinx.coroutines.CoroutineScope
import org.javacord.api.event.message.MessageCreateEvent

class PlayNowCommand(
    messageCreateEvent: MessageCreateEvent,
    audioManager: AudioManager,
    scope: CoroutineScope,
): BasePlayCommand(messageCreateEvent, audioManager, scope) {

    override val playTime: PlayTime
        get() = PlayTime.IMMEDIATE
}