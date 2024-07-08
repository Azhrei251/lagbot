package com.azhapps.lagbot

import com.azhapps.lagbot.audio.LinkManager
import com.azhapps.lagbot.commands.*
import com.azhapps.lagbot.utils.PropertiesUtil
import com.azhapps.lagbot.utils.displayUsername
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.kord.lavakord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

object Main {

    private lateinit var kord: Kord
    private lateinit var lavaKord: LavaKord
    private lateinit var linkManager: LinkManager
    private lateinit var commandHandler: CommandHandler

    val scope by lazy {
        CoroutineScope(newFixedThreadPoolContext(4, "API"))
    }

    private val logger = LoggerFactory.getLogger(Main::class.java)

    suspend fun isConnectedToVoice(event: MessageCreateEvent) = event.getGuildOrNull()?.voiceStates?.collect() != null

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            kord = Kord(PropertiesUtil.get(PropertiesUtil.DISCORD_TOKEN))

            lavaKord = kord.lavakord().apply {
                addNode(
                    PropertiesUtil.get(PropertiesUtil.LAVALINK_URL),
                    PropertiesUtil.get(PropertiesUtil.LAVALINK_PASSWORD)
                )
            }

            linkManager = LinkManager(lavaKord, scope)
            commandHandler = CommandHandler(linkManager)

            /* TODO Investigate these later
            kord.createGlobalApplicationCommands {
                Command.values().forEach {
                    input(it.path.first(), it.description) {
                        if (it.hasQuery) {
                            string(QUERY, "The query for your command")
                        }
                    }
                }
            }*/

            kord.on<GuildChatInputCommandInteractionCreateEvent> {
                val info  = CommandInfo(
                    messageContent = interaction.command.options[QUERY]?.value.toString(),
                    messageAuthor = interaction.user.displayUsername(),
                    guildId = interaction.guildId.value,
                    voiceChannelId = interaction.user.getVoiceStateOrNull()?.channelId?.value
                )
                val command = interaction.command.rootName.command()
                val ack = interaction.deferPublicResponse()
                commandHandler.handle(command, info) {
                    ack.respond { content = it }
                }
            }

            kord.on<MessageCreateEvent> {
                if (!message.content.startsWith('!')) {
                    return@on
                }
                val command = message.content.command()
                val guildId = message.getGuild().id
                val info  = CommandInfo(
                    messageContent = message.content.substringAfter(' '),
                    messageAuthor = message.author.displayUsername(),
                    guildId = guildId.value,
                    voiceChannelId = message.author?.asMember(guildId)?.getVoiceStateOrNull()?.channelId?.value
                )

                commandHandler.handle(command, info) {
                    this.message.channel.createMessage(it)
                }
            }


            logger.info("Lagbot loaded")

            kord.login {
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }
}