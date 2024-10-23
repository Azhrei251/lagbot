package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioManager
import com.azhapps.lagbot.audio.PlayTime
import com.azhapps.lagbot.github.GithubRepository
import com.azhapps.lagbot.spotify.SpotifyRepository
import kotlinx.coroutines.CoroutineScope
import org.javacord.api.event.message.MessageCreateEvent

private const val PREFIX = "!"

class Commands(
    internal val audioManager: AudioManager,
    internal val githubRepository: GithubRepository,
    internal val spotifyRepository: SpotifyRepository,
) {

    fun handle(messageEvent: MessageCreateEvent) {
        get(messageEvent.messageContent)?.let { info ->
            when (info) {
                Info.PLAY -> play(messageEvent, PlayTime.QUEUED)

                Info.PLAY_NEXT -> play(messageEvent, PlayTime.NEXT)

                Info.PLAY_NOW -> play(messageEvent, PlayTime.IMMEDIATE)

                Info.HELP -> help(messageEvent)

                Info.SKIP -> skip(messageEvent)

                Info.QUEUE -> queue(messageEvent)

                Info.CLEAR -> clear(messageEvent)

                Info.RESUME -> resume(messageEvent)

                Info.PAUSE -> pause(messageEvent)

                Info.STOP -> stop(messageEvent)

                Info.REMOVE -> remove(messageEvent)

                Info.LOOP -> loop(messageEvent)

                Info.STOP_LOOP -> loopStop(messageEvent)

                Info.ISSUE -> createIssue(messageEvent)
            }
        }
    }

    private fun get(string: String): Info? {
        val matchString = string.substringBefore(' ')

        return Info.entries.firstOrNull { info ->
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