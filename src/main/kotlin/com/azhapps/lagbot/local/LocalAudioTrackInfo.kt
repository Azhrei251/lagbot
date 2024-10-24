package com.azhapps.lagbot.local

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo

data class LocalAudioTrackInfo(
    val trackInfo: AudioTrackInfo,
    val filePath: String,
)