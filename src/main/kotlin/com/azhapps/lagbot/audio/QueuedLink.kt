package com.azhapps.lagbot.audio

import com.azhapps.lagbot.utils.Utils
import dev.arbjerg.lavalink.protocol.v4.Track
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.rest.loadItem

private const val MAX_MESSAGE_SIZE = 1750

class QueuedLink(
    private val link: Link,
) {
    val player = link.player
    var state = Link.State.NOT_CONNECTED
    var songLastStartedAt: Long = -1L

    private val queue: ArrayDeque<Track> = ArrayDeque()
    private val loopQueue: ArrayDeque<Track> = ArrayDeque()

    suspend fun loadItem(query: String) = link.loadItem(query)

    suspend fun connectAudio(voiceChannelId: ULong) {
        state = Link.State.CONNECTED
        link.connectAudio(voiceChannelId)
    }

    suspend fun disconnectAudio() {
        state = Link.State.NOT_CONNECTED
        link.disconnectAudio()
    }

    fun hasQueuedItems() = queue.isNotEmpty() || loopQueue.isNotEmpty()

    suspend fun play(
        track: Track,
        playTime: PlayTime,
    ): String? {
        return when (playTime) {
            PlayTime.QUEUED -> play(track)
            PlayTime.IMMEDIATE -> playImmediate(track)
            PlayTime.NEXT -> playNext(track)
        }
    }

    private suspend fun play(
        track: Track
    ): String? =
        if (!player.paused && player.playingTrack == null) {
            songLastStartedAt = System.currentTimeMillis()
            player.playTrack(track)
            null
        } else {
            queue.add(track)
            updateLoopIfRequired(track)
            getTrackQueueInfo(track)
        }

    private suspend fun playImmediate(track: Track): String? {
        songLastStartedAt = System.currentTimeMillis()
        player.playTrack(track)
        updateLoopIfRequired(track)
        return null
    }

    private suspend fun playNext(track: Track): String {
        queue.addFirst(track)
        updateLoopIfRequired(track)
        return getTrackQueueInfo(track, 1)
    }

    suspend fun playNextInQueue(): Boolean {
        return if (!queue.isEmpty()) {
            songLastStartedAt = System.currentTimeMillis()
            player.playTrack(queue.removeFirst())
            true
        } else {
            if (loopQueue.isNotEmpty()) {
                queue.addAll(loopQueue.map {
                    it
                })
                player.playTrack(queue.removeFirst())
                true
            } else {
               false
            }
        }
    }

    suspend fun skip() {
        if (queue.isEmpty() && loopQueue.isEmpty()) {
            player.stopTrack()
        } else {
            playNextInQueue()
        }
    }

    suspend fun printQueue(onResponse: suspend (String) -> Unit) {
        var messageText = "```"
        var queueDuration = 0L
        if (player.playingTrack != null) {
            messageText += "Currently playing: ${player.playingTrack?.info?.title}\n\n"
            queueDuration = (player.playingTrack?.info?.length ?: 0) - player.position
        }

        if (queue.isEmpty()) {
            messageText += "Queue empty"

        } else {
            var excessSongs = 0

            queue.forEachIndexed { i, it ->
                val nextMessage = "${i + 1}: ${it.info.title} | ${Utils.formatTimeStamp(it.info.length)}\n"

                queueDuration += it.info.length

                if (messageText.length + nextMessage.length > MAX_MESSAGE_SIZE) {
                    excessSongs++
                } else {
                    messageText += nextMessage
                }
            }

            if (excessSongs > 0) {
                messageText += "\n$excessSongs more songs...\n"
            }
        }
        messageText += "\nQueue duration: ${Utils.formatTimeStamp(queueDuration)}"
        messageText += "```"
        onResponse(messageText)
    }

    suspend fun pause() {
        player.pause(true)
    }

    suspend fun resume() {
        player.unPause()
    }

    suspend fun stop() {
        player.stopTrack()
        queue.clear()
        loopQueue.clear()
    }

    fun clear() {
        queue.clear()
        loopQueue.clear()
    }

    fun remove(index: Int): Pair<Boolean, Track?> = if (index < queue.size) {
        Pair(true, queue.removeAt(index))
    } else {
        Pair(false, null)
    }

    fun loop(): Int {
        loopQueue.clear()
        player.playingTrack?.let {
            loopQueue.add(it)
        }
        loopQueue.addAll(queue)
        return loopQueue.size
    }

    fun stopLoop() {
        loopQueue.clear()
    }

    private fun updateLoopIfRequired(track: Track) {
        if (loopQueue.isNotEmpty()) {
            loopQueue.add(track)
        }
    }

    private fun getTrackQueueInfo(track: Track, position: Int = queue.size) =
        "Added ${track.info.title} to queue at position $position"
}