package com.azhapps.lagbot

import com.azhapps.lagbot.audio.AudioManager
import com.azhapps.lagbot.commands.Commands
import com.azhapps.lagbot.github.GithubRepository
import com.azhapps.lagbot.spotify.SpotifyRepository
import com.azhapps.lagbot.utils.PropertiesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.intent.Intent
import org.javacord.api.event.message.MessageEvent
import org.slf4j.LoggerFactory

object Main {

    lateinit var api: DiscordApi

    private val ioScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }
    private val githubRepository = GithubRepository(ioScope)
    private val spotifyRepository = SpotifyRepository(ioScope)
    private val audioManager = AudioManager(ioScope)
    private val commands = Commands(
        githubRepository = githubRepository,
        spotifyRepository = spotifyRepository,
        audioManager = audioManager
    )
    private val logger = LoggerFactory.getLogger(Main::class.java)

    fun isConnectedToVoice(event: MessageEvent) = event.server.get().getConnectedVoiceChannel(api.yourself).isPresent

    @JvmStatic
    fun main(args: Array<String>) {
        api = DiscordApiBuilder()
            .setToken(PropertiesUtil.get(PropertiesUtil.DISCORD_TOKEN))
            .setAllNonPrivilegedIntentsAnd(Intent.MESSAGE_CONTENT)
            .login()
            .join()

        logger.info("Lagbot loaded")

        api.yourself.connectedVoiceChannels.forEach { serverVoiceChannel ->
            logger.info("Reconnecting to channel in ${serverVoiceChannel.server.name}")
            serverVoiceChannel.connect().thenAccept {
                audioManager.connect(serverVoiceChannel.server, it)
            }
        }

        api.addMessageCreateListener {
            commands.handle(it)
        }
    }
}