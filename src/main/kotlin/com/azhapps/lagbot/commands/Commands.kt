package com.azhapps.lagbot.commands

import org.javacord.api.event.message.MessageCreateEvent

object Commands {

    private const val PREFIX = "!"

    fun handle(messageEvent: MessageCreateEvent) {
        get(messageEvent.messageContent)?.let { info ->
            when (info) {
                Info.PLAY -> PlayCommand(messageEvent).execute()

                Info.PLAY_NEXT -> PlayNextCommand(messageEvent).execute()

                Info.PLAY_NOW -> PlayNowCommand(messageEvent).execute()

                Info.HELP -> HelpCommand(messageEvent).execute()

                Info.SKIP -> SkipCommand(messageEvent).execute()

                Info.QUEUE -> QueueCommand(messageEvent).execute()

                Info.CLEAR -> ClearCommand(messageEvent).execute()

                Info.RESUME -> ResumeCommand(messageEvent).execute()

                Info.PAUSE -> PauseCommand(messageEvent).execute()

                Info.STOP -> StopCommand(messageEvent).execute()

                Info.REMOVE -> RemoveCommand(messageEvent).execute()
            }
        }
    }

    private fun get(string: String): Info? {
        val matchString = string.substringBefore(' ')

        return Info.values().firstOrNull { info ->
            info.keys.any {
                matchString == "$PREFIX${it}"
            }
        }
    }

    enum class Info(val keys: List<String>, val helpText: String) {
        HELP(listOf("help", "h"), "${PREFIX}help: Provides a list of commands and their uses"),
        PLAY(listOf("play", "p"), "${PREFIX}play {identifier}: Adds the requested song to the end of the queue"),
        PLAY_NEXT(listOf("playnext", "pn"), "${PREFIX}playnext {identifier}: Adds the requested song to the front of the queue"),
        PLAY_NOW(listOf("playnow"), "${PREFIX}playnow {identifier}: Immediately plays the requested song"),
        STOP(listOf("stop", "s"), "${PREFIX}stop: Stops the music playback and clears the queue"),
        PAUSE(listOf("pause", "p"), "${PREFIX}pause: Pauses the music playback"),
        RESUME(listOf("resume", "r"), "${PREFIX}resume: Resumes the music playback"),
        QUEUE(listOf("queue", "q"), "${PREFIX}queue: Displays the current queue"),
        SKIP(listOf("skip"), "${PREFIX}skip: Skips the currently playing song"),
        CLEAR(listOf("clear"), "${PREFIX}clear: Clears the queue"),
        REMOVE(listOf("remove"), "${PREFIX}remove {index}: Removes the song at the given index"),
    }
}