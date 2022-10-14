package com.azhapps.lagbot.audio

import com.azhapps.lagbot.utils.Utils
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import org.javacord.api.entity.channel.TextChannel

class TrackScheduler(private val player: AudioPlayer, private val textChannel: TextChannel) : AudioEventAdapter() {

    private val queue: ArrayDeque<AudioTrack> = ArrayDeque()

    fun queueSize() = queue.size

    fun addToQueue(audioTrack: AudioTrack): String? =
        if (!player.isPaused && player.playingTrack == null) {
            player.playTrack(audioTrack)
            null
        } else {
            queue.add(audioTrack)
            getTrackQueueInfo(audioTrack)
        }

    private fun getTrackQueueInfo(audioTrack: AudioTrack) =
        "Added ${audioTrack.info.title} to queue at position ${queue.size}"

    fun playImmediate(audioTrack: AudioTrack): String? {
        player.playTrack(audioTrack)
        return null
    }

    fun playNext(audioTrack: AudioTrack): String {
        queue.addFirst(audioTrack)
        return getTrackQueueInfo(audioTrack)
    }

    private fun playNextInQueue() {
        if (!queue.isEmpty()) {
            player.playTrack(queue.removeFirst())
        } else {
            AudioUtil.setupTimeout(textChannel.asServerTextChannel().get().server)
        }
    }

    fun skip() {
        if (queue.isEmpty()) {
            player.stopTrack()
        } else {
            playNextInQueue()
        }
    }

    fun printQueue() {
        var messageText = "```"
        var queueDuration = 0L
        if (player.playingTrack != null) {
            messageText += "Currently playing: ${player.playingTrack.info.title}\n\n"
            queueDuration = player.playingTrack.duration - player.playingTrack.position
        }

        if (queue.isEmpty()) {
            messageText += "Queue empty"

        } else {
            queue.forEachIndexed { i, it ->
                queueDuration += it.duration
                messageText += "${i + 1}: ${it.info.title} | ${Utils.formatTimeStamp(it.duration)}\n"
          L  }
        }
        messageText += "\nQueue duration: ${Utils.formatTimeStamp(queueDuration)}"
        messageText += "```"
        textChannel.sendMessage(messageText)
    }

    fun pause() {
        (player as? DefaultAudioPlayer)?.isPaused = true
    }

    fun resume() {
        (player as? DefaultAudioPlayer)?.isPaused = false
    }

    fun stop() {
        player.stopTrack()
        queue.clear()
    }

    fun clear() {
        queue.clear()
    }

    fun remove(index: Int) = if (index < queue.size) {
        queue.removeAt(index)
        true
    } else {
        false
    }

    override fun onPlayerPause(player: AudioPlayer) {
        AudioUtil.setupTimeout(textChannel.asServerTextChannel().get().server)
    }

    override fun onPlayerResume(player: AudioPlayer) {
        if (player.playingTrack == null) {
            playNextInQueue()
        }
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        textChannel.sendMessage("Playing: ${track.info?.title}")
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) {
            playNextInQueue()
        } else {
            AudioUtil.setupTimeout(textChannel.asServerTextChannel().get().server)
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
        playNextInQueue()
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack?, thresholdMs: Long) {
        playNextInQueue()
    }
}