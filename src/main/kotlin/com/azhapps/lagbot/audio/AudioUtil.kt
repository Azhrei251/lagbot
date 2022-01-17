package com.azhapps.lagbot.audio

import com.azhapps.lagbot.Main
import com.azhapps.lagbot.utils.PropertiesUtil
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.javacord.api.audio.AudioConnection
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.server.Server
import org.slf4j.LoggerFactory
import java.net.MalformedURLException
import java.net.URL
import java.util.*

object AudioUtil {
    private val logger = LoggerFactory.getLogger(AudioUtil::class.java)
    private val playerMap = mutableMapOf<Long, AudioPlayer>()
    private val schedulerMap = mutableMapOf<Long, TrackScheduler>()
    private val connectionMap = mutableMapOf<Long, AudioConnection>()
    private val playerManager by lazy {
        DefaultAudioPlayerManager().apply {
            registerSourceManager(YoutubeAudioSourceManager())
            registerSourceManager(BandcampAudioSourceManager())
            registerSourceManager(VimeoAudioSourceManager())
            registerSourceManager(TwitchStreamAudioSourceManager())
        }
    }

    fun connect(server: Server, audioConnection: AudioConnection) {
        val player = playerMap[server.id] ?: playerManager.createPlayer().apply {
            playerMap[server.id] = this
        }
        audioConnection.setAudioSource(LavaPlayerAudioSource(Main.api, player))
        connectionMap[server.id] = audioConnection
        setupTimeout(server)
    }

    fun disconnect(server: Server) {
        connectionMap[server.id]?.close()
    }

    fun setupTimeout(server: Server) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (playerMap[server.id]?.playingTrack == null) {
                    disconnect(server)
                    logger.info("Disconnected from server ${server.name}")
                }
            }
        }, PropertiesUtil.get(PropertiesUtil.AFK_TIMEOUT).toLong())
    }

    fun playSong(server: Server, identifier: String, textChannel: TextChannel, playTime: PlayTime) {
        val player = playerMap[server.id]!!
        val scheduler = schedulerMap[server.id] ?: TrackScheduler(player, textChannel).apply {
            schedulerMap[server.id] = this
            player.addListener(this)
        }

        //If we've got a valid URL, try to load it. Otherwise, do a youtube search
        var isSearch = false
        val identifierToUse = try {
            URL(identifier)
            identifier
        } catch (e: MalformedURLException) {
            isSearch = true
            "ytsearch:${identifier}"
        }

        playerManager.loadItem(identifierToUse, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                val message = when (playTime) {
                    PlayTime.IMMEDIATE -> scheduler.playImmediate(track)
                    PlayTime.NEXT -> scheduler.playNext(track)
                    PlayTime.QUEUED -> scheduler.addToQueue(track)
                }
                textChannel.sendMessage(message)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                //Search results are returned as a playlist oddly. Only add the first. Otherwise add the whole playlist
                if (isSearch) {
                    textChannel.sendMessage(scheduler.addToQueue(playlist.tracks.first()))

                } else {
                    var message = "```"
                    for (track in playlist.tracks) {
                       message += "${scheduler.addToQueue(track)}\n"
                    }
                    message += "```"
                    textChannel.sendMessage(message)
                }
            }

            override fun noMatches() {
                textChannel.sendMessage("Could not find that song")
            }

            override fun loadFailed(exception: FriendlyException?) {
                textChannel.sendMessage("Something went wrong" + exception.toString())
            }

        })
    }

    fun getScheduler(server: Server) = schedulerMap[server.id]

    enum class PlayTime {
        QUEUED, IMMEDIATE, NEXT
    }
}
