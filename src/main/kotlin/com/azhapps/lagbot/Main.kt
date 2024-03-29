package com.azhapps.lagbot

import com.azhapps.lagbot.audio.AudioUtil
import com.azhapps.lagbot.commands.Commands
import com.azhapps.lagbot.utils.PropertiesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newFixedThreadPoolContext
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.intent.Intent
import org.javacord.api.event.message.MessageEvent
import org.slf4j.LoggerFactory

object Main {

    lateinit var api: DiscordApi

    val scope by lazy {
        CoroutineScope(newFixedThreadPoolContext(4, "API"))
    }

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
                AudioUtil.connect(serverVoiceChannel.server, it)
            }
        }

        api.addMessageCreateListener {
            Commands.handle(it)
        }
    }
}