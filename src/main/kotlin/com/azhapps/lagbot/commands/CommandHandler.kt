package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.LinkManager
import com.azhapps.lagbot.audio.PlayTime
import com.azhapps.lagbot.audio.QueuedLink
import com.azhapps.lagbot.github.GithubRepository
import com.azhapps.lagbot.spotify.SpotifyRepository
import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.rest.loadItem

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

            Command.SKIP -> TODO()

            Command.QUEUE -> TODO()

            Command.CLEAR -> TODO()

            Command.RESUME -> TODO()

            Command.PAUSE -> TODO()

            Command.STOP -> stop(info, onResponse)

            Command.REMOVE -> TODO()

            Command.LOOP -> TODO()

            Command.STOP_LOOP -> TODO()

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
            if (link.link.state != Link.State.CONNECTED) {
                link.link.connectAudio(info.voiceChannelId)
            }

            if (info.messageContent.contains("open.spotify.com")) {
                // TODO probably want to inject rather than object ref
                SpotifyRepository.getSearchTerms(info.messageContent).run {
                    forEach {
                        playSong(link, it) {
                            // Do nothing
                        }
                    }
                    onResponse("${this.size} tracks added to queue")
                }

            } else {
                var loadPlayList = true
                val player = link.player
                val search = if (request.startsWith("http")) {
                    info.messageContent
                } else {
                    loadPlayList = false
                    "ytsearch:$request"
                }

                when (val item = link.link.loadItem(search)) {
                    is LoadResult.TrackLoaded -> link.play(track = item.data, playTime)
                    is LoadResult.PlaylistLoaded -> link.play(track = item.data.tracks.first(), playTime)
                    is LoadResult.SearchResult -> link.play(item.data.tracks.first(), playTime)
                    is LoadResult.NoMatches -> onTrackLoaded("No song found")
                    is LoadResult.LoadFailed -> onTrackLoaded(item.data.message ?: "Error encountered")
                }

                playSong(link, info.messageContent) {
                    onResponse(it)
                }
            }
        }
    }

    private suspend fun playSong(link: QueuedLink, request: String, onTrackLoaded: suspend (String) -> Unit) {
        var loadPlayList = true
        val player = link.player
        val search = if (request.startsWith("http")) {
            request
        } else {
            loadPlayList = false
            "ytsearch:$request"
        }
        when (val item = link.link.loadItem(search)) {
            is LoadResult.TrackLoaded -> player.playTrack(track = item.data)
            is LoadResult.PlaylistLoaded -> player.playTrack(track = item.data.tracks.first())
            is LoadResult.SearchResult -> player.playTrack(item.data.tracks.first())
            is LoadResult.NoMatches -> onTrackLoaded("No song found")
            is LoadResult.LoadFailed -> onTrackLoaded(item.data.message ?: "Error encountered")
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
            link.player.stopTrack()
            onResponse("Stopped")
        }
        /*val scheduler = AudioUtil.getScheduler(event.server.get())
        if (scheduler == null) {
            event.message.channel.createMessage()
        } else {
            event.message.channel.createMessage("Stopped")
            scheduler.stop()
        }*/
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