package com.azhapps.lagbot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import org.javacord.api.entity.channel.TextChannel
import java.util.*
import kotlin.collections.ArrayDeque

class TrackScheduler(private val player: AudioPlayer, private val textChannel: TextChannel) : AudioEventAdapter() {

    private val queue: ArrayDeque<AudioTrack> = ArrayDeque()

    fun addToQueue(audioTrack: AudioTrack) {
        if (!player.isPaused && player.playingTrack == null) {
            player.playTrack(audioTrack)
        } else {
            queue.add(audioTrack)
            textChannel.sendMessage("Added ${audioTrack.info.title} to queue at position ${queue.size}")
        }
    }

    fun playImmediate(audioTrack: AudioTrack) {
        player.playTrack(audioTrack)
    }

    fun playNext(audioTrack: AudioTrack) {
        queue.addFirst(audioTrack)
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
        if (player.playingTrack != null) {
            messageText += "Currently playing: ${player.playingTrack.info.title}\n\n"
        }

        if (queue.isEmpty()) {
            messageText += "Queue empty"

        } else {
            queue.forEachIndexed { i, it ->
                messageText += "${i + 1}: ${it.info.title}\n"
            }
        }
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