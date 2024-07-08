package com.azhapps.lagbot.audio

import com.azhapps.lagbot.utils.PropertiesUtil
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.Link
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LinkManager(
    private val lavaKord: LavaKord,
    private val scope: CoroutineScope
) {
    private val guildLinkMap: MutableMap<ULong, QueuedLink> = mutableMapOf()
    private val timeoutMillis = PropertiesUtil.get(PropertiesUtil.AFK_TIMEOUT).toLong()

    init {
        scope.launch {
            while (true) {
                checkQueue()
                delay(10)
            }
        }
    }

    // Attempts to retrieve the existing link for the given guild, if it doesn't exist, creates a new one
    fun getLink(guildId: ULong): QueuedLink = guildLinkMap[guildId] ?: QueuedLink(lavaKord.getLink(guildId)).apply {
        guildLinkMap[guildId] = this
    }

    private suspend fun checkQueue() {
        guildLinkMap.values.forEach {
            if (it.state == Link.State.CONNECTED) {
                val playingTrack = it.player.playingTrack
                if (it.hasQueuedItems() && (playingTrack == null || playingTrack.info.length <= it.player.position)) {
                    scope.launch { it.playNextInQueue() }
                }

                val latestValidTimeMillis = System.currentTimeMillis() - timeoutMillis
                if (
                    it.songLastFinishedAt != -1L &&
                    it.songLastFinishedAt <= latestValidTimeMillis
                ) {
                    scope.launch { it.disconnectAudio() }
                }
            }
        }
    }
}