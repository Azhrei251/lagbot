package com.azhapps.lagbot

import com.azhapps.lagbot.audio.AudioUtil
import com.azhapps.lagbot.commands.Commands
import com.azhapps.lagbot.utils.PropertiesUtil
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.schlaubi.lavakord.kord.lavakord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

object Main {

    private lateinit var kord: Kord

    val scope by lazy {
        CoroutineScope(newFixedThreadPoolContext(4, "API"))
    }

    private val logger = LoggerFactory.getLogger(Main::class.java)

    suspend fun isConnectedToVoice(event: MessageCreateEvent) = event.getGuildOrNull()?.voiceStates?.collect() != null

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            kord = Kord(PropertiesUtil.get(PropertiesUtil.DISCORD_TOKEN))

            kord.on<MessageCreateEvent> {
                Commands.handle(this)
            }

            kord.lavakord()

            logger.info("Lagbot loaded")

            /* api.yourself.connectedVoiceChannels.forEach { serverVoiceChannel ->
                 logger.info("Reconnecting to channel in ${serverVoiceChannel.server.name}")
                 serverVoiceChannel.connect().thenAccept {
                     AudioUtil.connect(serverVoiceChannel.server, it)
                 }
             } */

            kord.login {
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }
}