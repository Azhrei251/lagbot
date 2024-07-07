package com.azhapps.lagbot.audio

import com.azhapps.lagbot.utils.Utils
import dev.arbjerg.lavalink.protocol.v4.Track
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.Link

private const val MAX_MESSAGE_SIZE = 1750

class QueuedLink(
    val link: Link,
) {
    val player = link.player

    private val queue: ArrayDeque<Track> = ArrayDeque()
    private val loopQueue: ArrayDeque<Track> = ArrayDeque()

    suspend fun play(
        track: Track,
        playTime: PlayTime,
    ) {
        when (playTime) {
            PlayTime.QUEUED -> play(track)
            PlayTime.IMMEDIATE -> playImmediate(track)
            PlayTime.NEXT -> playNext(track)
        }
    }

   private suspend fun play(
        track: Track
    ): String? =
        if (!player.paused && player.playingTrack == null) {
            player.playTrack(track)
            null
        } else {
            queue.add(track)
            updateLoopIfRequired(track)
            getTrackQueueInfo(track)
        }


    private suspend fun playImmediate(track: Track): String? {
        link.state
        player.playTrack(track)
        updateLoopIfRequired(track)
        return null
    }

   private suspend fun playNext(track: Track): String {
        queue.addFirst(track)
        updateLoopIfRequired(track)
        return getTrackQueueInfo(track, 1)
    }

    private suspend fun playNextInQueue() {
        if (!queue.isEmpty()) {
            player.playTrack(queue.removeFirst())
        } else {
            if (loopQueue.isNotEmpty()) {
                queue.addAll(loopQueue.map {
                    it
                })
                player.playTrack(queue.removeFirst())
            } else {
                // TODO AudioUtil.setupTimeout(textChannel.asServerTextChannel().get().server)
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

    suspend fun printQueue() {
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
        // textChannel.sendMessage(messageText)
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

    fun remove(index: Int) = if (index < queue.size) {
        queue.removeAt(index)
        true
    } else {
        false
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

class LinkManager(
    private val lavaKord: LavaKord
) {
    private val guildLinkMap: MutableMap<ULong, QueuedLink> = mutableMapOf()

    // Attempts to retrieve the existing link for the given guild, if it doesn't exist, creates a new one
    fun getLink(guildId: ULong): QueuedLink = guildLinkMap[guildId] ?: QueuedLink(lavaKord.getLink(guildId)).apply {
        guildLinkMap[guildId] = this

        // TODO timeouts?
    }
}