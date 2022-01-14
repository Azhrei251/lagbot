package com.azhapps.lagbot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import org.javacord.api.audio.AudioSourceBase
import org.javacord.api.DiscordApi

import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame

class LavaPlayerAudioSource(api: DiscordApi, private val audioPlayer: AudioPlayer) : AudioSourceBase(api) {

    private var lastFrame: AudioFrame? = null

    override fun getNextFrame(): ByteArray? {
        return if (lastFrame == null) {
            null
        } else applyTransformers(lastFrame!!.data)
    }

    override fun hasFinished() = false

    override fun hasNextFrame(): Boolean {
        lastFrame = audioPlayer.provide()
        return lastFrame != null
    }

    override fun copy() = LavaPlayerAudioSource(api, audioPlayer)
}