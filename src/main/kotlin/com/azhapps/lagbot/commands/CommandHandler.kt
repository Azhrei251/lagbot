package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.LinkManager
import com.azhapps.lagbot.audio.PlayTime
import com.azhapps.lagbot.audio.QueuedLink
import com.azhapps.lagbot.github.GithubRepository
import com.azhapps.lagbot.spotify.SpotifyRepository
import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.arbjerg.lavalink.protocol.v4.Track
import dev.schlaubi.lavakord.audio.Link

data class CommandInfo(
    val guildId: ULong,
    val voiceChannelId: ULong?,
    val messageContent: String,
    val messageAuthor: String,
)

class CommandHandler(
    private val linkManager: LinkManager
) {

    suspend fun handle(
        command: Command?,
        info: CommandInfo,
        onResponse: suspend (String) -> Unit
    ) {
        when (command) {
            Command.PLAY -> playTrack(info, PlayTime.QUEUED, onResponse)

            Command.PLAY_NEXT -> playTrack(info, PlayTime.NEXT, onResponse)

            Command.PLAY_NOW -> playTrack(info, PlayTime.IMMEDIATE, onResponse)

            Command.HELP -> help(onResponse)

            // START TODO responses for all of these
            Command.SKIP -> linkManager.getLink(info.guildId).skip()

            Command.QUEUE -> linkManager.getLink(info.guildId).printQueue(onResponse)

            Command.CLEAR -> linkManager.getLink(info.guildId).clear()

            Command.RESUME -> linkManager.getLink(info.guildId).resume()

            Command.PAUSE -> linkManager.getLink(info.guildId).pause()

            Command.LOOP -> linkManager.getLink(info.guildId).loop()

            Command.STOP_LOOP -> linkManager.getLink(info.guildId).stopLoop()
            // END TODO

            Command.STOP -> stop(info, onResponse)

            Command.REMOVE -> remove(info, onResponse)

            Command.ISSUE -> createGithubIssue(info, onResponse)

            null -> onResponse("Unknown command")
        }
    }

    private suspend fun help(onResponse: suspend (String) -> Unit) {
        var messageText = "```"
        Command.values().forEach {
            messageText += "${it.description}\n"
        }
        messageText += "```"
        onResponse(messageText)
    }

    private suspend fun playTrack(
        info: CommandInfo,
        playTime: PlayTime,
        onResponse: suspend (String) -> Unit
    ) {
        if (info.voiceChannelId == null) {
            onResponse("Join a voice channel first!")
        } else {
            val link = linkManager.getLink(info.guildId)
            if (link.state != Link.State.CONNECTED) {
                link.connectAudio(info.voiceChannelId)
            }

            if (info.messageContent.contains("open.spotify.com")) {
                // TODO probably want to inject rather than object ref
                SpotifyRepository.getSearchTerms(info.messageContent).run {
                    forEach {
                        loadSong(link, it, playTime, onResponse)
                    }
                    onResponse("${this.size} tracks added to queue")
                }
            } else {
                loadSong(link, info.messageContent, playTime, onResponse)
            }
        }
    }

    private suspend fun loadSong(
        link: QueuedLink,
        request: String,
        playTime: PlayTime,
        onResponse: suspend (String) -> Unit
    ) {
        var loadPlayList = true
        val search = if (request.startsWith("http")) {
            request
        } else {
            loadPlayList = false
            "ytsearch:$request"
        }

        when (val item = link.loadItem(search)) {
            is LoadResult.TrackLoaded -> playSong(link, item.data, playTime, onResponse)
            is LoadResult.PlaylistLoaded -> {
                if (loadPlayList) {
                    item.data.tracks.forEach {
                        link.play(it, PlayTime.QUEUED)
                    }
                    onResponse("${item.data.tracks.size} tracks added to queue")
                } else {
                    playSong(link, item.data.tracks.first(), playTime, onResponse)
                }
            }

            is LoadResult.SearchResult -> playSong(link, item.data.tracks.first(), playTime, onResponse)
            is LoadResult.NoMatches -> onResponse("No song found")
            is LoadResult.LoadFailed -> onResponse(item.data.message ?: "Error encountered")
        }
    }

    private suspend fun playSong(
        link: QueuedLink,
        track: Track,
        playTime: PlayTime,
        onResponse: suspend (String) -> Unit
    ) {
        val response = link.play(track, playTime)
        if (response != null) {
            onResponse(response)
        }
    }

    private suspend fun stop(
        info: CommandInfo,
        onResponse: suspend (String) -> Unit
    ) {
        val link = linkManager.getLink(info.guildId)
        if (link.player.playingTrack == null) {
            onResponse("Nothing to stop!")
        } else {
            link.stop()
            onResponse("Stopped")
        }
    }

    private suspend fun remove(info: CommandInfo, onResponse: suspend (String) -> Unit) {
        val index = info.messageContent.toIntOrNull()
        if (index == null) {
            onResponse("Invalid command content: ${info.messageContent}")
        } else {
            val removed = linkManager.getLink(info.guildId).remove(index)
            if (removed.first) {
                onResponse("Removed song ${removed.second?.info?.title} at position $index")
            } else {
                onResponse("Given index $index was outside the range of the queue")
            }
        }
    }

    private suspend fun createGithubIssue(
        info: CommandInfo,
        onResponse: suspend (String) -> Unit
    ) {
        val decomposed = info.messageContent.split(' ')

        GithubRepository.createIssue(
            title = decomposed.slice(IntRange(0, decomposed.size - 1)).joinToString(" "),
            body = "Created by lagbot. Requesting user: ${info.messageAuthor}"
        ) {
            onResponse(it)
        }
    }
}