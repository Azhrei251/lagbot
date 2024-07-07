package com.azhapps.lagbot.commands

private const val PREFIX = "!"
const val QUERY = "query"

enum class Command(val path: List<String>, val description: String, val hasQuery: Boolean = true) {
    HELP(listOf("help", "h"), "help: Provides a list of commands and their uses", false),
    PLAY(listOf("play", "p"), "play {identifier}: Adds the requested song to the end of the queue"),
    PLAY_NEXT(
        listOf("playnext", "pn"),
        "playnext {identifier}: Adds the requested song to the front of the queue"
    ),
    PLAY_NOW(listOf("playnow"), "playnow {identifier}: Immediately plays the requested song"),
    STOP(listOf("stop", "s"), "stop: Stops the music playback and clears the queue", false),
    PAUSE(listOf("pause", "p"), "pause: Pauses the music playback", false),
    RESUME(listOf("resume", "r"), "resume: Resumes the music playback", false),
    QUEUE(listOf("queue", "q"), "queue: Displays the current queue", false),
    SKIP(listOf("skip"), "skip: Skips the currently playing song", false),
    CLEAR(listOf("clear"), "clear: Clears the queue"),
    REMOVE(listOf("remove"), "remove {index}: Removes the song at the given index"),
    LOOP(listOf("loop", "l"), "loop: Repeats the current queue... repeatedly", false),
    STOP_LOOP(listOf("stoploop"), "stoploop: Stop looping", false),
    ISSUE(listOf("issue"), "issue {title}: Create a new issue on github"),
}

fun String.command(): Command? {
    val matchString = substringBefore(' ').removePrefix("!")

    return Command.values().firstOrNull { info ->
        info.path.any {
            matchString.equals(it, ignoreCase = true)
        }
    }
}
