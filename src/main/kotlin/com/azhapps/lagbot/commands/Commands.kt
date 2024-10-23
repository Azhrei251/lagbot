package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioManager
import com.azhapps.lagbot.github.GithubRepository
import kotlinx.coroutines.CoroutineScope
import org.javacord.api.event.message.MessageCreateEvent

private const val PREFIX = "!"

class Commands(
    private val scope: CoroutineScope,
    private val githubRepository: GithubRepository,
    private val audioManager: AudioManager,
) {

    fun handle(messageEvent: MessageCreateEvent) {
        get(messageEvent.messageContent)?.let { info ->
            when (info) {
                Info.PLAY -> PlayCommand(messageEvent, audioManager, scope).execute()

                Info.PLAY_NEXT -> PlayNextCommand(messageEvent, audioManager, scope).execute()

                Info.PLAY_NOW -> PlayNowCommand(messageEvent, audioManager, scope).execute()

                Info.HELP -> HelpCommand(messageEvent).execute()

                Info.SKIP -> SkipCommand(audioManager, messageEvent).execute()

                Info.QUEUE -> QueueCommand(audioManager, messageEvent).execute()

                Info.CLEAR -> ClearCommand(audioManager, messageEvent).execute()

                Info.RESUME -> ResumeCommand(audioManager, messageEvent).execute()

                Info.PAUSE -> PauseCommand(audioManager, messageEvent).execute()

                Info.STOP -> StopCommand(audioManager, messageEvent).execute()

                Info.REMOVE -> RemoveCommand(audioManager, messageEvent).execute()

                Info.LOOP -> LoopCommand(audioManager, messageEvent).execute()

                Info.STOP_LOOP -> LoopStopCommand(audioManager, messageEvent).execute()

                Info.ISSUE -> CreateIssueCommand(messageEvent, githubRepository).execute()
            }
        }
    }

    private fun get(string: String): Info? {
        val matchString = string.substringBefore(' ')

        return Info.values().firstOrNull { info ->
            info.keys.any {
                matchString.equals("$PREFIX${it}", ignoreCase = true)
            }
        }
    }

    enum class Info(val keys: List<String>, val helpText: String) {
        HELP(listOf("help", "h"), "${PREFIX}help: Provides a list of commands and their uses"),
        PLAY(listOf("play", "p"), "${PREFIX}play {identifier}: Adds the requested song to the end of the queue"),
        PLAY_NEXT(
            listOf("playnext", "pn"),
            "${PREFIX}playnext {identifier}: Adds the requested song to the front of the queue"
        ),
        PLAY_NOW(listOf("playnow"), "${PREFIX}playnow {identifier}: Immediately plays the requested song"),
        STOP(listOf("stop", "s"), "${PREFIX}stop: Stops the music playback and clears the queue"),
        PAUSE(listOf("pause", "p"), "${PREFIX}pause: Pauses the music playback"),
        RESUME(listOf("resume", "r"), "${PREFIX}resume: Resumes the music playback"),
        QUEUE(listOf("queue", "q"), "${PREFIX}queue: Displays the current queue"),
        SKIP(listOf("skip"), "${PREFIX}skip: Skips the currently playing song"),
        CLEAR(listOf("clear"), "${PREFIX}clear: Clears the queue"),
        REMOVE(listOf("remove"), "${PREFIX}remove {index}: Removes the song at the given index"),
        LOOP(listOf("loop", "l"), "${PREFIX}loop: Repeats the current queue... repeatedly"),
        STOP_LOOP(listOf("stoploop"), "${PREFIX}stoploop: Stop looping"),
        ISSUE(listOf("issue"), "${PREFIX}issue {title}: Create a new issue on github"),
    }
}