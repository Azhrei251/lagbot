package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.AudioManager
import com.azhapps.lagbot.audio.PlayTime
import com.azhapps.lagbot.commands.model.CommandContext
import com.azhapps.lagbot.github.GithubRepository
import com.azhapps.lagbot.spotify.SpotifyRepository
import org.javacord.api.event.message.MessageCreateEvent

private const val PREFIX = "!"

class Commands(
    internal val audioManager: AudioManager,
    internal val githubRepository: GithubRepository,
    internal val spotifyRepository: SpotifyRepository,
) {

    fun handle(messageEvent: MessageCreateEvent) {
        get(messageEvent.messageContent)?.let { info ->
            val context = CommandContext(
                onResponse = { messageEvent.channel.sendMessage(it) },
                scheduler = audioManager.getScheduler(messageEvent.server.get()),
                arguments = messageEvent.messageContent.substringAfter(' ')
            )
            // Always update scheduler's text channel to the most recently requeusted channel.
            context.scheduler?.updateTextChannel(messageEvent.channel)

            when (info) {
                Info.PLAY -> play(
                    context = context,
                    playTime = PlayTime.QUEUED,
                    server = messageEvent.server.get(),
                    textChannel = messageEvent.channel,
                    voiceChannel = messageEvent.messageAuthor.connectedVoiceChannel,
                    requesterInVoice = messageEvent.messageAuthor.connectedVoiceChannel.isPresent
                )

                Info.PLAY_NEXT -> play(
                    context = context,
                    playTime = PlayTime.NEXT,
                    server = messageEvent.server.get(),
                    textChannel = messageEvent.channel,
                    voiceChannel = messageEvent.messageAuthor.connectedVoiceChannel,
                    requesterInVoice = messageEvent.messageAuthor.connectedVoiceChannel.isPresent
                )

                Info.PLAY_NOW -> play(
                    context = context,
                    playTime = PlayTime.IMMEDIATE,
                    server = messageEvent.server.get(),
                    textChannel = messageEvent.channel,
                    voiceChannel = messageEvent.messageAuthor.connectedVoiceChannel,
                    requesterInVoice = messageEvent.messageAuthor.connectedVoiceChannel.isPresent
                )

                Info.HELP -> help(context)

                Info.SKIP -> skip(context)

                Info.QUEUE -> queue(context)

                Info.CLEAR -> clear(context)

                Info.RESUME -> resume(context)

                Info.PAUSE -> pause(context)

                Info.STOP -> stop(context)

                Info.REMOVE -> remove(context)

                Info.LOOP -> loop(context)

                Info.STOP_LOOP -> loopStop(context)

                Info.ISSUE -> createIssue(
                    context,
                    "${messageEvent.messageAuthor.discriminatedName} | ${messageEvent.messageAuthor.displayName}"
                )
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